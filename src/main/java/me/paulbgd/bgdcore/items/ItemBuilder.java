/*
 * Property of Burn Games LLC. 
 * 
 * You may not distribute, decompile, or modify without expressed permission.
 */
package me.paulbgd.bgdcore.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import net.minidev.json.JSONObject;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.Potion;

/**
 * A wrapper for Bukkit's ItemStack class. Makes it easier to make an item in few lines.
 */
public class ItemBuilder {

    /**
     * Gets the data.
     *
     * @return the data
     */
    @Getter
    private final short data;
    /**
     * Gets the type.
     *
     * @return the type
     */
    @Getter
    private Material type;
    /**
     * Gets the amount.
     *
     * @return the amount
     */
    @Getter
    private int amount;
    /**
     * Gets the title.
     *
     * @return the title
     */
    @Getter
    private String title = null;

    /**
     * Gets the lore.
     *
     * @return the lore
     */
    @Getter
    private List<String> lore = new ArrayList<String>();

    /**
     * Gets all of the enchantments.
     *
     * @return the enchantments
     */
    @Getter
    private Map<Enchantment, Integer> allEnchantments = new HashMap<Enchantment, Integer>();

    /**
     * Gets the color.
     *
     * @return the color
     */
    @Getter
    private Color color;

    /**
     * Gets the potion.
     *
     * @return the potion
     */
    @Getter
    private Potion potion;

    /**
     * Gets the owner.
     *
     * @return the owner
     */
    @Getter
    private String owner;

    /**
     * Checks if is shiny.
     *
     * @return true, if is shiny
     */
    @Getter
    private boolean shiny;

    /**
     * Instantiates a new item builder.
     *
     * @param item the item
     */
    public ItemBuilder(ItemStack item) {
        this(item.getType(), item.getAmount(), item.getDurability());

        if (item.getItemMeta().hasDisplayName()) {
            this.title = item.getItemMeta().getDisplayName();
        }
        if (item.getItemMeta().hasLore()) {
            this.lore = item.getItemMeta().getLore();
        }
        this.allEnchantments = item.getEnchantments();
        if (item.getType().name().startsWith("LEATHER_")) {
            this.color = ((LeatherArmorMeta) item.getItemMeta()).getColor();
        } else if (item.getType() == Material.POTION) {
            // not supported
        } else if (item.getType() == Material.SKULL_ITEM && data == (short) 3) {
            this.owner = ((SkullMeta) item.getItemMeta()).getOwner();
        }
        TransitionItem transitionItem = ItemConverter.getItem(item);
        if (transitionItem.isShiny()) {
            // shiny :o
            this.shiny = true;
        }
    }

    /**
     * Instantiates a new item builder.
     *
     * @param mat the mat
     */
    public ItemBuilder(Material mat) {
        this(mat, 1);
    }

    /**
     * Instantiates a new item builder.
     *
     * @param mat    the mat
     * @param amount the amount
     */
    public ItemBuilder(Material mat, int amount) {
        this(mat, amount, (short) 0);
    }

    /**
     * Instantiates a new item builder.
     *
     * @param mat  the mat
     * @param data the data
     */
    public ItemBuilder(Material mat, short data) {
        this(mat, 1, data);
    }

    /**
     * Instantiates a new item builder.
     *
     * @param mat    the mat
     * @param amount the amount
     * @param data   the data
     */
    public ItemBuilder(Material mat, int amount, short data) {
        this.type = mat;
        this.amount = amount;
        this.data = data;
    }

    /**
     * Sets the type.
     *
     * @param mat the mat
     * @return the item builder
     */
    public ItemBuilder setType(Material mat) {
        this.type = mat;
        return this;
    }

    /**
     * Append title.
     *
     * @param appending the appending
     * @return the item builder
     */
    public ItemBuilder appendTitle(String appending) {
        this.title += appending;
        return this;
    }

    /**
     * Append title.
     *
     * @param appending the appending
     * @param color     the color
     * @return the item builder
     */
    public ItemBuilder appendTitle(String appending, ChatColor color) {
        this.title += color + "" + ChatColor.BOLD + appending;
        return this;
    }

    /**
     * Sets the title.
     *
     * @param title the title
     * @return the item builder
     */
    public ItemBuilder setTitle(String title) {
        return this.setTitle(title, ChatColor.GOLD);
    }

    /**
     * Sets the title.
     *
     * @param title the title
     * @param color the color
     * @return the item builder
     */
    public ItemBuilder setTitle(String title, ChatColor color) {
        this.title = color + "" + ChatColor.BOLD + title;
        return this;
    }

    /**
     * Sets the lore.
     *
     * @param lore  the lore
     * @param index the index
     * @return the item builder
     */
    public ItemBuilder setLore(String lore, int index) {
        this.lore.set(index, lore);
        return this;
    }

    /**
     * Adds the lore.
     *
     * @param lore the lore
     * @return the item builder
     */
    public ItemBuilder addLore(String... lore) {
        for (String line : lore) {
            this.lore.add(ChatColor.GRAY + line);
        }
        return this;
    }

    /**
     * Sets the shiny.
     *
     * @param shiny the shiny
     * @return the item builder
     */
    public ItemBuilder setShiny(boolean shiny) {
        this.shiny = shiny;
        return this;
    }

    /**
     * Adds the enchantment.
     *
     * @param enchant the enchant
     * @param level   the level
     * @return the item builder
     */
    public ItemBuilder addEnchantment(Enchantment enchant, int level) {
        allEnchantments.put(enchant, level);
        return this;
    }

    /**
     * Sets the color.
     *
     * @param color the color
     * @return the item builder
     */
    public ItemBuilder setColor(Color color) {
        if (!this.type.name().contains("LEATHER_")) {
            throw new IllegalArgumentException("Can only dye leather armor!");
        }
        this.color = color;
        return this;
    }

    /**
     * Sets the potion.
     *
     * @param potion the potion
     * @return the item builder
     */
    public ItemBuilder setPotion(Potion potion) {
        if (this.type != Material.POTION) {
            this.type = Material.POTION;
        }
        this.potion = potion;
        return this;
    }

    /**
     * Sets the amount.
     *
     * @param amount the amount
     * @return the item builder
     */
    public ItemBuilder setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    /**
     * Sets the owner.
     *
     * @param owner the owner
     * @return the item builder
     */
    public ItemBuilder setOwner(String owner) {
        this.owner = owner;
        return this;
    }

    /**
     * Builds the itemstack.
     *
     * @return the item stack
     */
    public ItemStack build() {
        Material mat = this.type;
        if (mat == null) {
            mat = Material.AIR;
        }
        ItemStack item = new ItemStack(mat, this.amount, this.data);
        ItemMeta meta = item.getItemMeta();
        if (this.title != null) {
            meta.setDisplayName(this.title);
        }
        if (!this.lore.isEmpty()) {
            meta.setLore(this.lore);
        }
        if (meta instanceof LeatherArmorMeta) {
            ((LeatherArmorMeta) meta).setColor(this.color);
        }
        if (owner != null && meta instanceof SkullMeta && this.data == (short) 3) {
            ((SkullMeta) meta).setOwner(owner);
        }
        item.setItemMeta(meta);
        item.addUnsafeEnchantments(this.allEnchantments);
        if (this.potion != null) {
            this.potion.apply(item);
        }
        if (this.allEnchantments.size() == 0 && this.shiny) {
            TransitionItem transitionItem = ItemConverter.getItem(item);
            JSONObject jsonObject = transitionItem.getJsonObject();
            if (!jsonObject.containsKey("tag")) {
                jsonObject.put("tag", new JSONObject());
            }
            JSONObject tag = (JSONObject) jsonObject.get("tag");
            tag.put("ench", new ArrayList<>());
            item = transitionItem.asBukkitItem();
        }
        return item;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#clone()
     */
    public ItemBuilder clone() {
        ItemBuilder newBuilder = new ItemBuilder(this.type, this.amount, this.data);

        newBuilder.title = title;
        newBuilder.lore = lore;
        newBuilder.allEnchantments = allEnchantments;
        newBuilder.color = color;
        newBuilder.potion = potion;
        newBuilder.owner = owner;
        newBuilder.shiny = shiny;

        return newBuilder;
    }

    /**
     * Checks for enchantment.
     *
     * @param enchant the enchant
     * @return true, if successful
     */
    public boolean hasEnchantment(Enchantment enchant) {
        return this.allEnchantments.containsKey(enchant);
    }

    /**
     * Gets the enchantment level.
     *
     * @param enchant the enchant
     * @return the enchantment level
     */
    public int getEnchantmentLevel(Enchantment enchant) {
        return this.allEnchantments.get(enchant);
    }

    /**
     * Checks if is item.
     *
     * @param item the item
     * @return true, if is item
     */
    public boolean isItem(ItemStack item) {
        if (item == null) {
            return this.type == Material.AIR;
        }
        ItemMeta meta = item.getItemMeta();
        if (item.getType() != this.type) {
            return false;
        }
        if (!meta.hasDisplayName() && this.getTitle() != null) {
            return false;
        }
        if (!meta.getDisplayName().equals(this.getTitle())) {
            return false;
        }
        if (!meta.hasLore() && !this.getLore().isEmpty()) {
            return false;
        }
        if (meta.hasLore()) {
            for (String lore : meta.getLore()) {
                if (!this.getLore().contains(lore)) {
                    return false;
                }
            }
        }
        if (meta instanceof SkullMeta && !((SkullMeta) meta).getOwner().equals(this.owner)) {
            return false;
        }
        if (meta instanceof PotionMeta && this.potion == null) {
            return false;
        }
        for (Enchantment enchant : item.getEnchantments().keySet()) {
            if (!this.hasEnchantment(enchant)) {
                return false;
            }
        }
        if (!shiny && ItemConverter.getItem(item).isShiny()) {
            return false;
        }
        return true;
    }

}