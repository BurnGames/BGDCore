package me.paulbgd.bgdcore.commands;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

@Data
@EqualsAndHashCode(callSuper = false)
public abstract class Subcommand extends CommandPiece {
    private final String[] names;
    private final Permission permission;
    private final String usage;
    private final String info;

    public Subcommand(Permission permission, String usage, String info, String... names) {
        this.names = names;
        this.permission = permission;
        this.usage = usage;
        this.info = info;
    }

    public List<String> getTabCompletions(int arguments) {
        return null;
    }

    public void showHelp(CommandSender commandSender) {
        String subName = names[0], subUsage = usage + (usage.length() > 0 ? " " : "");

        sendMessage(String.format("%s%sUsage: /<command> %s %s - %s%s", ChatColor.GRAY, ChatColor.BOLD, subName, subUsage, ChatColor.DARK_GRAY, info), commandSender);
    }

}
