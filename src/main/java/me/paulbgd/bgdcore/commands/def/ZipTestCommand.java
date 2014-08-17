package me.paulbgd.bgdcore.commands.def;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import me.paulbgd.bgdcore.BGDCore;
import me.paulbgd.bgdcore.commands.Command;
import me.paulbgd.bgdcore.io.ZipUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

public class ZipTestCommand extends Command {

    public ZipTestCommand() {
        super(BGDCore.getPlugin(BGDCore.class), "", "Do not use", new Permission("paulbgd.only"), "ziptest");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("Choose folder!");
            return;
        }
        File file = new File(javaPlugin.getDataFolder(), args[0]);
        File zip = new File(javaPlugin.getDataFolder(), args[0] + ".zip");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(zip);
            ZipUtils.zipDirectory(fileOutputStream, file);
            fileOutputStream.close();
            FileInputStream fileInputStream = new FileInputStream(zip);
            ZipUtils.extractZip(fileInputStream, new File(javaPlugin.getDataFolder(), "extracted"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
