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
import me.paulbgd.bgdcore.json.JSONTidier;
import me.paulbgd.bgdcore.reflection.ReflectionClass;
import me.paulbgd.bgdcore.reflection.ReflectionField;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import net.minidev.json.JSONValue;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
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
            Object currentValue = checkFieldValue(field.getValue().getObject());
            if (!jsonObject.containsKey(field.getName())) {
                jsonObject.put(field.getName(), currentValue);
            } else {
                Object configValue = jsonObject.get(field.getName());
                if (!currentValue.equals(entry.getValue()) && configValue.equals(entry.getValue())) {
                    // the config contains the old value and we have a new one to set
                    jsonObject.put(field.getName(), currentValue);
                } else if (!configValue.equals(entry.getValue())) {
                    field.setValue(checkValue(configValue));
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
        if (object instanceof List) {
            List<?> list = (List<?>) object;
            object = new JSONArray();
            for (int i = 0, object1Size = ((JSONArray) object).size(); i < object1Size; i++) {
                ((JSONArray) object).set(i, checkFieldValue(list.get(i)));
            }
        } else if (object instanceof Location) {
            Location location = (Location) object;
            JSONObject newLocation = new JSONObject();
            newLocation.put("type", "location");
            newLocation.put("world", location.getWorld().getName());
            newLocation.put("x", location.getX());
            newLocation.put("y", location.getY());
            newLocation.put("z", location.getZ());
            if (location.getYaw() != 0.0f) {
                newLocation.put("yaw", location.getYaw());
            }
            if (location.getPitch() != 0.0f) {
                newLocation.put("pitch", location.getPitch());
            }
            object = newLocation;
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
                            String world = (String) json.get("world");
                            double x = Double.parseDouble(json.get("x").toString());
                            double y = Double.parseDouble(json.get("y").toString());
                            double z = Double.parseDouble(json.get("z").toString());
                            float yaw = 0.0f;
                            float pitch = 0.0f;
                            if (json.containsKey("yaw")) {
                                yaw = Float.parseFloat(json.get("yaw").toString());
                            }
                            if (json.containsKey("pitch")) {
                                pitch = Float.parseFloat(json.get("pitch").toString());
                            }
                            object = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
                            break;
                        case "vector":
                            x = Double.parseDouble(json.get("x").toString());
                            y = Double.parseDouble(json.get("y").toString());
                            z = Double.parseDouble(json.get("z").toString());
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
