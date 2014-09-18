package me.paulbgd.bgdcore.player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Data;
import lombok.Setter;
import me.paulbgd.bgdcore.blocks.block.Blocks;
import net.minidev.json.JSONObject;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

@Data
public class PlayerWrapper extends JSONObject implements Players {

    private final UUID uniqueId;
    private final HashMap<JavaPlugin, PluginPlayerData> plugins = new HashMap<>();
    private HashMap<String, Object> data;

    @Setter
    private Block pointOne;
    @Setter
    private Block pointTwo;
    @Setter
    private Blocks clipboard;

    public PlayerWrapper(Player player) {
        this(player.getUniqueId());
    }

    public PlayerWrapper(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uniqueId);
    }

    @Override
    public String getName() {
        return toString();
    }

    @Override
    public List<PlayerWrapper> getPlayers() {
        return Arrays.asList(this);
    }

    @Override
    public String toString() {
        return uniqueId.toString();
    }

    public Object getData(String key) {
        checkData();
        return data.get(key);
    }

    public int getInt(String key) {
        return (int) getData(key);
    }

    public String getString(String key) {
        return (String) getData(key);
    }

    public void setData(String key, Object value) {
        checkData();
        data.put(key, value);
    }

    public boolean hasData(String key) {
        return this.data != null && getData(key) != null;
    }

    private void checkData() {
        if (data == null) {
            this.data = new HashMap<>();
        }
    }

    public void load(JavaPlugin plugin, PluginPlayerData playerData) {
        Object jsonObject = get(plugin.getName());
        if (jsonObject == null) {
            jsonObject = new JSONObject();
        }
        playerData.load((JSONObject) jsonObject);
        plugins.put(plugin, playerData);
    }

    public void save() {
        for (Map.Entry<JavaPlugin, PluginPlayerData> entry : plugins.entrySet()) {
            put(entry.getKey().getName(), entry.getValue().save());
        }
    }

    public PluginPlayerData getPluginData(JavaPlugin javaPlugin) {
        return this.plugins.get(javaPlugin);
    }

    public <T extends PluginPlayerData> T getPluginData(Class<?> clazz) {
        for (PluginPlayerData pluginPlayerData : plugins.values()) {
            if(pluginPlayerData.getClass().equals(clazz)) {
                return (T) pluginPlayerData;
            }
        }
        return null;
    }

}
