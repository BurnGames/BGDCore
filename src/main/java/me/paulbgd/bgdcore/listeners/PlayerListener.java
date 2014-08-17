package me.paulbgd.bgdcore.listeners;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import me.paulbgd.bgdcore.BGDCore;
import me.paulbgd.bgdcore.player.PlayerWrapper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerListener implements Listener {

    private final ConcurrentHashMap<UUID, PlayerWrapper> wrappers = new ConcurrentHashMap<>();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin(final AsyncPlayerPreLoginEvent event) {
        // let's load it async!
        wrappers.put(event.getUniqueId(), BGDCore.loadPlayerWrapper(event.getUniqueId(), event.getName()));
        new BukkitRunnable() {
            @Override
            public void run() {
                if (wrappers.contains(event.getUniqueId())) {
                    wrappers.remove(event.getUniqueId()); // aw, wasn't used
                }
            }
        }.runTaskLater(BGDCore.getPlugin(BGDCore.class), 30);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        PlayerWrapper playerWrapper;
        if (wrappers.contains(event.getPlayer().getUniqueId())) {
            playerWrapper = wrappers.get(event.getPlayer().getUniqueId());
            wrappers.remove(event.getPlayer().getUniqueId());
        } else {
            playerWrapper = BGDCore.loadPlayerWrapper(event.getPlayer());
        }
        BGDCore.addPlayerWrapper(playerWrapper.getUniqueId(), playerWrapper);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                PlayerWrapper playerWrapper = BGDCore.getPlayerWrapper(event.getPlayer());
                BGDCore.savePlayerWrapper(playerWrapper);
                BGDCore.removePlayerWrapper(event.getPlayer().getUniqueId());
            }
        }.runTaskAsynchronously(BGDCore.getPlugin(BGDCore.class));
    }

}
