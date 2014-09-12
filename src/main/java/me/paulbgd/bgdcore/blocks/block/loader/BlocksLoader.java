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

package me.paulbgd.bgdcore.blocks.block.loader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import me.paulbgd.bgdcore.blocks.block.Blocks;

/**
 * A class used to load and save Blocks objects.
 */
public interface BlocksLoader {

    /**
     * Gets the name of this BlocksLoader. Used in files.
     *
     * @return the name
     */
    public String getName();

    /**
     * Loads a Blocks object from the specified InputStream.
     *
     * @param inputStream the input stream to load from
     * @return new blocks object
     * @throws java.io.IOException if there's an error loading it
     */
    public Blocks load(InputStream inputStream) throws IOException;

    /**
     * Saves a set of blocks to a said OutputStream.
     *
     * @param blocks       the blocks to save
     * @param outputStream the output stream to save to
     * @throws java.io.IOException if there's an error saving
     */
    public void save(Blocks blocks, OutputStream outputStream) throws IOException;

}
