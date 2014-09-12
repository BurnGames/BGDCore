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

package me.paulbgd.bgdcore.blocks.block.data;

import lombok.Data;
import lombok.Setter;
import me.paulbgd.bgdcore.nms.NMSManager;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.bukkit.block.BlockState;

/**
 * Represents the data a block can contain.
 * <p/>
 * {@link SimpleBlockData}
 * {@link ComplexBlockData}
 */
@Data
public abstract class BlockData {

    @Setter
    protected int id;
    @Setter
    protected short blockData;

    /**
     * Creates a simple BlockData instance using the block id and data
     *
     * @param id        the block id
     * @param blockData the block data
     */
    public BlockData(int id, short blockData) {
        this.id = id;
        this.blockData = blockData;
    }

    /**
     * Creates a BlockData instance using a BlockState.
     * Deprecated for use of Bukkit object BlockState.
     * <p/>
     * {@link org.bukkit.block.BlockState}
     *
     * @param blockState the block state
     */
    @Deprecated
    public BlockData(BlockState blockState) {
        this.id = blockState.getTypeId();
        this.blockData = blockState.getRawData();
    }

    /**
     * Loads a new BlockData using the id and some sort of object
     *
     * @param id   the block id
     * @param data either a JSON string, JSON object, or short
     * @return new BlockData
     */
    public static BlockData loadData(int id, Object data) {
        if (data instanceof String) {
            String stringedData = (String) data;
            if (JSONValue.isValidJson(stringedData)) {
                data = JSONValue.parse(stringedData);
            }
        }
        if (data instanceof JSONObject) {
            // load complex data!
            return new ComplexBlockData(id, (JSONObject) data);
        } else {
            // normal block, not an issue
            return new SimpleBlockData(id, Short.valueOf(data.toString()));
        }
    }

    /**
     * Loads a new BlockData using a BlockState.
     * Deprecated for use of Bukkit object BlockState.
     *
     * @param blockState the block state to use
     * @return new BlockData
     */
    @Deprecated
    public static BlockData loadData(BlockState blockState) {
        if (blockState.getClass().getSimpleName().equals("CraftBlockState")) {
            // normal data value, bleh
            return new SimpleBlockData(blockState);
        } else {
            return new ComplexBlockData(NMSManager.getNms().getTileEntity(blockState.getWorld(), blockState.getX(), blockState.getY(), blockState.getZ()), blockState.getRawData());
        }
    }

    /**
     * Gets the data that can be easily stored in JSON
     *
     * @return the data
     */
    public abstract Object getData();

    /**
     * Clones the object
     *
     * @return a clone
     */
    public abstract BlockData clone();

}
