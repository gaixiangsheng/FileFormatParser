package com.erlin.parse.androidArsc.type;

/**
 * ResourceTypes.h -> struct ResTable_header
 */

public class ResTableHeader {
    public ResChunkHeader resChunkHeader;
    public int packageCount;//固定4字节

    public int getOffsets(){
        return resChunkHeader.getOffsets()+4;
    }

    @Override
    public String toString() {
        return "ResTableHeader{" +
                "resChunkHeader=" + resChunkHeader +
                ", packageCount=" + packageCount +
                '}';
    }
}
