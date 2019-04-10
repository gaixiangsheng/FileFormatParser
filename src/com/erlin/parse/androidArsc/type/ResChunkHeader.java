package com.erlin.parse.androidArsc.type;

/**
 * ResourceTypes.h -> struct ResChunk_header
 * resources.arsc文件是有一系列的Chunk构成，
 * 每一个chunk均包含如下结构的ResChunk_header,
 * 用来描述这个chunk的基本信息。
 */
public class ResChunkHeader {
    public short type;//2个字节
    public short headerSize;//2个字节
    public int size;//4个字节

    public int getOffsets(){
        return 2+2+4;
    }

    @Override
    public String toString() {
        return "ResChunkHeader{" +
                "type=" + type +
                ", headerSize=" + headerSize +
                ", size=" + size +
                '}';
    }
}
