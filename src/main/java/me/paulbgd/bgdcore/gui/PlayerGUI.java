/*
 * Property of Burn Games LLC. 
 * 
 * You may not distribute, decompile, or modify without expressed permission.
 */
package me.paulbgd.bgdcore.gui;

import java.util.UUID;
import lombok.NonNull;
import me.paulbgd.bgdcore.BGDCore;
import me.paulbgd.bgdcore.items.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a GUI only for one player.
 */
public abstract class PlayerGUI implements Listener {

    /**
     * The player's uuid.
     */
    private UUID uuid;

    /**
     * The inventory.
     */
    private Inventory inventory;

    /**
     * Instantiates a new player gui.
     *
     * @param player the player
     * @param title  the title
     * @param slots  the slots
     */
    public PlayerGUI(@NonNull Player player, @NonNull String title, int slots) {
        if (!player.isOnline()) {
            return;
        }
        this.uuid = player.getUniqueId();
        this.inventory = Bukkit.createInventory(null, slots, title);
        Bukkit.getPluginManager().registerEvents(this, BGDCore.getPlugin(BGDCore.class));
    }

    /**
     * Gets the spot.
     *
     * @param row      the row
     * @param position the position
     * @return the spot
     */
    public static int getSpot(int row, int position) {
        return (row * 9) + position - 1;
    }

    public static int getInventorySize(int max) {
        if (max <= 9) return 9;
        if (max <= 18) return 18;
        if (max <= 27) return 27;
        if (max <= 36) return 36;
        if (max <= 45) return 45;
        return 54;
    }

    /**
     * Adds the item.
     *
     * @param items the items
     */
    public void addItem(ItemStack... items) {
        this.inventory.addItem(items);
    }

    /**
     * Sets the item.
     *
     * @param item     the item
     * @param position the position
     */
    public void setItem(ItemStack item, int position) {
        this.inventory.setItem(position, item);
    }

    /**
     * Sets the item.
     *
     * @param item     the item
     * @param position the position
     */
    public void setItem(ItemBuilder item, int position) {
        this.inventory.setItem(position, item.build());
    }

    /**
     * Gets the inventory.
     *
     * @return the inventory
     */
    public Inventory getInventory() {
        return this.inventory;
    }

    /**
     * Sets the size.
     *
     * @param size the new size
     */
    public void setSize(int size) {
        Inventory newInv = Bukkit.createInventory(null, size, this.inventory.getTitle());
        newInv.setContents(this.inventory.getContents());
        this.inventory = newInv;
    }

    /**
     * On player quit.
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (event.getPlayer().getUniqueId() == this.uuid) {
            HandlerList.unregisterAll(this);
        }
    }

    /**
     * On inventory click.
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked().getUniqueId().equals(this.uuid) && event.getInventory().equals(this.inventory)) {
            if (event.getCurrentItem() != null && this.inventory.contains(event.getCurrentItem())) {
                event.setCancelled(true);
                try {
                    this.onClick((Player) event.getWhoClicked(), event.getCurrentItem());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                HumanEntity who = event.getWhoClicked();
                ((Player) who).playSound(who.getLocation(), Sound.FIRE_IGNITE, 0.3f, 0.3f);
            }
        }
    }

    /**
     * On inventory close.
     *
     * @param event the event
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().equals(this.inventory) && event.getPlayer() instanceof Player) {
            if (this.onClose((Player) event.getPlayer())) {
                ((Player) event.getPlayer()).playSound(event.getPlayer().getLocation(), Sound.CHEST_CLOSE, 0.3f, 0.3f);
            }
        }
    }

    /**
     * On click.
     *
     * @param player the player
     * @param item   the item
     */
    public abstract void onClick(Player player, ItemStack item);

    public boolean onClose(Player player) {
        return true;
    }

}
