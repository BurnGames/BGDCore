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

package me.paulbgd.bgdcore.blocks.block.paster;

/**
 * The official Pasters supplied by Blocks.
 */
public class Paster {

    /**
     * The Simple Paster, which tries to just paste all of the blocks
     */
    public static final BlockPaster SIMPLE_PASTER = new SimplePaster();
    /**
     * The "Async" Paster, which will paste the blocks in chunks.
     */
    public static final BlockPaster ASYNC_PASTER = new AsyncPaster() {
        @Override
        public void finish() {
            try {
                throw new IllegalAccessException("You cannot stop the main async paster!");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    };

}
