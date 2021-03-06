package me.paulbgd.bgdcore.nms;

import me.paulbgd.bgdcore.items.TransitionItem;
import me.paulbgd.bgdcore.reflection.ReflectionObject;
import net.minidev.json.JSONObject;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

public interface BGDNMS {

    public boolean setBlock(World world, int x, int y, int z, Material type, short data);

    public ReflectionObject getTileEntity(World world, int x, int y, int z);
    public void setTileEntity(World world, int x, int y, int z, Object object);

    public int getId(World world, int x, int y, int z);

    public short getData(World world, int x, int y, int z);

    public TransitionItem getItem(ItemStack itemStack);

    public ItemStack getBukkitItem(TransitionItem transitionItem);

    public Object jsonToNBT(JSONObject jsonObject);

    public JSONObject nbtToJSON(Object nbt);

}
