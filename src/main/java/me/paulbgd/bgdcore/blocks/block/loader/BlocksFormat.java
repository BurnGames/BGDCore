/*
 * COPYRIGHT AND PERMISSION NOTICE
 *
 * Copyright (c) 2014, PaulBGD, <paul@paulbgd.me>.
 *
 * All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software for any purpose
 * with or without fee is hereby granted, provided that the above copyright
 * notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT OF THIRD PARTY RIGHTS. IN
 * NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Except as contained in this notice, the name of a copyright holder shall not
 * be used in advertising or otherwise to promote the sale, use or other dealings
 * in this Software without prior written authorization of the copyright holder.
 */

package me.paulbgd.bgdcore.blocks.block.loader;

import com.google.common.base.Joiner;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.paulbgd.bgdcore.blocks.block.Block;
import me.paulbgd.bgdcore.blocks.block.BlockPosition;
import me.paulbgd.bgdcore.blocks.block.Blocks;
import me.paulbgd.bgdcore.blocks.block.data.BlockData;
import me.paulbgd.bgdcore.blocks.block.data.SimpleBlockData;
import me.paulbgd.bgdcore.io.ZipUtils;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.block.Biome;

/**
 * The official loader and saver of the Blocks format.
 */
public class BlocksFormat implements BlocksLoader {

    /**
     * The chunk size to save in each file. Used like a buffer.
     * Minecraft uses 65536 for it's chunks, so we'll steal that.
     */
    private static final int chunkSize = 65536;

    /**
     * Saves a list of blocks into a byte array
     *
     * @param blocks to save
     * @return blocks as bytes
     */
    private static byte[] saveChunk(List<Block> blocks) throws UnsupportedEncodingException {
        if (blocks.isEmpty()) {
            return new byte[0];
        }
        JSONObject chunk = new JSONObject();
        chunk.put("s", blocks.size());
        for (Block block : blocks) {
            BlockPosition blockPosition = block.getPosition();
            BlockData blockData = block.getData();
            String stringedId = Integer.toString(blockData.getId());
            JSONArray jsonArray;
            if (!chunk.containsKey(stringedId)) {
                chunk.put(stringedId, new JSONArray());
            }
            jsonArray = (JSONArray) chunk.get(stringedId);
            String key = Joiner.on('!').join(new Object[]{blockPosition.getRelativeX(), blockPosition.getRelativeY(), blockPosition.getRelativeZ()});
            Object data = blockData.getData();
            if (blockData instanceof SimpleBlockData && (short) data == 0) { // let's save space! No need for data if we don't need it
                jsonArray.add(key); // simply add the key!
            } else {
                JSONObject value = new JSONObject();
                value.put("l", key);
                value.put("d", data);
                jsonArray.add(value);
            }
        }
        return chunk.toJSONString().getBytes("UTF-8");
    }

    @Override
    public String getName() {
        return "blocks";
    }

    @Override
    public Blocks load(InputStream inputStream) throws IOException {
        Blocks blocks = new Blocks();
        HashMap<InputStream, String> files = ZipUtils.readZip(inputStream);
        IOUtils.closeQuietly(inputStream);

        HashMap<Biome, List<String>> biomes = new HashMap<>();
        for (Map.Entry<InputStream, String> file : files.entrySet()) {
            JSONObject parse = (JSONObject) JSONValue.parse(inputStream);
            if (parse == null) {
                continue;
            }
            for (Map.Entry<String, Object> entry : parse.entrySet()) {
                if (file.getValue().equals("b")) {
                    // biome data!
                    Biome biome = Biome.valueOf(entry.getKey());
                    JSONArray positions = (JSONArray) entry.getValue();
                    List<String> keys = biomes.get(biome);
                    if (keys == null) {
                        keys = new ArrayList<>();
                        biomes.put(biome, keys);
                    }
                    for (Object position : positions) {
                        keys.add((String) position);
                    }
                    continue;
                }
                if (!StringUtils.isNumeric(entry.getKey())) {
                    continue; // meh, not data
                }
                int id = Integer.valueOf(entry.getKey());
                JSONArray jsonArray = (JSONArray) entry.getValue();
                for (Object object : jsonArray) {
                    String key;
                    String data;
                    if (object instanceof JSONObject) {
                        JSONObject jsonObject = (JSONObject) object;
                        key = (String) jsonObject.get("l");
                        data = jsonObject.get("d").toString();
                    } else {
                        key = object.toString();
                        data = "0";
                    }
                    String[] position = key.split("!");
                    BlockPosition blockPosition = new BlockPosition(Integer.valueOf(position[0]), Integer.valueOf(position[1]), Integer.valueOf(position[2]));
                    Block block = new Block(blockPosition, BlockData.loadData(id, data));
                    blocks.add(block);
                }
            }
        }
        return blocks;
    }

    @Override
    public void save(Blocks blocks, OutputStream outputStream) throws IOException {
        List<Block> allBlocks = new ArrayList<>(blocks);
        int i = 0;
        int j = 0;
        HashMap<InputStream, String> outputStreams = new HashMap<>();
        List<Block> toSave = new ArrayList<>();
        while (!allBlocks.isEmpty()) {
            if (++j >= chunkSize) {
                outputStreams.put(new ByteArrayInputStream(saveChunk(toSave)), Integer.toString(i));
                toSave.clear();
                i++;
                j = 0;
            }
            toSave.add(allBlocks.remove(0));
        }
        // save last chunk, if there's a few leftovers
        outputStreams.put(new ByteArrayInputStream(saveChunk(toSave)), Integer.toString(i));
        // save biomes
        if (blocks.getBiomes().size() > 0) {
            JSONObject biomes = new JSONObject();
            for (Map.Entry<Biome, List<String>> entry : blocks.getBiomes().entrySet()) {
                biomes.put(entry.getKey().name(), entry.getValue());
            }
            outputStreams.put(new ByteArrayInputStream(biomes.toJSONString().getBytes("UTF-8")), "b");
        }

        ZipUtils.writeZip(outputStreams, outputStream);
    }
}
