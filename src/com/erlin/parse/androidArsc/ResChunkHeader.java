package com.erlin.parse.androidArsc;

/**
 * ResourceTypes.h -> struct ResChunk_header
 */
public class ResChunkHeader {
    public short type;
    public short headerSize;
    public int size;

    @Override
    public String toString() {
        return "ResChunkHeader{" +
                "type=" + type +
                ", headerSize=" + headerSize +
                ", size=" + size +
                '}';
    }
}
