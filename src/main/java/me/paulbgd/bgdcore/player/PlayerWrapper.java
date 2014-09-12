package me.paulbgd.bgdcore.player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import net.minidev.json.JSONObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Data
public class PlayerWrapper extends JSONObject implements Players {

    private final UUID uniqueId;

    public PlayerWrapper(Player player) {
        this(player.getUniqueId());
    }

    public PlayerWrapper(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public Player getPlayer() {
        Player player = Bukkit.getPlayer(uniqueId);
        if (player == null) {
            throw new IllegalArgumentException("The player " + uniqueId + " is offline!");
        }
        return player;
    }

    @Override
    public String getName() {
        return toString();
    }

    @Override
    public List<PlayerWrapper> getPlayers() {
        return Arrays.asList(this);
    }

    @Override
    public String toString() {
        return uniqueId.toString();
    }

}
