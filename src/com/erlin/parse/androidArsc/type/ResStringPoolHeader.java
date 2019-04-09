package com.erlin.parse.androidArsc.type;

/**
 * ResourceTypes.h -> struct ResStringPool_header
 */
public class ResStringPoolHeader {
    public static final int SORTED_FLAG = 1<<0;
    public static final int UTF8_FLAG = 1<<8;

    public ResChunkHeader resChunkHeader;
    public int stringCount;
    public int styleCount;
    public int flags;
    public int stringsStart;
    public int stylesStart;


    @Override
    public String toString() {
        return "ResStringPoolHeader{" +
                "resChunkHeader=" + resChunkHeader +
                ", stringCount=" + stringCount +
                ", styleCount=" + styleCount +
                ", flags=" + flags +
                ", stringsStart=" + stringsStart +
                ", stylesStart=" + stylesStart +
                '}';
    }
}
