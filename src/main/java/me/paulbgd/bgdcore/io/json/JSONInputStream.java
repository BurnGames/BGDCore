package me.paulbgd.bgdcore.io.json;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

public class JSONInputStream extends GZIPInputStream {

    public JSONInputStream(InputStream in) throws IOException {
        super(in);
    }

    public JSONObject readObject() {
        return (JSONObject) readJSON();
    }

    public JSONArray readArray() {
        return (JSONArray) readJSON();
    }

    private Object readJSON() {
        try {
            return JSONValue.parse(this);
        } catch (Exception e) {
            return null;
        }
    }

}
