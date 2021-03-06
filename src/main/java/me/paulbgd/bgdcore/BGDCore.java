package me.paulbgd.bgdcore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;
import lombok.Getter;
import me.paulbgd.bgdcore.commands.def.ItemData;
import me.paulbgd.bgdcore.commands.def.ReloadConfigCommand;
import me.paulbgd.bgdcore.configuration.ConfigurationFile;
import me.paulbgd.bgdcore.configuration.CoreConfiguration;
import me.paulbgd.bgdcore.io.json.JSONInputStream;
import me.paulbgd.bgdcore.listeners.PlayerListener;
import me.paulbgd.bgdcore.nms.NMSManager;
import me.paulbgd.bgdcore.player.PlayerWrapper;
import me.paulbgd.bgdcore.player.PluginPlayerData;
import net.minidev.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class BGDCore extends JavaPlugin {

    @Getter
    private static final List<ConfigurationFile> configurations = new ArrayList<>();
    private static final Map<JavaPlugin, Class<? extends PluginPlayerData>> playerData = new HashMap<JavaPlugin, Class<? extends PluginPlayerData>>();
    private static final ConcurrentHashMap<UUID, PlayerWrapper> wrappers = new ConcurrentHashMap<>();

    @Getter
    private static Logger logging;
    @Getter
    private static File playerFolder;

    @Override
    public void onEnable() {
        // set some defaults
        logging = this.getLogger();

        // setup our folder
        if (!getDataFolder().exists() && !getDataFolder().mkdir()) {
            logging.warning("Failed to create data folder! Will continue on..");
        }
        playerFolder = new File(getDataFolder(), "players");
        if (!playerFolder.exists() && !playerFolder.mkdir()) {
            logging.warning("Failed to create player data folder! Will continue on..");
        }

        // register our own configuration
        registerConfiguration(new CoreConfiguration(new File(getDataFolder(), "config.json")));

        // load nms
        new NMSManager();

        // register our own commands
        new ReloadConfigCommand(this);
        new ItemData(this);

        // register our own listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        // in the case of a reload, let's load our player wrappers
        for (Player player : Bukkit.getOnlinePlayers()) {
            addPlayerWrapper(player.getUniqueId(), loadPlayerWrapper(player));
        }
    }

    @Override
    public void onDisable() {
        // to hack our JSONOutputStream back in
        /*try {
            ClassLoader classLoader = new URLClassLoader(new URL[]{getFile().toURI().toURL()});
            classLoader.loadClass("me.paulbgd.bgdcore.io.json.JSONOutputStream");
        } catch (ClassNotFoundException | MalformedURLException e) {
            e.printStackTrace();
        }*/
        for (PlayerWrapper playerWrapper : wrappers.values()) {
            savePlayerWrapper(playerWrapper);
        }
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        for (ConfigurationFile configuration : configurations) {
            try {
                configuration.update();
            } catch (Exception e) {
                logging.log(Level.SEVERE, "Failed to reload configuration \"" + configuration.getFile().getAbsolutePath() + "\"!");
                e.printStackTrace();
            }
        }
    }

    @Override
    public void saveConfig() {
        super.saveConfig();
        for (ConfigurationFile configuration : configurations) {
            try {
                configuration.update();
            } catch (Exception e) {
                logging.log(Level.SEVERE, "Failed to save configuration \"" + configuration.getFile().getAbsolutePath() + "\"!");
                e.printStackTrace();
            }
        }
    }

    public static void registerConfiguration(ConfigurationFile configurationFile) {
        if (configurations.contains(configurationFile)) {
            throw new IllegalArgumentException("Configuration file \"" + configurationFile.getFile().getAbsolutePath() + "\" has already been registered!");
        }
        configurations.add(configurationFile);
    }

    public static void registerPluginPlayerData(JavaPlugin plugin, Class<? extends PluginPlayerData> data) {
        playerData.put(plugin, data);

        for (PlayerWrapper wrapper : wrappers.values()) {
            try {
                wrapper.load(plugin, data.getConstructor().newInstance());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    public static PlayerWrapper getPlayerWrapper(Player player) {
        return getPlayerWrapper(player.getUniqueId());
    }

    public static PlayerWrapper getPlayerWrapper(UUID uuid) {
        if (!wrappers.containsKey(uuid)) {
            wrappers.put(uuid, loadPlayerWrapper(uuid));
        }
        return wrappers.get(uuid);
    }

    public static void addPlayerWrapper(UUID uuid, PlayerWrapper playerWrapper) {
        wrappers.put(uuid, playerWrapper);
    }

    public static void removePlayerWrapper(UUID uuid) {
        wrappers.remove(uuid);
    }

    public static PlayerWrapper loadPlayerWrapper(Player player) {
        return loadPlayerWrapper(player.getUniqueId());
    }

    @Deprecated
    public static PlayerWrapper loadPlayerWrapper(UUID uniqueId, String name) {
        return loadPlayerWrapper(uniqueId);
    }

    public static PlayerWrapper loadPlayerWrapper(UUID uniqueId) {
        PlayerWrapper playerWrapper = new PlayerWrapper(uniqueId);
        try {
            File file = new File(playerFolder, getUUIDHash(uniqueId));
            if (file.exists()) {
                JSONInputStream jsonInputStream = new JSONInputStream(new FileInputStream(file));
                JSONObject jsonObject = jsonInputStream.readObject();
                IOUtils.closeQuietly(jsonInputStream);

                playerWrapper.putAll(jsonObject);
            }
        } catch (IOException e) {
            // neither of these should happen, but oh well
            e.printStackTrace();
        }
        for (Map.Entry<JavaPlugin, Class<? extends PluginPlayerData>> entry : playerData.entrySet()) {
            try {
                playerWrapper.load(entry.getKey(), entry.getValue().getConstructor().newInstance());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return playerWrapper;
    }

    public static void savePlayerWrapper(final PlayerWrapper playerWrapper) {
        if (Bukkit.isPrimaryThread() && BGDCore.getPlugin(BGDCore.class).isEnabled()) {
            try {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        savePlayerWrapper(playerWrapper);
                    }
                }.runTaskAsynchronously(getPlugin(BGDCore.class));
                return;
            } catch (Exception e) {
                // disabling!
            }
        }
        playerWrapper.save();
        Validate.notNull(playerWrapper);
        debug("Saving " + playerWrapper.getUniqueId());
        try {
            File file = new File(playerFolder, getUUIDHash(playerWrapper.getUniqueId()));
            GZIPOutputStream jsonOutputStream = new GZIPOutputStream(new FileOutputStream(file));
            IOUtils.write(playerWrapper.toJSONString(), jsonOutputStream);
            jsonOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getUUIDHash(UUID uniqueId) {
        return uniqueId.toString() + ".bgd";
    }

    public static List<PlayerWrapper> loadAllWrappers() {
        List<PlayerWrapper> wrappers = new ArrayList<>();
        if (playerFolder != null && playerFolder.isDirectory()) {
            for (String file : playerFolder.list()) {
                wrappers.add(loadPlayerWrapper(UUID.fromString(file.split("\\.")[0])));
            }
        }
        return wrappers;
    }

    public static String getCachedUsername(UUID uuid) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (offlinePlayer != null) {
            return offlinePlayer.getName();
        }
        return null;
    }

    public static void debug(String message) {
        if (CoreConfiguration.debugMode) {
            if (logging == null) {
                System.out.println("[BGDCore][Debug] " + message);
            } else {
                getLogging().severe(message);
            }
        }
    }

}
