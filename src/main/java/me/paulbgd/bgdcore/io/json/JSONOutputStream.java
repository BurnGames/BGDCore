package me.paulbgd.bgdcore.io.json;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.commons.io.IOUtils;

public class JSONOutputStream extends GZIPOutputStream {

    public JSONOutputStream(JSONObject jsonObject, OutputStream out) throws IOException {
        super(out);

        write(jsonObject.toJSONString());
    }

    public JSONOutputStream(JSONArray jsonArray, OutputStream outputStream) throws IOException {
        super(outputStream);

        write(jsonArray.toJSONString());
    }

    private void write(String string) throws IOException {
        IOUtils.write(string, this);
    }

}
