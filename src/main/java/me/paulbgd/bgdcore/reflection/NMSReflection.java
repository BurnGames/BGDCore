package me.paulbgd.bgdcore.reflection;

public class NMSReflection {

    // util
    public static ReflectionClass craftMagicNumbers = Reflection.getCBSClass("util.CraftMagicNumbers");

    // blocks
    public static ReflectionClass nmsBlock = Reflection.getNMSClass("Block");
    public static ReflectionClass tileEntity = Reflection.getNMSClass("TileEntity");

    // world
    public static ReflectionClass nmsWorld = Reflection.getNMSClass("World");

    // items
    public static ReflectionClass craftItemStack = Reflection.getCBSClass("inventory.CraftItemStack");
    public static ReflectionClass nmsItemStack = Reflection.getNMSClass("ItemStack");

    // nbt
    public static ReflectionClass nbtCompressedStreamTools = Reflection.getNMSClass("NBTCompressedStreamTools");

    public static ReflectionClass nbtBase = Reflection.getNMSClass("NBTBase");
    public static ReflectionClass nbtTagCompound = Reflection.getNMSClass("NBTTagCompound");
    public static ReflectionClass nbtTagList = Reflection.getNMSClass("NBTTagList");
    public static ReflectionClass nbtTagIntArray = Reflection.getNMSClass("NBTTagIntArray");
    public static ReflectionClass nbtTagByteArray = Reflection.getNMSClass("NBTTagByteArray");
    public static ReflectionClass nbtTagString = Reflection.getNMSClass("NBTTagString");
    public static ReflectionClass nbtTagDouble = Reflection.getNMSClass("NBTTagDouble");
    public static ReflectionClass nbtTagFloat = Reflection.getNMSClass("NBTTagFloat");
    public static ReflectionClass nbtTagLong = Reflection.getNMSClass("NBTTagLong");
    public static ReflectionClass nbtTagInt = Reflection.getNMSClass("NBTTagInt");
    public static ReflectionClass nbtTagShort = Reflection.getNMSClass("NBTTagShort");
    public static ReflectionClass nbtTagByte = Reflection.getNMSClass("NBTTagByte");

}
