package me.paulbgd.bgdcore.commands.def;

import me.paulbgd.bgdcore.commands.Command;
import me.paulbgd.bgdcore.items.ItemConverter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemData extends Command {

    public ItemData(JavaPlugin javaPlugin) {
        super(javaPlugin, "/itemdata", "Shows item data", new Permission("bgdcore.itemdata", PermissionDefault.OP), "itemdata");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You are not a player!");
            return;
        }
        System.out.println(ItemConverter.getItem(((Player) sender).getItemInHand()).toString());
        sender.sendMessage(ChatColor.GREEN + "Outputted data in console.");
    }
}
