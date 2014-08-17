package me.paulbgd.bgdcore.commands;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
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

}
