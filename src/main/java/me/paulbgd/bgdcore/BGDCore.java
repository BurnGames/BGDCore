package me.paulbgd.bgdcore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import me.paulbgd.bgdcore.commands.def.ReloadConfigCommand;
import me.paulbgd.bgdcore.configuration.ConfigurationFile;
import me.paulbgd.bgdcore.configuration.CoreConfiguration;
import me.paulbgd.bgdcore.nms.NMSManager;
import org.bukkit.plugin.java.JavaPlugin;

public class BGDCore extends JavaPlugin {

    @Getter
    private static final List<ConfigurationFile> configurations = new ArrayList<>();

    @Getter
    private static Logger logging;

    @Override
    public void onEnable() {
        // set some defaults
        logging = this.getLogger();

        // setup our folder
        if (!getDataFolder().exists() && !getDataFolder().mkdir()) {
            logging.warning("Failed to create data folder! Will continue on..");
        }

        // load nms
        new NMSManager();

        // register our own commands
        new ReloadConfigCommand(this);

        // register our own configuration
        registerConfiguration(new CoreConfiguration(new File(getDataFolder(), "config.json")));
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


}
