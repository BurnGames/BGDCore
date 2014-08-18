package me.paulbgd.bgdcore.configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import lombok.Getter;
import me.paulbgd.bgdcore.BGDCore;
import me.paulbgd.bgdcore.json.JSONLocation;
import me.paulbgd.bgdcore.json.JSONTidier;
import me.paulbgd.bgdcore.reflection.ReflectionClass;
import me.paulbgd.bgdcore.reflection.ReflectionField;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import net.minidev.json.JSONValue;
import org.apache.commons.io.FileUtils;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class ConfigurationFile {

    @Getter
    private final File file;
    private final HashMap<ReflectionField, Object> previous = new HashMap<>();
    private final ConfigurationType configurationType;

    public ConfigurationFile(File file) {
        this(file, ConfigurationType.STATIC);
    }

    public ConfigurationFile(File file, ConfigurationType configurationType) {
        this.file = file;
        this.configurationType = configurationType;
        try {
            // we need to call the following in the reverse order to fill our hashmap
            updateDefaults();
            updateJSON();
        } catch (Exception e) {
            BGDCore.getLogging().log(Level.SEVERE, "Failed to update configuration to file \"" + file.getAbsolutePath() + "\"!");
            e.printStackTrace();
        }
    }

    public void update() throws Exception {
        updateJSON();
        updateDefaults();
    }

    private void updateJSON() throws Exception {
        JSONObject jsonObject = null;
        if (!file.exists() && !file.createNewFile()) {
            throw new FileNotFoundException("Failed to create file \"" + file.getAbsolutePath() + "\"!");
        } else {
            String json = FileUtils.readFileToString(file);
            if (json != null && !json.equals("") && !json.equals(" ")) {
                jsonObject = (JSONObject) JSONValue.parse(json); // reload from file
            }
        }
        if (jsonObject == null) {
            jsonObject = new JSONObject();
        }
        for (Map.Entry<ReflectionField, Object> entry : previous.entrySet()) {
            ReflectionField field = entry.getKey();
            Object value = entry.getValue();
            Object currentValue = checkFieldValue(field.getValue().getObject());
            if (!jsonObject.containsKey(field.getName())) {
                jsonObject.put(field.getName(), currentValue);
            } else {
                Object configValue = jsonObject.get(field.getName());
                // to allow us to use .equals on null, we'll use a little hack
                if (currentValue == null) {
                    currentValue = "null";
                }
                if (value == null) {
                    value = "null";
                }
                if (configValue == null) {
                    configValue = "null";
                }
                if (!currentValue.equals(entry.getValue()) && configValue.equals(value)) {
                    // the config contains the old value and we have a new one to set
                    jsonObject.put(field.getName(), currentValue.equals("null") ? null : currentValue);
                    System.out.println("Saving " + getClass().getName() + "." + field.getName() + " to file");
                    if (currentValue instanceof List) {
                        System.out.println("Contains " + ((List) currentValue).size() + " elements!");
                    }
                } else if (!configValue.equals(value)) {
                    field.setValue(checkValue(configValue.equals("null") ? null : null));
                }
            }
        }
        try {
            FileUtils.write(file, JSONTidier.tidyJSON(jsonObject.toJSONString(JSONStyle.NO_COMPRESS)));
        } catch (Exception e) {
            BGDCore.getLogging().warning("Failed to save \"" + file.getAbsolutePath() + "\" to file!");
            e.printStackTrace();
        }
    }

    private void updateDefaults() {
        // load to be previous fields
        for (Field field : getClass().getDeclaredFields()) {
            if (isValidField(field.getModifiers()) && !Modifier.isFinal(field.getModifiers())) {
                ReflectionField reflectionField = new ReflectionField(configurationType == ConfigurationType.STATIC ? null : this, field);
                previous.put(reflectionField, reflectionField.getValue().getObject());
            }
        }
    }

    private static Object checkFieldValue(Object object) {
        if (object == null) {
            return object;
        }
        if (object instanceof List) {
            System.out.println("Saving " + object.getClass().getName() + " with " + ((List) object).size() + " elements to file!");
            List<?> list = (List<?>) object;
            object = new JSONArray();
            for (Object o : list) {
                System.out.println("Converted to " + checkFieldValue(o) + "!");
                ((JSONArray) object).add(checkFieldValue(o));
            }
            System.out.println(((JSONArray) object).size() + " elements saved!");
        } else if (object instanceof Location) {
            object = new JSONLocation((Location) object);
        } else if (object instanceof Vector) {
            Vector vector = (Vector) object;
            JSONObject newVector = new JSONObject();
            newVector.put("type", "vector");
            newVector.put("x", vector.getX());
            newVector.put("y", vector.getY());
            newVector.put("z", vector.getZ());
            object = newVector;
        } else if (object instanceof Enum) {
            Enum anEnum = (Enum) object;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "enum");
            jsonObject.put("enum", anEnum.getClass().getName());
            jsonObject.put("value", anEnum.name());
            object = jsonObject;
        }
        return object;
    }

    private static Object checkValue(Object object) {
        if (object == null) {
            return object;
        }
        switch (object.getClass().getSimpleName()) {
            case "ArrayList":
            case "LinkedList":
                List list = (List) object;
                for (int i = 0, listSize = list.size(); i < listSize; i++) {
                    list.set(i, checkValue(list.get(i)));
                }
            case "JSONArray":
                JSONArray jsonArray = (JSONArray) object;
                List<Object> list2 = new ArrayList<>();
                for (Object o : jsonArray) {
                    list2.add(checkValue(o));
                }
                object = list2;
            case "JSONObject":
                JSONObject json = (JSONObject) object;
                if (json.containsKey("type")) {
                    switch ((String) json.get("type")) {
                        case "location":
                            object = new JSONLocation(json).getLocation();
                            break;
                        case "vector":
                            double x = Double.parseDouble(json.get("x").toString());
                            double y = Double.parseDouble(json.get("y").toString());
                            double z = Double.parseDouble(json.get("z").toString());
                            object = new Vector(x, y, z);
                            break;
                        case "enum":
                            try {
                                ReflectionClass enumClass = new ReflectionClass(Class.forName((String) json.get("enum")));
                                object = enumClass.getStaticMethod("valueOf", "string").invoke(json.get("value")).getObject();
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                            break;
                    }
                }
            default:
                return object;
        }
    }

    private boolean isValidField(int modifiers) {
        if (configurationType == ConfigurationType.STATIC) {
            return Modifier.isStatic(modifiers);
        } else {
            return !Modifier.isStatic(modifiers);
        }
    }

    public static enum ConfigurationType {
        OBJECT, STATIC;
    }

}
