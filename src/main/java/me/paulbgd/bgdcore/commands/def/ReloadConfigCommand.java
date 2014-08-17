package me.paulbgd.bgdcore.commands.def;

import java.util.ArrayList;
import java.util.List;
import me.paulbgd.bgdcore.BGDCore;
import me.paulbgd.bgdcore.commands.Command;
import me.paulbgd.bgdcore.commands.Subcommand;
import me.paulbgd.bgdcore.configuration.ConfigurationFile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;

public class ReloadConfigCommand extends Command {

    public ReloadConfigCommand(BGDCore bgdCore) {
        super(bgdCore, "", "Shows help", new Permission("bgdcore.cmds.reloadconfigs"), "reloadconfig");

        addSubCommand(new ReloadAll(), new ReloadPlugin(), new ReloadSpecific());
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        showHelp(sender);
    }

    public class ReloadAll extends Subcommand {

        public ReloadAll() {
            super(new Permission("bgdcore.cmds.reloadconfigs.all"), "", "Reloads all configuration files", "all", "every");
        }

        @Override
        public void onCommand(CommandSender sender, String[] args) {
            Plugin[] plugins = Bukkit.getPluginManager().getPlugins();
            for (Plugin plugin : plugins) {
                plugin.reloadConfig();
            }
            sendMessage(ChatColor.GREEN + "Reloaded " + plugins.length + " plugin configuration file(s)!", sender);
        }
    }

    public class ReloadPlugin extends Subcommand {

        public ReloadPlugin() {
            super(new Permission("bgdcore.cmds.reloadconfigs.plugin"), "<plugin>", "Reloads a plugin's configuration file", "plugin");
        }

        @Override
        public void onCommand(CommandSender sender, String[] args) {
            if (args.length != 1) {
                showHelp(sender);
                return;
            }
            Plugin plugin = Bukkit.getPluginManager().getPlugin(args[0]);
            if (plugin == null) {
                sendMessage(ChatColor.RED + "Plugin \"" + args[0] + "\" does not exist!", sender);
            } else {
                plugin.reloadConfig();
                sendMessage(ChatColor.GREEN + "Reloaded " + plugin.getName() + "'s configuration file(s)!", sender);
            }
        }

        @Override
        public List<String> getTabCompletions(int args) {
            Plugin[] allPlugins = Bukkit.getPluginManager().getPlugins();
            List<String> plugins = new ArrayList<>(allPlugins.length);
            for (Plugin plugin : allPlugins) {
                plugins.add(plugin.getName());
            }
            return plugins;
        }

    }

    public class ReloadSpecific extends Subcommand {

        public ReloadSpecific() {
            super(new Permission("bgdcore.cmds.reloadconfigs.config"), "<name>", "Reloads a configuration used by BGDCore", "specific");
        }

        @Override
        public void onCommand(CommandSender sender, String[] args) {
            if (args.length != 1) {
                showHelp(sender);
                return;
            }
            String fileName = args[0];
            if (!fileName.toLowerCase().endsWith(".json")) {
                fileName += ".json";
            }
            for (ConfigurationFile configurationFile : BGDCore.getConfigurations()) {
                if (configurationFile.getFile().getName().equalsIgnoreCase(fileName)) {
                    String full = configurationFile.getFile().getAbsolutePath();
                    if (full.length() > 30) {
                        full = "..." + full.substring(full.length() - 30);
                    }
                    try {
                        configurationFile.update();
                        sendMessage(ChatColor.GREEN + "Reloaded the " + full + " configuration file!", sender);
                    } catch (Exception e) {
                        sendMessage(ChatColor.RED + "Failed to reload the " + full + " configuration file!", sender);
                        e.printStackTrace();
                    }
                    return;
                }
            }
        }

        @Override
        public List<String> getTabCompletions(int args) {
            List<ConfigurationFile> configurationFiles = BGDCore.getConfigurations();
            List<String> files = new ArrayList<>(configurationFiles.size());
            for (ConfigurationFile configurationFile : configurationFiles) {
                files.add(configurationFile.getFile().getName());
            }
            return files;
        }
    }

}
