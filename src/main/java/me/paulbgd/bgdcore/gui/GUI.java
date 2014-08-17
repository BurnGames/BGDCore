package me.paulbgd.bgdcore.gui;


import lombok.Getter;
import me.paulbgd.bgdcore.BGDCore;
import me.paulbgd.bgdcore.items.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * The Class GUI.
 */
public abstract class GUI implements Listener {

    /**
     * Gets the inventory.
     *
     * @return the inventory
     */
    @Getter
    private final Inventory inventory;

    /**
     * Instantiates a new gui.
     *
     * @param title the title
     * @param size  the size
     */
    public GUI(String title, int size) {
        this.inventory = Bukkit.createInventory(null, size, title);

        Bukkit.getPluginManager().registerEvents(this, BGDCore.getPlugin(BGDCore.class));
    }

    /**
     * Adds the item.
     *
     * @param item the item
     */
    public void addItem(ItemBuilder item) {
        this.inventory.addItem(item.build());
    }

    /**
     * Sets the item.
     *
     * @param index the index
     * @param item  the item
     */
    public void setItem(int index, ItemBuilder item) {
        this.inventory.setItem(index, item.build());
    }

    /**
     * Gets the spot.
     *
     * @param row      the row
     * @param position the position
     * @return the spot
     */
    public int getSpot(int row, int position) {
        return (row * 9) + position - 1;
    }

    /**
     * On inventory click.
     *
     * @param event the event
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().equals(this.inventory)) {
            if (event.getCurrentItem() != null && this.inventory.contains(event.getCurrentItem()) && event.getWhoClicked() instanceof Player) {
                this.onClick((Player) event.getWhoClicked(), event.getCurrentItem());
                HumanEntity who = event.getWhoClicked();
                ((Player) who).playSound(who.getLocation(), Sound.FIRE_IGNITE, 0.3f, 0.3f);
                event.setCancelled(true);
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

    /**
     * On close. Needs to be overriden.
     *
     * @param player the player
     * @return true, if you want to play the closing sound.
     */
    public boolean onClose(Player player) {
        // to be overriden
        return true;
    }

}
