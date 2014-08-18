package me.paulbgd.bgdcore.io.json;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import org.apache.commons.io.IOUtils;

public class JSONOutputStream extends GZIPOutputStream {

    public JSONOutputStream(JSONObject jsonObject, OutputStream out) throws IOException {
        super(out);

        write(jsonObject.toJSONString(JSONStyle.MAX_COMPRESS));
    }

    public JSONOutputStream(JSONArray jsonArray, OutputStream outputStream) throws IOException {
        super(outputStream);

        write(jsonArray.toJSONString(JSONStyle.MAX_COMPRESS));
    }

    private void write(String string) throws IOException {
        IOUtils.write(string, this);
    }

}
