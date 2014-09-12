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

package org.jnbt;

//@formatter:off

//@formatter:on

import java.nio.charset.Charset;

/**
 * A class which holds constant values.
 *
 * @author Graham Edgecombe, Jocopa3
 */
public final class NBTConstants {

    /**
     * The character set used by NBT (UTF-8).
     */
    public static final Charset CHARSET = Charset.forName("UTF-8");

    /**
     * Tag type constants.
     */
    public static final int TYPE_END = 0, TYPE_BYTE = 1, TYPE_SHORT = 2,
            TYPE_INT = 3, TYPE_LONG = 4, TYPE_FLOAT = 5, TYPE_DOUBLE = 6,
            TYPE_BYTE_ARRAY = 7, TYPE_STRING = 8, TYPE_LIST = 9,
            TYPE_COMPOUND = 10, TYPE_INT_ARRAY = 11;

    /**
     * Default private constructor.
     */
    private NBTConstants() {

    }

}
