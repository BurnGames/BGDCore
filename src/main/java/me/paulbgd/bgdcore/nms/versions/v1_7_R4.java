package me.paulbgd.bgdcore.nms.versions;

import me.paulbgd.bgdcore.BGDCore;
import me.paulbgd.bgdcore.reflection.NMSReflection;
import me.paulbgd.bgdcore.reflection.ReflectionMethod;
import me.paulbgd.bgdcore.reflection.ReflectionObject;
import org.bukkit.Material;
import org.bukkit.World;

public class v1_7_R4 extends v1_7_R1 {

    @Override
    public boolean setBlock(World world, int x, int y, int z, Material type, short data) {
        ReflectionObject nmsWorld = new ReflectionObject(world).getMethod("getHandle").invoke();
        ReflectionMethod setTypeAndData = NMSReflection.nmsWorld.getMethod(nmsWorld, "setTypeAndData", new Class[]{int.class, int.class, int.class, NMSReflection.nmsBlock.getClazz(), int.class, int.class});
        ReflectionMethod getNMSBlock = NMSReflection.nmsBlock.getStaticMethod("getById", type.getId());
        try {
            return (boolean) setTypeAndData.invoke(x, y, z, getNMSBlock.invoke(type.getId()), (int) data, 2).getObject();
        } catch (Exception e) {
            BGDCore.debug("Failed to set block with NMS! Doing bukkit.");
        }
        return world.getBlockAt(x, y, z).setTypeIdAndData(type.getId(), (byte) data, false);
    }

}
