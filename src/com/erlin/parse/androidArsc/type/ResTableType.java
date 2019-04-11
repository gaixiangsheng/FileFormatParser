package com.erlin.parse.androidArsc.type;

/**
 * ResourceTypes.h -> struct ResTable_type
 */
public class ResTableType {
    public static final int NO_ENTRY = 0xFFFFFFFF;
    public ResChunkHeader resChunkHeader;
    public byte id;
    public byte res0;
    public short res1;
    public int entryCount;
    public int entriesStart;
    public ResTableConfig resTableConfig;

    @Override
    public String toString() {
        return "ResTableType{" +
                "resChunkHeader=" + resChunkHeader +
                ", packageId=" + id +
                ", res0=" + res0 +
                ", res1=" + res1 +
                ", entryCount=" + entryCount +
                ", entriesStart=" + entriesStart +
                ", resTableConfig=" + resTableConfig +
                '}';
    }
}
