package me.paulbgd.bgdcore.player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Data
public class PlayerWrapper implements Players {

    private final UUID uniqueId;
    private final String name;

    public PlayerWrapper(Player player) {
        this.uniqueId = player.getUniqueId();
        this.name = player.getName();
    }

    public Player getPlayer() {
        Player player = Bukkit.getPlayer(uniqueId);
        if (player == null) {
            throw new IllegalArgumentException("The player " + name + " is offline!");
        }
        return player;
    }

    @Override
    public List<PlayerWrapper> getPlayers() {
        return Arrays.asList(this);
    }

    @Override
    public String toString() {
        return name;
    }

}
