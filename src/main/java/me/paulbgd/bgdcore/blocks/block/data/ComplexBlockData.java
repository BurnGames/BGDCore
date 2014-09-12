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

import me.paulbgd.bgdcore.nms.NMSManager;
import me.paulbgd.bgdcore.reflection.NMSReflection;
import me.paulbgd.bgdcore.reflection.ReflectionObject;
import net.minidev.json.JSONObject;

/**
 * Represents a TileEntity. Stores data as JSON and NBT.
 */
public class ComplexBlockData extends BlockData {

    private final JSONObject data;

    /**
     * Creates a ComplexBlockData using the block id and JSON string
     *
     * @param id   the block id
     * @param data the json string
     */
    public ComplexBlockData(int id, JSONObject data) {
        super(id, Short.valueOf((String) data.get("e")));
        this.data = data;
    }

    /**
     * Creates a ComplexBlockData using a TileEntity.
     *
     * @param tileEntity the TileEntity
     * @param data       block data
     */
    public ComplexBlockData(ReflectionObject tileEntity, int data) {
        super((Integer) NMSReflection.craftMagicNumbers.getStaticMethod("getId", NMSReflection.nmsBlock.getClazz()).invoke(tileEntity.getMethod("q").invoke()).getObject(), (short) data);
        this.data = new JSONObject();
        this.data.put("e", Short.toString(this.blockData));

        ReflectionObject nbtTagCompound = NMSReflection.nbtTagCompound.newInstance();
        tileEntity.getMethod("b", nbtTagCompound.getObject().getClass()).invoke(nbtTagCompound.getObject());
        JSONObject nbtData = NMSManager.getNms().nbtToJSON(nbtTagCompound.getObject());
        this.data.put("n", nbtData);
    }

    @Override
    public Object getData() {
        return this.data;
    }

    @Override
    public BlockData clone() {
        return new ComplexBlockData(getId(), this.data);
    }

    /**
     * Returns the NBT of the TileEntity
     *
     * @return the NBT
     */
    public Object getNBT() {
        return NMSManager.getNms().jsonToNBT((JSONObject) data.get("n"));
    }

}
