package me.paulbgd.bgdcore.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class CommandPiece {

    protected final void sendMessage(String message, CommandSender sender) {
        if (!(sender instanceof Player)) {
            // for safety, remove colors. Some consoles hate them
            message = ChatColor.stripColor(message);
        }
        sender.sendMessage(message);
    }

    public abstract void onCommand(CommandSender sender, String[] args);
}
