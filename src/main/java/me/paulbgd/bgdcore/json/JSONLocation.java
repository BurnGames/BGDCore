package me.paulbgd.bgdcore.json;

import net.minidev.json.JSONObject;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class JSONLocation extends JSONObject {

    public JSONLocation(Location location) {
        put("type", "location");
        put("world", location.getWorld().getName());
        put("x", location.getX());
        put("y", location.getY());
        put("z", location.getZ());
        if (location.getYaw() != 0.0f) {
            put("yaw", location.getYaw());
        }
        if (location.getPitch() != 0.0f) {
            put("pitch", location.getPitch());
        }
    }

    public JSONLocation(JSONObject json) {
        putAll(json);
    }

    public Location getLocation() {
        String world = (String) get("world");
        double x = Double.parseDouble(get("x").toString());
        double y = Double.parseDouble(get("y").toString());
        double z = Double.parseDouble(get("z").toString());
        float yaw = 0.0f;
        float pitch = 0.0f;
        if (containsKey("yaw")) {
            yaw = Float.parseFloat(get("yaw").toString());
        }
        if (containsKey("pitch")) {
            pitch = Float.parseFloat(get("pitch").toString());
        }
        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }

}
