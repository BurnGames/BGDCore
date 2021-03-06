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

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * <p>
 * This class reads <strong>NBT</strong>, or <strong>Named Binary Tag</strong>
 * streams, and produces an object graph of subclasses of the <code>Tag</code>
 * object.
 * </p>
 * <p/>
 * <p>
 * The NBT format was created by Markus Persson, and the specification may be
 * found at <a href="http://www.minecraft.net/docs/NBT.txt">
 * http://www.minecraft.net/docs/NBT.txt</a>.
 * </p>
 *
 * @author Graham Edgecombe
 */
public final class NBTInputStream implements Closeable {

    /**
     * The data input stream.
     */
    private final DataInputStream is;

    /**
     * Creates a new <code>NBTInputStream</code>, which will source its data
     * from the specified input stream.
     *
     * @param is      The input stream.
     * @param gzipped Whether the stream is GZip-compressed.
     * @throws java.io.IOException if an I/O error occurs.
     */
    public NBTInputStream(InputStream is, final boolean gzipped) throws IOException {
        if (gzipped) {
            is = new GZIPInputStream(is);
        }
        this.is = new DataInputStream(is);
    }

    /**
     * Creates a new <code>NBTInputStream</code>, which will source its data
     * from the specified GZIP-compressed input stream.
     *
     * @param is The input stream.
     * @throws java.io.IOException if an I/O error occurs.
     */
    public NBTInputStream(final InputStream is) throws IOException {
        this.is = new DataInputStream(new GZIPInputStream(is));
    }

    //TODO: comment this.  supports raw Gziped data.
    // author: ensirius
    public NBTInputStream(final DataInputStream is) {
        this.is = is;
    }

    /**
     * Reads an NBT tag from the stream.
     *
     * @return The tag that was read.
     * @throws java.io.IOException if an I/O error occurs.
     */
    public Tag readTag() throws IOException {

        return readTag(0);
    }

    /**
     * Reads an NBT from the stream.
     *
     * @param depth The depth of this tag.
     * @return The tag that was read.
     * @throws java.io.IOException if an I/O error occurs.
     */
    private Tag readTag(final int depth) throws IOException {

        final int type = is.readByte() & 0xFF;

        String name;
        if (type != NBTConstants.TYPE_END) {
            final int nameLength = is.readShort() & 0xFFFF;
            final byte[] nameBytes = new byte[nameLength];
            is.readFully(nameBytes);
            name = new String(nameBytes, NBTConstants.CHARSET);
        } else {
            name = "";
        }

        return readTagPayload(type, name, depth);
    }

    /**
     * Reads the payload of a tag, given the name and type.
     *
     * @param type  The type.
     * @param name  The name.
     * @param depth The depth.
     * @return The tag.
     * @throws java.io.IOException if an I/O error occurs.
     */
    private Tag readTagPayload(final int type, final String name, final int depth)
            throws IOException {

        switch (type) {
            case NBTConstants.TYPE_END:
                if (depth == 0) {
                    throw new IOException(
                            "[JNBT] TAG_End found without a TAG_Compound/TAG_List tag preceding it.");
                } else {
                    return new EndTag();
                }
            case NBTConstants.TYPE_BYTE:
                return new ByteTag(name, is.readByte());
            case NBTConstants.TYPE_SHORT:
                return new ShortTag(name, is.readShort());
            case NBTConstants.TYPE_INT:
                return new IntTag(name, is.readInt());
            case NBTConstants.TYPE_LONG:
                return new LongTag(name, is.readLong());
            case NBTConstants.TYPE_FLOAT:
                return new FloatTag(name, is.readFloat());
            case NBTConstants.TYPE_DOUBLE:
                return new DoubleTag(name, is.readDouble());
            case NBTConstants.TYPE_BYTE_ARRAY:
                int length = is.readInt();
                byte[] bytes = new byte[length];
                is.readFully(bytes);
                return new ByteArrayTag(name, bytes);
            case NBTConstants.TYPE_STRING:
                length = is.readShort();
                bytes = new byte[length];
                is.readFully(bytes);
                return new StringTag(name, new String(bytes,
                        NBTConstants.CHARSET));
            case NBTConstants.TYPE_LIST:
                final int childType = is.readByte();
                length = is.readInt();

                final List<Tag> tagList = new ArrayList<Tag>();
                for (int i = 0; i < length; i++) {
                    final Tag tag = readTagPayload(childType, "", depth + 1);
                    if (tag instanceof EndTag) {
                        throw new IOException(
                                "[JNBT] TAG_End not permitted in a list.");
                    }
                    tagList.add(tag);
                }

                return new ListTag(name, NBTUtils.getTypeClass(childType),
                        tagList);
            case NBTConstants.TYPE_COMPOUND:
                final Map<String, Tag> tagMap = new HashMap<String, Tag>();
                while (true) {
                    final Tag tag = readTag(depth + 1);
                    if (tag instanceof EndTag) {
                        break;
                    } else {
                        tagMap.put(tag.getName(), tag);
                    }
                }

                return new CompoundTag(name, tagMap);
            case NBTConstants.TYPE_INT_ARRAY:
                length = is.readInt();
                final int[] ints = new int[length];
                for (int i = 0; i < length; i++) {
                    ints[i] = is.readInt();
                }
                return new IntArrayTag(name, ints);
            default:
                throw new IOException("[JNBT] Invalid tag type: " + type
                        + ".");
        }
    }

    @Override
    public void close() throws IOException {

        is.close();
    }
}
