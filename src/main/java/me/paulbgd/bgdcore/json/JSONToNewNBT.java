package me.paulbgd.bgdcore.json;

import java.util.Arrays;
import java.util.Map;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.jnbt.ByteArrayTag;
import org.jnbt.CompoundTag;
import org.jnbt.IntArrayTag;
import org.jnbt.Tag;

public class JSONToNewNBT {

    public static JSONObject getJSON(CompoundTag tag) {
        return (JSONObject) getJSON((Tag) tag);
    }

    private static Object getJSON(Tag tag) {
        if (tag instanceof ByteArrayTag) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("array", "byte");
            JSONArray jsonArray = new JSONArray();
            jsonArray.addAll(Arrays.asList((byte[]) tag.getValue()));
            jsonObject.put("value", jsonArray);
            return jsonObject;
        } else if (tag instanceof IntArrayTag) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("array", "int");
            JSONArray jsonArray = new JSONArray();
            jsonArray.addAll(Arrays.asList((int[]) tag.getValue()));
            jsonObject.put("value", jsonArray);
            return jsonObject;
        } else if (tag instanceof CompoundTag) {
            Map<String, Tag> map = (Map<String, Tag>) tag.getValue();
            JSONObject jsonObject = new JSONObject();
            for (Map.Entry<String, Tag> stringTagEntry : map.entrySet()) {
                jsonObject.put(stringTagEntry.getKey(), getJSON(stringTagEntry.getValue()));
            }
            return jsonObject;
        } else {
            return tag.getValue();
        }
    }

}
