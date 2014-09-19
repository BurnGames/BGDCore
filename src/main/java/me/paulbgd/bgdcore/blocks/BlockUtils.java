/*
 * COPYRIGHT AND PERMISSION NOTICE
 *
 * Copyright (c) 2014, PaulBGD, <paul@paulbgd.me>.
 *
 * All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software for any purpose
 * with or without fee is hereby granted, provided that the above copyright
 * notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT OF THIRD PARTY RIGHTS. IN
 * NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Except as contained in this notice, the name of a copyright holder shall not
 * be used in advertising or otherwise to promote the sale, use or other dealings
 * in this Software without prior written authorization of the copyright holder.
 */

package me.paulbgd.bgdcore.blocks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import me.paulbgd.bgdcore.blocks.block.BlockPosition;
import me.paulbgd.bgdcore.blocks.block.data.BlockData;
import me.paulbgd.bgdcore.blocks.block.data.ComplexBlockData;
import me.paulbgd.bgdcore.blocks.block.data.SimpleBlockData;
import me.paulbgd.bgdcore.nms.NMSManager;
import me.paulbgd.bgdcore.reflection.NMSReflection;
import me.paulbgd.bgdcore.reflection.ReflectionMethod;
import me.paulbgd.bgdcore.reflection.ReflectionObject;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class BlockUtils {

    /**
     * Gets a list of all the blocks between two points.
     *
     * @param from   where they're being added from
     * @param block1 point 1
     * @param block2 point 2
     * @return a list of all blocks between the points
     */
    public static List<me.paulbgd.bgdcore.blocks.block.Block> getAllBlocks(Block from, Block block1, Block block2) {
        if (!block1.getWorld().equals(block2.getWorld())) {
            throw new IllegalArgumentException("Two different worlds!");
        }
        List<me.paulbgd.bgdcore.blocks.block.Block> blocks = new ArrayList<>();
        int maxHeight = block1.getWorld().getMaxHeight(), minY = lowestInteger(block1.getY(), block2.getY());
        if (minY > maxHeight) {
            return blocks;
        }
        int maxY = highestInteger(block1.getY(), block2.getY());
        if (maxY > maxHeight) {
            maxY = maxHeight;
        }
        int minX = lowestInteger(block1.getX(), block2.getX()), minZ = lowestInteger(block1.getZ(), block2.getZ());
        int maxX = highestInteger(block1.getX(), block2.getX()), maxZ = highestInteger(block1.getZ(), block2.getZ());
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPosition blockPosition = new BlockPosition(x - from.getX(), y - from.getY(), z - from.getZ());
                    BlockData blockData;
                    ReflectionObject tileEntity = NMSManager.getNms().getTileEntity(from.getWorld(), x, y, z);
                    short data = NMSManager.getNms().getData(from.getWorld(), x, y, z);
                    if (tileEntity == null) {
                        // normal block I suppose
                        blockData = new SimpleBlockData(NMSManager.getNms().getId(from.getWorld(), x, y, z), data);
                    } else {
                        blockData = new ComplexBlockData(tileEntity, data);
                    }
                    blocks.add(new me.paulbgd.bgdcore.blocks.block.Block(blockPosition, blockData));
                }
            }
        }
        return blocks;
    }

    /**
     * Pastes a list of blocks at a specified location. There is the option to paste with air.
     *
     * @param blocks   a list of blocks to paste
     * @param location the location to paste at
     * @param air      if or if not to paste the air
     */
    public static void paste(Collection<me.paulbgd.bgdcore.blocks.block.Block> blocks, Block location, boolean air) {
        int x = location.getX(), y = location.getY(), z = location.getZ();
        for (me.paulbgd.bgdcore.blocks.block.Block block : blocks) {
            BlockPosition position = block.getPosition();
            BlockData data = block.getData();
            int j = x + position.getRelativeX(), k = y + position.getRelativeY(), l = z + position.getRelativeZ();
            if (data.getId() == 0 && (!air || NMSManager.getNms().getId(location.getWorld(), j, k, l) == 0)) {
                continue; // no need to do air.. again
            }
            try {
                NMSManager.getNms().setBlock(location.getWorld(), j, k, l, Material.getMaterial(data.getId()), data.getBlockData());
                if (data instanceof ComplexBlockData) {
                    ReflectionObject nbtTagCompound = new ReflectionObject(((ComplexBlockData) data).getNBT());
                    ReflectionMethod setInt = nbtTagCompound.getMethod("setInt", "", 0);
                    setInt.invoke("x", j);
                    setInt.invoke("y", k);
                    setInt.invoke("z", l);
                    ReflectionObject tileEntity = NMSManager.getNms().getTileEntity(location.getWorld(), j, k, l);
                    tileEntity.getMethodByClasses("a", NMSReflection.nbtTagCompound.getClazz()).invoke(nbtTagCompound.getObject());
                    NMSManager.getNms().setTileEntity(location.getWorld(), j, k, l, tileEntity.getObject());
                } else if (!(data instanceof SimpleBlockData)) {
                    throw new IllegalArgumentException(String.format("Invalid data type '%s'!", data.getClass()));
                }
            } catch (NullPointerException e) {
            }
        }
    }

    /**
     * Gets the lowest integer between two numbers
     *
     * @param i1 number 1
     * @param i2 number 2
     * @return lowest integer
     */
    private static int lowestInteger(int i1, int i2) {
        return i1 < i2 ? i1 : i2;
    }


    /**
     * Gets the highest integer between two numbers
     *
     * @param i1 number 1
     * @param i2 number 2
     * @return highest integer
     */
    private static int highestInteger(int i1, int i2) {
        return i1 > i2 ? i1 : i2;
    }

}
