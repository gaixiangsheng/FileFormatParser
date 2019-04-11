package com.erlin.parse.androidArsc.type;

/**
 * ResourceTypes.h -> struct ResTable_typeSpec
 */
public class ResTableTypeSpec {
    public static final int SPEC_PUBLIC = 0x40000000;

    public ResChunkHeader resChunkHeader;
    byte id;
    byte res0;
    short res1;

    @Override
    public String toString() {
        return "ResTableTypeSpec{" +
                "resChunkHeader=" + resChunkHeader +
                ", packageId=" + id +
                ", res0=" + res0 +
                ", res1=" + res1 +
                '}';
    }
}
