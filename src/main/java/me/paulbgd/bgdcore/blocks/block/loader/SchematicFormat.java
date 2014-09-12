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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Data;
import me.paulbgd.bgdcore.blocks.block.Block;
import me.paulbgd.bgdcore.blocks.block.BlockPosition;
import me.paulbgd.bgdcore.blocks.block.Blocks;
import me.paulbgd.bgdcore.blocks.block.data.BlockData;
import me.paulbgd.bgdcore.nms.NMSManager;
import me.paulbgd.bgdcore.reflection.NMSReflection;
import me.paulbgd.bgdcore.reflection.ReflectionMethod;
import me.paulbgd.bgdcore.reflection.ReflectionObject;
import net.minidev.json.JSONObject;
import org.jnbt.ByteArrayTag;
import org.jnbt.CompoundTag;
import org.jnbt.IntTag;
import org.jnbt.ListTag;
import org.jnbt.NBTInputStream;
import org.jnbt.NBTOutputStream;
import org.jnbt.ShortTag;
import org.jnbt.Tag;

/**
 * Loads a Schematic into a Blocks object, used for conversion. No save method at this time.
 */
public class SchematicFormat implements BlocksLoader {

    @Override
    public String getName() {
        return "schematic";
    }

    @Override
    public Blocks load(InputStream inputStream) throws IOException {
        NBTInputStream nbtInputStream = new NBTInputStream(inputStream);
        Tag schematic = nbtInputStream.readTag();
        if (!schematic.getName().equals("Schematic")) {
            System.out.println("Invalid schematic!");
            return new Blocks();
        }
        Map<String, Tag> nbt = (Map<String, Tag>) schematic.getValue();
        int xLength = ((ShortTag) nbt.get("Width")).getValue(), yLength = ((ShortTag) nbt.get("Height")).getValue(), zLength = ((ShortTag) nbt.get("Length")).getValue();
        byte[] ids = ((ByteArrayTag) nbt.get("Blocks")).getValue();
        byte[] datas = ((ByteArrayTag) nbt.get("Data")).getValue();
        byte[] addId = new byte[0];
        short[] blocks = new short[ids.length];
        int startX = getInt(nbt, "WEOffsetX");
        int startY = getInt(nbt, "WEOffsetY");
        int startZ = getInt(nbt, "WEOffsetZ");
        if (nbt.containsKey("AddBlocks")) {
            addId = ((ByteArrayTag) nbt.get("AddBlocks")).getValue();
        }
        // load block ids and data first
        for (int index = 0, idsLength = ids.length; index < idsLength; index++) {
            if ((index >> 1) >= addId.length) { // No corresponding AddBlocks index
                blocks[index] = (short) (ids[index] & 0xFF);
            } else {
                if ((index & 1) == 0) {
                    blocks[index] = (short) (((addId[index >> 1] & 0x0F) << 8) + (ids[index] & 0xFF));
                } else {
                    blocks[index] = (short) (((addId[index >> 1] & 0xF0) << 4) + (ids[index] & 0xFF));
                }
            }
        }
        if (nbt.containsKey("Entities") && ((ListTag) nbt.get("Entities")).getValue().size() > 0) {
            System.out.println("Found entities stored in schematic - current version of Blocks has no support for them.");
        }
        List<DataBuilder> loadedBlocks = new ArrayList<>(ids.length);
        for (int x = 0; x < xLength; ++x) {
            for (int y = 0; y < yLength; ++y) {
                for (int z = 0; z < zLength; ++z) {
                    int index = y * xLength * zLength + z * xLength + x;
                    DataBuilder dataBuilder = new DataBuilder();
                    dataBuilder.setId(blocks[index]);
                    dataBuilder.setData(datas[index]);
                    dataBuilder.setPosition(new BlockPosition(x + startX, y + startY, z + startZ));
                    loadedBlocks.add(dataBuilder);
                }
            }
        }
        if (nbt.containsKey("TileEntities")) {
            List<Tag> tileEntities = ((ListTag) nbt.get("TileEntities")).getValue();
            for (Tag tileEntity1 : tileEntities) {
                CompoundTag tileEntity = (CompoundTag) tileEntity1;
                Map<String, Tag> tileData = tileEntity.getValue();
                BlockPosition blockPosition = new BlockPosition(getInt(tileData, "x") + startX, getInt(tileData, "y") + startY, getInt(tileData, "z") + startZ);
                JSONObject tileJson = new JSONObject();
                DataBuilder dataBuilder = null;
                for (DataBuilder builder : loadedBlocks) {
                    if (builder.getPosition().equals(blockPosition)) {
                        dataBuilder = builder;
                        break;
                    }
                }
                if (dataBuilder == null) {
                    System.out.println("Invalid location for TileEntity: " + blockPosition);
                    continue;
                }
                tileJson.put("e", String.valueOf(dataBuilder.getData()));
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                NBTOutputStream nbtOutputStream = new NBTOutputStream(byteArrayOutputStream);
                nbtOutputStream.writeTag(tileEntity);
                ReflectionMethod a = NMSReflection.nbtCompressedStreamTools.getStaticMethod("a", InputStream.class);
                ReflectionObject tileEntityNBT = a.invoke(byteArrayOutputStream);
                tileJson.put("n", NMSManager.getNms().nbtToJSON(tileEntityNBT));
                dataBuilder.setData(tileJson);
            }
        }
        // time to build!
        Blocks blocksObject = new Blocks();
        for (DataBuilder loadedBlock : loadedBlocks) {
            blocksObject.add(new Block(loadedBlock.getPosition(), BlockData.loadData(loadedBlock.getId(), loadedBlock.getData())));
        }
        return blocksObject;
    }

    private int getInt(Map<String, Tag> map, String name) {
        return map.containsKey(name) ? ((IntTag) map.get(name)).getValue() : 0;
    }

    @Override
    public void save(Blocks blocks, OutputStream outputStream) throws IOException {
        throw new IllegalArgumentException("We do not support saving to schematics at this time.");
       /*HashMap<String, Tag> tagHashMap = new HashMap<>();
        int height = blocks.getHeight() + Math.abs(blocks.getMinY()), length = blocks.getLength() + Math.abs(blocks.getMinZ()), width = blocks.getWidth() + Math.abs(blocks.getMinX());
        System.out.println(height + " " + length + " " + width);
        System.out.println(blocks.getMinX() + " " + blocks.getMinY() + " " + blocks.getMinZ() + " : " + blocks.getMaxX() + " " + blocks.getMaxY() + " " + blocks.getMaxZ());
        tagHashMap.put("Width", new ShortTag("Width", (short) width));
        tagHashMap.put("Height", new ShortTag("Height", (short) height));
        tagHashMap.put("Length", new ShortTag("Length", (short) length));
        tagHashMap.put("Materials", new StringTag("Materials", "Alpha"));
        byte[] ids = new byte[width * height * length];
        byte[] addBlocks = null;
        byte[] blockData = new byte[width * height * length];
        ArrayList<Tag> tileEntities = new ArrayList<>();
        System.out.println("Saving " + ids.length + " blocks");

        for (Block block : blocks) {
            BlockPosition position = block.getPosition();
            int x = position.getRelativeX() + Math.abs(blocks.getMinX());
            int y = position.getRelativeY() + Math.abs(blocks.getMinY());
            int z = position.getRelativeZ() + Math.abs(blocks.getMinZ());
            System.out.println(x + " " + y + " " + z);
            int index = y * width * length + z * width + x;
            BlockData data = block.getData();
            if (data.getId() > 255) {
                if (addBlocks == null) { // Lazily create section - <3 sk89k
                    addBlocks = new byte[(ids.length >> 1) + 1];
                }
                addBlocks[index >> 1] = (byte) (((index & 1) == 0) ? addBlocks[index >> 1] & 0xF0 | (data.getId() >> 8) & 0xF : addBlocks[index >> 1] & 0xF | ((data.getId() >> 8) & 0xF) << 4);
            }
            ids[index] = (byte) data.getId();
            blockData[index] = (byte) data.getBlockData();
            if (data.getId() != 0) {
                System.out.println("Stored " + Material.getMaterial(data.getId()) + "!");
            }
            if (data instanceof ComplexBlockData) {
                // tile entity
                CompoundTag compoundTag = ((ComplexBlockData) data).getNBT();
                Map<String, Tag> value = compoundTag.getValue();
                value.put("x", new IntTag("x", x));
                value.put("y", new IntTag("y", y));
                value.put("z", new IntTag("z", z));
                tileEntities.add(compoundTag);
            }
        }

        tagHashMap.put("Blocks", new ByteArrayTag("Blocks", ids));
        tagHashMap.put("Data", new ByteArrayTag("Data", blockData));
        tagHashMap.put("Entities", new ListTag("Entities", CompoundTag.class, new ArrayList<Tag>()));
        tagHashMap.put("TileEntities", new ListTag("TileEntities", CompoundTag.class, tileEntities));
        if (addBlocks != null) {
            tagHashMap.put("AddBlocks", new ByteArrayTag("AddBlocks", addBlocks));
        }

        NBTOutputStream nbtOutputStream = new NBTOutputStream(outputStream);
        CompoundTag schematic = new CompoundTag("Schematic", tagHashMap);
        nbtOutputStream.writeTag(schematic);
        nbtOutputStream.close();*/
    }

    @Data
    private final class DataBuilder {
        private int id;
        private Object data = (short) 0;
        private BlockPosition position;
    }

}
