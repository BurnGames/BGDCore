package me.paulbgd.bgdcore.items;

import lombok.Getter;
import lombok.Setter;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import org.bukkit.inventory.ItemStack;

public class TransitionItem {

    @Getter
    @Setter
    private JSONObject jsonObject;

    public ItemStack asBukkitItem() {
        return ItemConverter.convertToBukkit(this);
    }

    public String toString() {
        return jsonObject.toJSONString(JSONStyle.MAX_COMPRESS);
    }

    public boolean isShiny() {
        return jsonObject.containsKey("ench");
    }

}
