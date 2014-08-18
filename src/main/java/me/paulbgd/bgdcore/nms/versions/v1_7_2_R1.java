package me.paulbgd.bgdcore.nms.versions;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import me.paulbgd.bgdcore.items.TransitionItem;
import me.paulbgd.bgdcore.nms.BGDNMS;
import me.paulbgd.bgdcore.reflection.NMSReflection;
import me.paulbgd.bgdcore.reflection.ReflectionMethod;
import me.paulbgd.bgdcore.reflection.ReflectionObject;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

public class v1_7_2_R1 implements BGDNMS {

    @Override
    public boolean setBlock(World world, int x, int y, int z, Material type, short data) {
        ReflectionObject nmsWorld = new ReflectionObject(world).getMethod("getHandle").invoke();
        ReflectionMethod setTypeAndData = nmsWorld.getMethod("setTypeAndData", int.class, int.class, int.class, NMSReflection.nmsBlock.getClazz(), int.class, int.class);
        ReflectionObject block = NMSReflection.craftMagicNumbers.getStaticMethod("getBlock", int.class).invoke(type.getId());
        return (boolean) setTypeAndData.invoke(x, y, z, block, (int) data, 2).getObject();
    }

    @Override
    public TransitionItem getItem(ItemStack itemStack) {
        TransitionItem transitionItem = new TransitionItem();
        ReflectionObject nmsItem = NMSReflection.craftItemStack.getStaticMethod("asNMSCopy", itemStack).invoke(itemStack);
        ReflectionObject nbt = NMSReflection.nbtTagCompound.newInstance();
        nmsItem.getMethod("save", nbt.getObject()).invoke(nbt.getObject());
        JSONObject jsonObject = nbtToJSON(nbt.getObject());
        transitionItem.setJsonObject(jsonObject);
        return transitionItem;
    }

    @Override
    public ItemStack getBukkitItem(TransitionItem transitionItem) {
        Object nbt = jsonToNBT(transitionItem.getJsonObject());
        ReflectionObject nmsItemStack = NMSReflection.nmsItemStack.newInstance();
        nmsItemStack.getMethodByClasses("c", NMSReflection.nbtTagCompound.getClazz()).invoke(nbt);
        Object itemStack = NMSReflection.craftItemStack.newInstance(nmsItemStack.getObject()).getObject();
        return (ItemStack) itemStack;
    }

    @Override
    public Object jsonToNBT(JSONObject jsonObject) {
        return loadNBTFromJSON(jsonObject);
    }

    private Object loadNBTFromJSON(Object object) {
        if (object instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) object;
            if (jsonObject.containsKey("nbtTagList")) {
                JSONArray jsonArray = (JSONArray) jsonObject.get("nbtTagList");
                ReflectionObject nbtTagList = NMSReflection.nbtTagList.newInstance();
                ReflectionMethod add = nbtTagList.getMethodByClasses("add", NMSReflection.nbtBase.getClazz());
                for (Object adding : jsonArray) {
                    add.invoke(loadNBTFromJSON(adding));
                }
                return nbtTagList.getObject();
            } else if (jsonObject.containsKey("nbtTagIntArray")) {
                JSONArray jsonArray = (JSONArray) jsonObject.get("nbtTagIntArray");
                int[] ints = new int[jsonArray.size()];
                for (int i = 0, jsonArraySize = jsonArray.size(); i < jsonArraySize; i++) {
                    ints[i] = (Integer) jsonArray.get(i);
                }
                return NMSReflection.nbtTagIntArray.newInstance(new Object[]{ints}).getObject();
            } else if (jsonObject.containsKey("nbtTagByteArray")) {
                JSONArray jsonArray = (JSONArray) jsonObject.get("nbtTagByteArray");
                byte[] ints = new byte[jsonArray.size()];
                for (int i = 0, jsonArraySize = jsonArray.size(); i < jsonArraySize; i++) {
                    ints[i] = (Byte) jsonArray.get(i);
                }
                return NMSReflection.nbtTagByteArray.newInstance(new Object[]{ints}).getObject();
            } else {
                ReflectionObject nbtTagCompound = NMSReflection.nbtTagCompound.newInstance();
                ReflectionMethod set = nbtTagCompound.getMethodByClasses("set", String.class, NMSReflection.nbtBase.getClazz());
                for (Object entry1 : jsonObject.entrySet()) {
                    Map.Entry<?, ?> entry = (Map.Entry<?, ?>) entry1;
                    set.invoke(entry.getKey(), loadNBTFromJSON(entry.getValue()));
                }
                return nbtTagCompound.getObject();
            }
        } else if (object instanceof String) {
            return NMSReflection.nbtTagString.newInstance(object).getObject();
        } else if (object instanceof Double) {
            return NMSReflection.nbtTagDouble.newInstance(object).getObject();
        } else if (object instanceof Float) {
            return NMSReflection.nbtTagFloat.newInstance(object).getObject();
        } else if (object instanceof Long) {
            return NMSReflection.nbtTagLong.newInstance(object).getObject();
        } else if (object instanceof Integer) {
            return NMSReflection.nbtTagInt.newInstance(object).getObject();
        } else if (object instanceof Short) {
            return NMSReflection.nbtTagShort.newInstance(object).getObject();
        } else if (object instanceof Byte) {
            return NMSReflection.nbtTagByte.newInstance(object).getObject();
        } else {
            return NMSReflection.nbtTagString.newInstance(object.toString()).getObject();
        }
    }

    @Override
    public JSONObject nbtToJSON(Object nbt) {
        return (JSONObject) loadObjectFromNBT(nbt);
    }

    private Object loadObjectFromNBT(Object nbt) {
        ReflectionObject reflection = new ReflectionObject(nbt);
        switch (nbt.getClass().getSimpleName()) {
            case "NBTTagList":
                JSONObject tagList = new JSONObject();
                JSONArray tagListArray = new JSONArray();
                List<?> list = (List<?>) reflection.getField("list").getValue().getObject();
                for (Object base : list) {
                    tagListArray.add(loadObjectFromNBT(base));
                }
                tagList.put("nbtTagList", tagListArray);
                return tagList;
            case "NBTTagCompound":
                JSONObject compoundObject = new JSONObject();
                Set<?> keys = (Set<?>) reflection.getMethod("c").invoke().getObject();
                ReflectionMethod get = reflection.getMethodByClasses("get", String.class);
                for (Object key : keys) {
                    compoundObject.put((String) key, loadObjectFromNBT(get.invoke(key).getObject()));
                }
                return compoundObject;
            case "NBTTagIntArray":
                JSONArray tagIntArray = new JSONArray();
                tagIntArray.addAll(Arrays.asList((int[]) reflection.getMethod("c").invoke().getObject()));
                JSONObject intArray = new JSONObject();
                intArray.put("nbtTagIntArray", tagIntArray);
                return intArray;
            case "NBTTagByteArray":
                JSONArray tagByteArray = new JSONArray();
                tagByteArray.addAll(Arrays.asList((byte[]) reflection.getMethod("c").invoke().getObject()));
                JSONObject byteArray = new JSONObject();
                byteArray.put("nbtTagByteArray", tagByteArray);
                return byteArray;
            case "NBTTagString":
                return reflection.getMethod("a_").invoke().getObject();
            case "NBTTagDouble":
                return reflection.getMethod("g").invoke().getObject();
            case "NBTTagFloat":
                return reflection.getMethod("h").invoke().getObject();
            case "NBTTagLong":
                return reflection.getMethod("c").invoke().getObject();
            case "NBTTagInt":
                return reflection.getMethod("d").invoke().getObject();
            case "NBTTagShort":
                return reflection.getMethod("e").invoke().getObject();
            case "NBTTagByte":
                return reflection.getMethod("f").invoke().getObject();
            default:
                // hmm
                return nbt.toString();
        }
    }

}
