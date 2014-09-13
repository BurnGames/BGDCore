package me.paulbgd.bgdcore.nms;

import java.util.Map;
import java.util.WeakHashMap;
import lombok.Getter;
import me.paulbgd.bgdcore.BGDCore;
import me.paulbgd.bgdcore.nms.versions.v1_7_R1;
import me.paulbgd.bgdcore.nms.versions.v1_7_R4;
import me.paulbgd.bgdcore.reflection.Reflection;

public class NMSManager {

    @Getter
    private static final BGDNMS nms;

    static {
        // these are here so that we can discard the references when this is all over
        Class<?>[] registered = new Class<?>[]{v1_7_R1.class, v1_7_R4.class};
        WeakHashMap<Integer, Class<?>> versions = new WeakHashMap<>(registered.length); // weakling

        for (Class<?> aClass : registered) {
            versions.put(getVersion(aClass.getSimpleName()), aClass);
        }
        String version = Reflection.getVersion();
        Class<?> nmsClass = null;
        try {
            nmsClass = Class.forName("me.paulbgd.bgdcore.nms.versions." + version);
        } catch (ClassNotFoundException e) {
            // let's sliiide back to find the closest version
            int hash = getVersion(version), highest = Integer.MIN_VALUE;
            for (Map.Entry<Integer, Class<?>> entry : versions.entrySet()) {
                if (entry.getKey() - hash <= 0 && entry.getKey() - hash > highest) {
                    nmsClass = entry.getValue();
                    highest = entry.getKey() - hash;
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

    private static int getVersion(String string) {
        if (string == null) {
            return 0;
        }
        int i = 1;
        for (char c : string.toCharArray()) {
            if (Character.isDigit(c) && c != '0') {
                i *= Integer.parseInt(Character.toString(c));
            }
        }
        return i == 1 ? 0 : i;
    }

}
