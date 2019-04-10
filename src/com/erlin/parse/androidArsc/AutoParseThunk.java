package com.erlin.parse.androidArsc;

import com.erlin.parse.androidArsc.type.ResChunkHeader;
import com.erlin.parse.androidArsc.type.ResStringPoolHeader;
import com.erlin.parse.androidArsc.type.ResTableHeader;
import com.erlin.parse.androidArsc.type.ResourceType;
import com.erlin.parse.util.Utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class AutoParseThunk {
    private static int offsets = 0;
    public static ResTableHeader mResTableHeader = null;
    public static ResStringPoolHeader mResStringPoolHeader = null;
    public static int fileSize = 0;

    public static void main(String[] args) {
        byte[] resourceArscBytes = Utils.readFileToByteArray("./files/resources.arsc");
        fileSize = resourceArscBytes.length;
        while (offsets < fileSize) {
            int resourceType = Utils.bytes2Short(Utils.copyBytes(resourceArscBytes, offsets, 2));
            switch (resourceType) {
                case ResourceType.RES_TABLE_TYPE:
                    parseResourcTableTypeChunk(resourceArscBytes);
                    offsets = mResTableHeader.getOffsets();
                    break;
                case ResourceType.RES_STRING_POOL_TYPE:
                    parseResourceStringPoolHeaderChunk(resourceArscBytes);
                    offsets += mResStringPoolHeader.getOffsets();
                    break;
            }
        }

    }

    public static void parseResourceStringPoolHeaderChunk(byte[] bytes) {
        if (!Utils.checkBytes(bytes)) {
            return;
        }
        mResStringPoolHeader = new ResStringPoolHeader();
        mResStringPoolHeader.resChunkHeader = getResourceChunkHeader(bytes, ResourceType.RES_STRING_POOL_TYPE, offsets);
        mResStringPoolHeader.stringCount = Utils.bytes2Int(Utils.copyBytes(bytes, offsets + 8, 4));
        mResStringPoolHeader.styleCount = Utils.bytes2Int(Utils.copyBytes(bytes, offsets + 12, 4));
        mResStringPoolHeader.flags = Utils.bytes2Int(Utils.copyBytes(bytes, offsets + 16, 4));
        mResStringPoolHeader.stringsStart = Utils.bytes2Int(Utils.copyBytes(bytes, offsets + 20, 4));
        mResStringPoolHeader.stylesStart = Utils.bytes2Int(Utils.copyBytes(bytes, offsets + 24, 4));

        System.out.println("ResStringPoolHeader:" + mResStringPoolHeader);
        mResStringPoolHeader.mStringPool = new ArrayList<>(mResStringPoolHeader.stringCount);
        mResStringPoolHeader.mStylePool = new ArrayList<>(mResStringPoolHeader.styleCount);

        //提取每个字符串的偏移位置
        int[] stringIndexArray = new int[mResStringPoolHeader.stringCount];
        int stringOffsets = offsets + 20;
        for (int i = 0; i < mResStringPoolHeader.stringCount; i++) {
            stringIndexArray[i] = Utils.bytes2Int(Utils.copyBytes(bytes, stringOffsets + i * 4, 4));
        }

        //提取每个样式串的偏移位置
        int[] styleIndexArray = new int[mResStringPoolHeader.styleCount];
        int styleOffsets = stringOffsets+mResStringPoolHeader.stringCount*4;
        for(int i=0;i<mResStringPoolHeader.styleCount;i++){
            styleIndexArray[i] = Utils.bytes2Int(Utils.copyBytes(bytes,styleOffsets+i*4,4));
        }

        //提取字符串池
        int stringContentIndex = styleOffsets+mResStringPoolHeader.styleCount*4;
        stringContentIndex = extractStringList(mResStringPoolHeader.mStringPool,bytes,mResStringPoolHeader.stringCount,stringContentIndex);

        //提取字符样式串
        int styleContentIndex = stringContentIndex;
        styleContentIndex = extractStringList(mResStringPoolHeader.mStylePool,bytes,mResStringPoolHeader.styleCount,styleContentIndex);
        offsets = styleContentIndex;
    }

    public static void parseResourcTableTypeChunk(byte[] bytes) {
        if (!Utils.checkBytes(bytes)) {
            return;
        }
        mResTableHeader = new ResTableHeader();
        mResTableHeader.resChunkHeader = getResourceChunkHeader(bytes, ResourceType.RES_TABLE_TYPE, offsets);
        mResTableHeader.packageCount = Utils.bytes2Int(Utils.copyBytes(bytes, offsets + 8, 4));
        System.out.println("ResTableHeader :" + mResTableHeader);
    }

    public static ResChunkHeader getResourceChunkHeader(byte[] bytes, short type, int offsets) {
        ResChunkHeader mResChunkHeader = new ResChunkHeader();
        mResChunkHeader.type = type;
        mResChunkHeader.headerSize = Utils.bytes2Short(Utils.copyBytes(bytes, offsets + 2, 2));
        mResChunkHeader.size = Utils.bytes2Int(Utils.copyBytes(bytes, offsets + 4, 4));
        return mResChunkHeader;
    }

    public static int extractStringList(ArrayList<String> arrayList,byte[] bytes,int count,int position){
        System.out.println("start position = "+position);
        for(int i=0;i<count;i++){
            byte[] styleLenByte = Utils.copyBytes(bytes,position,2);
            int strLen = styleLenByte[1]&0x7F;
            String stringContent = "";
            if(strLen != 0){
                try {
                    stringContent = new String(Utils.copyBytes(bytes,position+2,strLen),"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    System.out.println(e.getMessage());
                }
            }
            arrayList.add(stringContent);
            position+=strLen+3;
            System.out.println("index : "+i+" string = "+stringContent);
        }
        System.out.println("end position = "+position);
        return position;
    }
}
