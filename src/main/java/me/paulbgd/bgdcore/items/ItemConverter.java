package me.paulbgd.bgdcore.items;

import me.paulbgd.bgdcore.nms.NMSManager;
import net.minidev.json.JSONObject;
import org.bukkit.inventory.ItemStack;

public class ItemConverter {

    public static ItemStack convertToBukkit(TransitionItem transitionItem) {
        return NMSManager.getNms().getBukkitItem(transitionItem);
    }

    public static TransitionItem getItem(ItemStack itemStack) {
        return NMSManager.getNms().getItem(itemStack);
    }

    public static TransitionItem getItem(JSONObject jsonObject) {
        TransitionItem transitionItem = new TransitionItem();
        transitionItem.setJsonObject(jsonObject);
        return transitionItem;
    }

}
