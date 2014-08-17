package me.paulbgd.bgdcore.nms;

import java.util.Map;
import java.util.WeakHashMap;
import lombok.Getter;
import me.paulbgd.bgdcore.BGDCore;
import me.paulbgd.bgdcore.nms.versions.v1_7_3_R3;
import me.paulbgd.bgdcore.reflection.Reflection;

public class NMSManager {

    @Getter
    private static final BGDNMS nms;

    static {
        // these are here so that we can discard the references when this is all over
        Class<?>[] registered = new Class<?>[]{v1_7_3_R3.class};
        WeakHashMap<Integer, Class<?>> versions = new WeakHashMap<>(registered.length); // weakling

        for (Class<?> aClass : registered) {
            versions.put(aClass.getSimpleName().hashCode(), aClass);
        }
        String version = Reflection.getVersion();
        Class<?> nmsClass = null;
        try {
            nmsClass = Class.forName("me.paulbgd.bgdcore.nms.versions." + version);
        } catch (ClassNotFoundException e) {
            // let's sliiide back to find the closest version
            int hash = version.hashCode();
            int highest = -1;
            for (Map.Entry<Integer, Class<?>> entry : versions.entrySet()) {
                if (entry.getKey() > highest && entry.getKey() < hash) {
                    nmsClass = entry.getValue();
                    highest = entry.getKey();
                }
            }
            if (nmsClass == null) {
                nmsClass = registered[registered.length - 1];
            }
        }
        BGDNMS toSet = null;
        try {
            toSet = nmsClass.asSubclass(BGDNMS.class).newInstance();
        } catch (InstantiationException | IllegalAccessException e1) {
            BGDCore.getLogging().severe("Failed to initialize NMS! " + e1.getClass().getSimpleName());
        }
        nms = toSet;
        if (nms == null) {
            BGDCore.getLogging().severe("Failed to load NMS version " + version + "! Things may not work right.");
        } else {
            BGDCore.getLogging().info("Loaded NMS version " + nmsClass.getSimpleName() + "!");
        }
    }

}
