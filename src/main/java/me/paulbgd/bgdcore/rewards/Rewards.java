package me.paulbgd.bgdcore.rewards;

import java.util.List;
import me.paulbgd.bgdcore.items.ItemConverter;
import net.minidev.json.JSONObject;
import org.bukkit.inventory.ItemStack;

public class Rewards {

    public static Reward fromJSON(JSONObject jsonObject) {
        if (jsonObject.containsKey("type")) {
            String type = (String) jsonObject.get("type");
            switch (type.toLowerCase()) {
                case "money":
                    if (jsonObject.containsKey("amount")) {
                        return new MoneyReward((Integer) jsonObject.get("amount"));
                    }
                    break;
                case "item":
                    if (jsonObject.containsKey("items")) {
                        List<Object> items = (List<Object>) jsonObject.get("items");
                        ItemStack[] itemStacks = new ItemStack[items.size()];
                        for (int i = 0, itemsSize = items.size(); i < itemsSize; i++) {
                            itemStacks[i] = ItemConverter.getItem((JSONObject) items.get(i)).asBukkitItem();
                        }
                        return new ItemReward(itemStacks);
                    }
                    break;
            }
        }
        return new EmptyReward();
    }

}
