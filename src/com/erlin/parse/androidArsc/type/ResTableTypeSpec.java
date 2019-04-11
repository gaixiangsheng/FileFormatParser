package com.erlin.parse.androidArsc.type;

/**
 * ResourceTypes.h -> struct ResTable_typeSpec
 */
public class ResTableTypeSpec {
    public static final int SPEC_PUBLIC = 0x40000000;

    public ResChunkHeader resChunkHeader;
    public byte id;
    public byte res0;
    public short res1;
    public int entryCount;

    @Override
    public String toString() {
        return "ResTableTypeSpec{" +
                "resChunkHeader=" + resChunkHeader +
                ", id=" + id +
                ", res0=" + res0 +
                ", res1=" + res1 +
                ", entryCount=" + entryCount +
                '}';
    }
}
