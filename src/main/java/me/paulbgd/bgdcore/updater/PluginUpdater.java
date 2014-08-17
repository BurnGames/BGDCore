package me.paulbgd.bgdcore.updater;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import me.paulbgd.bgdcore.BGDCore;
import me.paulbgd.bgdcore.configuration.CoreConfiguration;
import me.paulbgd.bgdcore.reflection.ReflectionObject;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class PluginUpdater {

    private final static String delimiter = "^v|[\\\\s_-]v"; // credit to Gravity, I'm not so great at regex myself.

    private final JavaPlugin plugin;
    private final URL url;
    private final int id;

    private URL latestFileURL;

    public PluginUpdater(JavaPlugin plugin, int curseId) {
        this.plugin = plugin;
        URL url = null;
        try {
            url = new URL("https://api.curseforge.com/servermods/files?projectIds=" + curseId);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        this.url = url;
        this.id = curseId;

        if (!CoreConfiguration.Plugins_Not_To_Update.contains(plugin.getName())) {
            new UpdaterTask().runTaskAsynchronously(plugin);
        }
    }

    public abstract void onLoad();

    public boolean needsUpdate() {
        return latestFileURL != null;
    }

    public void update() {
        if (Bukkit.isPrimaryThread()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    update();
                }
            }.runTaskAsynchronously(plugin);
            return;
        }
        Validate.notNull(latestFileURL);
        String currentName = ((File) new ReflectionObject(plugin).getMethod("getFile").invoke().getObject()).getName();
        try {
            FileUtils.copyURLToFile(this.latestFileURL, new File(Bukkit.getServer().getUpdateFolderFile(), currentName));
        } catch (IOException e) {
            BGDCore.getLogging().warning("Failed to download update for \"" + plugin.getName() + "\"!");
            e.printStackTrace();
        }
    }

    public class UpdaterTask extends BukkitRunnable {
        @Override
        public void run() {
            try {
                doCheck();
            } catch (IOException e) {
                BGDCore.getLogging().warning("Failed to load update info for \"" + plugin.getName() + "\"!");
                e.printStackTrace();
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    onLoad();
                }
            }.runTask(plugin);
        }

        private void doCheck() throws IOException {
            URLConnection urlConnection = url.openConnection();
            urlConnection.setConnectTimeout(3000); // curse can be slow, this should be good though
            urlConnection.setDoOutput(true);
            InputStream inputStream = urlConnection.getInputStream();
            JSONArray jsonArray = (JSONArray) JSONValue.parse(inputStream);
            inputStream.close();

            // process the result
            if (jsonArray.size() == 0) {
                BGDCore.getLogging().warning("Failed to update plugin \"" + plugin.getName() + "\" due to invalid id '" + id + "!");
                return;
            }
            JSONObject updateData = (JSONObject) jsonArray.get(jsonArray.size() - 1);
            String[] remoteVersionSplit = ((String) updateData.get("name")).split(delimiter);
            if (remoteVersionSplit.length != 2) {
                BGDCore.getLogging().warning("Invalid remote version for plugin \"" + plugin.getName() + "\"!");
                return;
            }
            String hostVersion = remoteVersionSplit[1].split(" ")[0];
            if (hostVersion.equalsIgnoreCase(plugin.getDescription().getVersion())) {
                // same version! no update needed
                return;
            }
            latestFileURL = new URL("downloadUrl");
        }
    }

}
