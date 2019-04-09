package com.erlin.parse.androidArsc.type;

/**
 * ResourceTypes.h -> struct ResTable_header
 */

public class ResTableHeader {
    public ResChunkHeader resChunkHeader;
    public int packageCount;

    @Override
    public String toString() {
        return "ResTableHeader{" +
                "resChunkHeader=" + resChunkHeader +
                ", packageCount=" + packageCount +
                '}';
    }
}
