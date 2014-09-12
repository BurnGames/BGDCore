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

package me.paulbgd.bgdcore.blocks.block;

import lombok.Data;

@Data
/**
 * Represents a relative position. Used for storing {@link me.paulbgd.blocks.api.block.Block} positions.
 */
public class BlockPosition {

    /**
     * The relative x
     */
    private final int relativeX;
    private final int relativeY;
    /**
     * The relative y
     */
    private final int relativeZ;
    /**
     * The relative z
     */

    /**
     * Returns a stringed version of this, to be used in storage
     *
     * @return stringed version
     */
    @Override
    public String toString() {
        return relativeX + "!" + relativeY + "!" + relativeZ;
    }

    /**
     * Checks if another position is the same relatively
     *
     * @param object to check again
     * @return true of they're at the same spot
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof BlockPosition)) {
            return false;
        }
        BlockPosition other = (BlockPosition) object;
        return other.relativeZ == relativeZ && other.relativeX == relativeX && other.getRelativeY() == relativeY;
    }

    /**
     * A quick way to check coordinates.
     *
     * @param x the x coord
     * @param y the y coord
     * @param z the z coord
     * @return if the coordinates are the same
     */
    public boolean equals(int x, int y, int z) {
        return x == this.relativeX && y == this.relativeY && z == this.relativeZ;
    }

    /**
     * Returns a clone of this for {@link me.paulbgd.blocks.api.block.Block#clone()}
     *
     * @return the clone
     */
    public BlockPosition clone() {
        return new BlockPosition(relativeX, relativeY, relativeZ);
    }

}
