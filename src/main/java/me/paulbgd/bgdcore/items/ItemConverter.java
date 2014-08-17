package me.paulbgd.bgdcore.items;

import me.paulbgd.bgdcore.nms.NMSManager;
import org.bukkit.inventory.ItemStack;

public class ItemConverter {

    public static ItemStack convertToBukkit(TransitionItem transitionItem) {
        return NMSManager.getNms().getBukkitItem(transitionItem);
    }

    public static TransitionItem getItem(ItemStack itemStack) {
        return NMSManager.getNms().getItem(itemStack);
    }

}
