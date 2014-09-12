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

import me.paulbgd.bgdcore.blocks.block.loader.BlocksFormat;
import me.paulbgd.bgdcore.blocks.block.loader.BlocksLoader;
import me.paulbgd.bgdcore.blocks.block.loader.SchematicFormat;

/**
 * Holds the official BlockLoaders. Switch from an enum allowing plugins to create their own format.
 */
public class BlocksType {

    /**
     * The Blocks format saver/loader. Used for the official format.
     */
    public static final BlocksLoader BLOCKS = new BlocksFormat();
    /**
     * The schematic saver/loader. Used for conversion.
     */
    public static final BlocksLoader SCHEMATIC = new SchematicFormat();

}
