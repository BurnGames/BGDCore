package me.paulbgd.bgdcore.player;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import me.paulbgd.bgdcore.reflection.ReflectionField;
import net.minidev.json.JSONObject;

public class PluginPlayerData {

    private final HashMap<String, ReflectionField> fields = new HashMap<>();
    private boolean loaded = false;

    void load(JSONObject data) {
        checkLoad();
        for (Map.Entry<String, ReflectionField> entry : fields.entrySet()) {
            if (data.containsKey(entry.getKey()) && data.get(entry.getKey()) != null) {
                entry.getValue().setValue(data.get(entry.getKey()));
            }
        }
    }

    JSONObject save() {
        checkLoad();
        JSONObject data = new JSONObject();
        for (Map.Entry<String, ReflectionField> entry : fields.entrySet()) {
            data.put(entry.getKey(), entry.getValue().getValue().getObject());
        }
        return data;
    }

    private void checkLoad() {
        if (!loaded) {
            loaded = true;
            for (Field field : getClass().getDeclaredFields()) {
                if (field.getName().equals("fields") || field.getName().equals("loaded")) {
                    continue;
                }
                fields.put(field.getName(), new ReflectionField(this, field));
            }
        }
    }

}
