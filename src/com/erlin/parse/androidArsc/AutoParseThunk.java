package com.erlin.parse.androidArsc;

import com.erlin.parse.androidArsc.type.ResChunkHeader;
import com.erlin.parse.androidArsc.type.ResStringPoolHeader;
import com.erlin.parse.androidArsc.type.ResTableHeader;
import com.erlin.parse.androidArsc.type.ResourceType;
import com.erlin.parse.util.Utils;

public class AutoParseThunk {
    private static int offsets = 0;
    public static ResTableHeader mResTableHeader = null;
    public static ResStringPoolHeader mResStringPoolHeader = null;
    public static int fileSize = 0;
    public static void main(String[] args){
        byte[] resourceArscBytes = Utils.readFileToByteArray("./files/resources.arsc");
        fileSize = resourceArscBytes.length;
        while (offsets<fileSize){
            int resourceType = Utils.bytes2Short(Utils.copyBytes(resourceArscBytes,offsets,2));
            switch (resourceType){
                case ResourceType.RES_TABLE_TYPE:
                    parseResourcTableTypeChunk(resourceArscBytes);
                    offsets = mResTableHeader.getOffsets();
                    break;
                case ResourceType.RES_STRING_POOL_TYPE:
                    parseResourceStringPoolHeaderChunk(resourceArscBytes);
                    offsets+=mResStringPoolHeader.getOffsets();
                    break;
            }
        }

    }

    public static void parseResourceStringPoolHeaderChunk(byte[] bytes){
        if(!Utils.checkBytes(bytes)){
            return;
        }
        mResStringPoolHeader = new ResStringPoolHeader();
        mResStringPoolHeader.resChunkHeader = getResourceChunkHeader(bytes,ResourceType.RES_STRING_POOL_TYPE,offsets);
        mResStringPoolHeader.stringCount = Utils.bytes2Int(Utils.copyBytes(bytes,offsets+8,4));
        mResStringPoolHeader.styleCount = Utils.bytes2Int(Utils.copyBytes(bytes,offsets+12,4));
        mResStringPoolHeader.flags = Utils.bytes2Int(Utils.copyBytes(bytes,offsets+16,4));
        mResStringPoolHeader.stringsStart = Utils.bytes2Int(Utils.copyBytes(bytes,offsets+20,4));
        mResStringPoolHeader.stylesStart = Utils.bytes2Int(Utils.copyBytes(bytes,offsets+24,4));

        System.out.println("ResStringPoolHeader:"+mResStringPoolHeader);
    }

    public static void parseResourcTableTypeChunk(byte[] bytes){
        if(!Utils.checkBytes(bytes)){
            return;
        }
        mResTableHeader = new ResTableHeader();
        mResTableHeader.resChunkHeader = getResourceChunkHeader(bytes,ResourceType.RES_TABLE_TYPE,offsets);
        mResTableHeader.packageCount = Utils.bytes2Int(Utils.copyBytes(bytes,offsets+8,4));
        System.out.println("ResTableHeader :"+mResTableHeader);
    }

    public static ResChunkHeader getResourceChunkHeader(byte[] bytes,short type,int offsets){
        ResChunkHeader mResChunkHeader = new ResChunkHeader();
        mResChunkHeader.type = type;
        mResChunkHeader.headerSize = Utils.bytes2Short(Utils.copyBytes(bytes,offsets+2,2));
        mResChunkHeader.size = Utils.bytes2Int(Utils.copyBytes(bytes,offsets+4,4));
        return mResChunkHeader;
    }

}
