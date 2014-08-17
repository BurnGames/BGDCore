package me.paulbgd.bgdcore.commands;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Data;
import me.paulbgd.bgdcore.reflection.ReflectionObject;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

@Data
public abstract class Command extends CommandPiece implements TabExecutor {

    protected final JavaPlugin javaPlugin;
    private final String[] names;
    private final String usage;
    private final String info;
    private final Permission permission;
    private final List<Subcommand> subcommands = new ArrayList<>();

    public Command(JavaPlugin javaPlugin, String usage, String info, Permission permission, String... names) {
        Validate.notNull(javaPlugin);
        this.javaPlugin = javaPlugin;
        this.names = names;
        this.usage = usage;
        this.info = info;
        this.permission = permission;

        // FIXME now, let's automatically add any subcommands that are inside the same class
        //List<ReflectionClass> subs = new ReflectionObject(this).getSubClassesOfType(Subcommand.class);
        //for (ReflectionClass sub : subs) {
        //    this.addSubCommand((Subcommand) sub.newInstance().getObject());
        //}

        // finish off by registering us in bukkit - no need for yaml!
        registerCommand(names);
        javaPlugin.getCommand(names[0]).setExecutor(this);
    }

    protected void addSubCommand(Subcommand... subcommand) {
        this.subcommands.addAll(Arrays.asList(subcommand));
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (!sender.hasPermission(this.permission)) {
            sendMessage(String.format("%s%s", ChatColor.RED, "You do not have permission for this!"), sender);
            return true;
        }
        if (args.length > 0) {
            for (Subcommand subcommand : subcommands) {
                for (String string : subcommand.getNames()) {
                    if (args[0].equalsIgnoreCase(string)) {
                        if (sender.hasPermission(subcommand.getPermission())) {
                            subcommand.onCommand(sender, args.length == 1 ? new String[0] : Arrays.copyOfRange(args, 1, args.length));
                        } else {
                            sendMessage(String.format("%s%s", ChatColor.RED, "You do not have permission for this!"), sender);
                        }
                        return true;
                    }
                }
            }
        }
        this.onCommand(sender, args);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {
        List<String> toReturn = new ArrayList<>();
        if (args.length == 0 || args[0].equals("")) {
            for (Subcommand subcommand : subcommands) {
                toReturn.addAll(Arrays.asList(subcommand.getNames()));
            }
        } else if (args.length == 1) {
            for (Subcommand subcommand : subcommands) {
                for (String name : subcommand.getNames()) {
                    if (name.toLowerCase().startsWith(args[0].toLowerCase())) {
                        toReturn.add(name);
                    }
                }
            }
        } else {
            // we have multiple arguments, let's check with our subcommands
            for (Subcommand subcommand : subcommands) {
                for (String name : subcommand.getNames()) {
                    if (name.equalsIgnoreCase(args[0])) {
                        List<String> tabCompletions = subcommand.getTabCompletions(args.length - 1);
                        if (tabCompletions != null) {
                            for (String tabCompletion : tabCompletions) {
                                if (tabCompletion.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
                                    toReturn.add(tabCompletion);
                                }
                            }
                        }
                    }
                }
            }
        }
        return toReturn;
    }

    protected final void showHelp(CommandSender sender) {
        sendMessage(String.format("%s%s%s: /%s", ChatColor.AQUA, ChatColor.BOLD, "Showing help for", this.names[0]), sender);
        sendMessage(String.format("%s%s /%s %s- %s%s", ChatColor.GRAY, ChatColor.BOLD, this.names[0], usage + (usage.length() > 0 ? " " : ""), ChatColor.DARK_GRAY, this.info), sender);
        for (Subcommand subcommand : subcommands) {
            String subName = subcommand.getNames()[0], subUsage = subcommand.getUsage() + (subcommand.getUsage().length() > 0 ? " " : "");
            sendMessage(String.format("%s%s /%s %s %s - %s%s", ChatColor.GRAY, ChatColor.BOLD, this.names[0], subName, subUsage, ChatColor.DARK_GRAY, subcommand.getInfo()), sender);
        }
    }

    private boolean registerCommand(String[] aliases) {
        PluginCommand command = getCommand(aliases[0], javaPlugin);
        command.setAliases(Arrays.asList(aliases));
        getCommandMap().register(javaPlugin.getName(), command);
        return true;
    }

    private PluginCommand getCommand(String name, JavaPlugin plugin) {
        try {
            Constructor constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            if (!constructor.isAccessible()) {
                constructor.setAccessible(true);
            }
            return (PluginCommand) constructor.newInstance(name, plugin);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private CommandMap getCommandMap() {
        return (CommandMap) new ReflectionObject(Bukkit.getPluginManager()).getField("commandMap").getValue().getObject();
    }

}
