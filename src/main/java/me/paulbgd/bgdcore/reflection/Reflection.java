package me.paulbgd.bgdcore.reflection;

import com.google.common.primitives.Primitives;
import lombok.Getter;
import org.bukkit.Bukkit;

public class Reflection {

    @Getter
    private static final String version;
    @Getter
    private static final String cbsPath;
    @Getter
    private static final String nmsPath;

    static {
        version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        cbsPath = "org.bukkit.craftbukkit." + version;
        nmsPath = "net.minecraft.server." + version;
    }

    public static Class<?>[] objectsToClassArray(Object[] objects) {
        Class<?>[] classes = new Class[objects.length];
        for (int i = 0; i < objects.length; i++) {
            Class<?> clazz = objects[i].getClass();
            // due to how java works, we have to convert to primitives. Guava makes this easy
            if (Primitives.isWrapperType(clazz)) {
                clazz = Primitives.unwrap(clazz);
            }
            classes[i] = clazz;
        }
        return classes;
    }

    public static ReflectionClass getClass(String name) {
        try {
            return new ReflectionClass(Class.forName(name));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ReflectionClass getNMSClass(String name) {
        return getClass(nmsPath + "." + name);
    }

    public static ReflectionClass getCBSClass(String name) {
        return getClass(cbsPath + "." + name);
    }

}
