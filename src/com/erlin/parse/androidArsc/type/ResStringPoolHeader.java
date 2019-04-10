package com.erlin.parse.androidArsc.type;

import java.util.ArrayList;

/**
 * ResourceTypes.h -> struct ResStringPool_header
 */
public class ResStringPoolHeader {
    public static final int SORTED_FLAG = 1<<0;
    public static final int UTF8_FLAG = 1<<8;

    public ResChunkHeader resChunkHeader;
    public int stringCount;//字符串总数
    public int styleCount;//字符串样式总数
    public int flags;//标志
    public int stringsStart;//字符串内容相对于头部的偏移量
    public int stylesStart;//字符串样式内容相对于头部的偏移量

    //提取String，存放到mStringPool中
    public ArrayList<String> mStringPool;
    //提取Style，存放到mStylePool中
    public ArrayList<String> mStylePool;

    public int getOffsets(){
        return resChunkHeader.getOffsets()+4+4+4+4+4;
    }

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
