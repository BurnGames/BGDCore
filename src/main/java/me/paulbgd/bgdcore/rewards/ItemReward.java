package me.paulbgd.bgdcore.rewards;

import me.paulbgd.bgdcore.player.PlayerWrapper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemReward implements Reward {

    private final ItemStack[] itemStacks;

    public ItemReward(ItemStack... itemStacks) {
        this.itemStacks = itemStacks;
    }

    @Override
    public void give(PlayerWrapper playerWrapper) {
        Player player = playerWrapper.getPlayer();
        if (player != null) {
            player.getInventory().addItem(itemStacks);
        }
    }

    @Override
    public void take(PlayerWrapper playerWrapper) {
        Player player = playerWrapper.getPlayer();
        if (player != null) {
            player.getInventory().removeItem(itemStacks);
        }
    }

}
