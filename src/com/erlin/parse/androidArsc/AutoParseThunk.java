package com.erlin.parse.androidArsc;

import com.erlin.parse.androidArsc.type.*;
import com.erlin.parse.util.Utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

public class AutoParseThunk {
    private static int offsets = 0;
    private static int chunkTypeOffsets = 0;
    public static ResTableHeader mResTableHeader = null;
    public static ResStringPoolHeader mResStringPoolHeader = null;
    public static ResTablePackage mResTablePackage = null;
    public static int fileSize = 0;

    public static void main(String[] args) {
        byte[] resourceArscBytes = Utils.readFileToByteArray("./files/resources.arsc");
        fileSize = resourceArscBytes.length;
        while (offsets < fileSize) {
            int resourceType = Utils.bytes2Short(Utils.copyBytes(resourceArscBytes, offsets, 2));
            System.out.println(Integer.toHexString(resourceType));
            switch (resourceType) {
                case ResourceType.RES_TABLE_TYPE:
                    mResTableHeader = parseResourcTableTypeChunk(resourceArscBytes);
                    if (mResTableHeader != null) {
                        offsets = mResTableHeader.getOffsets();
                        System.out.println("ResTableHeader :" + mResTableHeader);
                    }
                    break;
                case ResourceType.RES_STRING_POOL_TYPE:
                    mResStringPoolHeader = parseResourceStringPoolHeaderChunk(resourceArscBytes, offsets);
                    if (mResStringPoolHeader != null) {
                        System.out.println("RES_STRING_POOL_TYPE stringContentPool : "+ Arrays.toString(mResStringPoolHeader.mStringPool.toArray()));
                        System.out.println("RES_STRING_POOL_TYPE styleContentPool : "+ Arrays.toString(mResStringPoolHeader.mStylePool.toArray()));

                        offsets += mResStringPoolHeader.resChunkHeader.size;
                    }
                    break;
                case ResourceType.RES_TABLE_PACKAGE_TYPE:
                    mResTablePackage = parseResourceTablePackageChunk(resourceArscBytes);
                    if (mResTablePackage != null && mResStringPoolHeader != null) {
                        System.out.println("RES_TABLE_PACKAGE_TYPE typeStringPool : "+ Arrays.toString(mResTablePackage.typeStringsPool.mStringPool.toArray()));
                        System.out.println("RES_TABLE_PACKAGE_TYPE keyStringPool : "+ Arrays.toString(mResTablePackage.keyStringsPool.mStylePool.toArray()));

                        offsets += mResTablePackage.resChunkHeader.size;
                    }
                    break;

            }
        }

        System.out.println("offsets:"+offsets+" ,chunkTypeOffsets:"+chunkTypeOffsets);

        while (chunkTypeOffsets<fileSize){
            ResChunkHeader chunkHeader = getResourceChunkHeader(resourceArscBytes,chunkTypeOffsets);
            if(ResourceType.RES_TABLE_TYPE_SPEC_TYPE == chunkHeader.type){
                ResTableTypeSpec mResTableTypeSpec = parseResTableTypeSpecChunk(resourceArscBytes);
                System.out.println("ResTableTypeSpec -> "+mResTableTypeSpec);
            }else {
                ResTableType mResTableType = parseResTableTypeChunk(resourceArscBytes,chunkTypeOffsets);
                System.out.println("mResTableType -> "+mResTableType);
            }

        }
    }

    public static ResTableType parseResTableTypeChunk(byte[] bytes,int offsets){
        ResTableType mResTableType = new ResTableType();
        mResTableType.resChunkHeader = getResourceChunkHeader(bytes,offsets);
        byte[] idBytes = Utils.copyBytes(bytes,offsets+8,1);
        mResTableType.id = (byte)(idBytes[0]&0xFF);

        byte[] res0Bytes = Utils.copyBytes(bytes,offsets+9,1);
        mResTableType.res0 = (byte)(res0Bytes[0]&0xFF);
        mResTableType.res1 = Utils.bytes2Short(Utils.copyBytes(bytes,offsets+10,2));

        mResTableType.entryCount = Utils.bytes2Int(Utils.copyBytes(bytes,offsets+12,4));
        mResTableType.entriesStart = Utils.bytes2Int(Utils.copyBytes(bytes,offsets+16,4));

        offsets+=20+mResTableType.resChunkHeader.size;
        chunkTypeOffsets = offsets;
        return mResTableType;
    }

    public static ResTableTypeSpec parseResTableTypeSpecChunk(byte[] bytes){
        if(!Utils.checkBytes(bytes)){
            return null;
        }
        ResTableTypeSpec mResTableTypeSpec = new ResTableTypeSpec();
        mResTableTypeSpec.resChunkHeader = getResourceChunkHeader(bytes,chunkTypeOffsets);
        byte[] idBytes = Utils.copyBytes(bytes,chunkTypeOffsets+8,1);
        mResTableTypeSpec.id = (byte) (idBytes[0]&0xFF);
        byte[] res0Bytes = Utils.copyBytes(bytes,chunkTypeOffsets+9,1);
        mResTableTypeSpec.res0 = (byte)(res0Bytes[0]&0xFF);
        mResTableTypeSpec.res1 = Utils.bytes2Short(Utils.copyBytes(bytes,chunkTypeOffsets+10,2));
        mResTableTypeSpec.entryCount = Utils.bytes2Int(Utils.copyBytes(bytes,chunkTypeOffsets+12,4));

        int[] entryIndexs = new int[mResTableTypeSpec.entryCount];
        int entryOffsets = chunkTypeOffsets+16+mResTableTypeSpec.resChunkHeader.headerSize;

        for(int i=0;i<mResTableTypeSpec.entryCount;i++){
            entryIndexs[i]=Utils.bytes2Int(Utils.copyBytes(bytes,entryOffsets+i*4,4));
        }

        entryOffsets+=mResTableTypeSpec.entryCount*4;
        chunkTypeOffsets = entryOffsets;
        return mResTableTypeSpec;
    }

    public static ResTablePackage parseResourceTablePackageChunk(byte[] bytes) {
        if (!Utils.checkBytes(bytes)) {
            return null;
        }

        ResTablePackage mResTablePackage = new ResTablePackage();
        mResTablePackage.resChunkHeader = getResourceChunkHeader(bytes, offsets);
        mResTablePackage.packageId = Utils.bytes2Int(Utils.copyBytes(bytes, offsets + 8, 4));
        mResTablePackage.packageName = new String(Utils.copyBytes(bytes, offsets + 12, 128 * 2));
        mResTablePackage.name = mResTablePackage.packageName.toCharArray();
        mResTablePackage.typeStrings = Utils.bytes2Int(Utils.copyBytes(bytes, offsets + 12 + 128 * 2, 4));
        mResTablePackage.lastPublicType = Utils.bytes2Int(Utils.copyBytes(bytes, offsets + 16 + 128 * 2, 4));
        mResTablePackage.keyStrings = Utils.bytes2Int(Utils.copyBytes(bytes, offsets + 20 + 128 * 2, 4));
        mResTablePackage.lastPublicKey = Utils.bytes2Int(Utils.copyBytes(bytes, offsets + 24 + 128 * 2, 4));

        //提取资源类型字符串池
        int typeStringsOffsets = offsets + mResTablePackage.typeStrings;
        mResTablePackage.typeStringsPool = parseResourceStringPoolHeaderChunk(bytes, typeStringsOffsets);

        //提取资源项名称字符串池
        int keyStringsOffsets = offsets + mResTablePackage.keyStrings;
        mResTablePackage.keyStringsPool = parseResourceStringPoolHeaderChunk(bytes, keyStringsOffsets);
        return mResTablePackage;
    }

    public static ResStringPoolHeader parseResourceStringPoolHeaderChunk(byte[] bytes, int offsets) {
        if (!Utils.checkBytes(bytes)) {
            return null;
        }
        ResStringPoolHeader mResStringPoolHeader = new ResStringPoolHeader();
        mResStringPoolHeader.resChunkHeader = getResourceChunkHeader(bytes, offsets);
        mResStringPoolHeader.stringCount = Utils.bytes2Int(Utils.copyBytes(bytes, offsets + 8, 4));
        mResStringPoolHeader.styleCount = Utils.bytes2Int(Utils.copyBytes(bytes, offsets + 12, 4));
        mResStringPoolHeader.flags = Utils.bytes2Int(Utils.copyBytes(bytes, offsets + 16, 4));
        mResStringPoolHeader.stringsStart = Utils.bytes2Int(Utils.copyBytes(bytes, offsets + 20, 4));
        mResStringPoolHeader.stylesStart = Utils.bytes2Int(Utils.copyBytes(bytes, offsets + 24, 4));

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
        int styleOffsets = stringOffsets + mResStringPoolHeader.stringCount * 4;
        for (int i = 0; i < mResStringPoolHeader.styleCount; i++) {
            styleIndexArray[i] = Utils.bytes2Int(Utils.copyBytes(bytes, styleOffsets + i * 4, 4));
        }

        //提取字符串池
        int stringContentIndex = styleOffsets + mResStringPoolHeader.styleCount * 4;
        stringContentIndex = extractStringList(mResStringPoolHeader.mStringPool, bytes, mResStringPoolHeader.stringCount, stringContentIndex);

        //提取字符样式串
        extractStringList(mResStringPoolHeader.mStylePool, bytes, mResStringPoolHeader.styleCount, stringContentIndex);

        chunkTypeOffsets = offsets+mResStringPoolHeader.resChunkHeader.size;
        return mResStringPoolHeader;
    }

    public static ResTableHeader parseResourcTableTypeChunk(byte[] bytes) {
        if (!Utils.checkBytes(bytes)) {
            return null;
        }
        ResTableHeader mResTableHeader = new ResTableHeader();
        mResTableHeader.resChunkHeader = getResourceChunkHeader(bytes, offsets);
        mResTableHeader.packageCount = Utils.bytes2Int(Utils.copyBytes(bytes, offsets + 8, 4));
        return mResTableHeader;
    }

    public static ResChunkHeader getResourceChunkHeader(byte[] bytes, int offsets) {
        ResChunkHeader mResChunkHeader = new ResChunkHeader();
        mResChunkHeader.type = Utils.bytes2Short(Utils.copyBytes(bytes,offsets,2));
        mResChunkHeader.headerSize = Utils.bytes2Short(Utils.copyBytes(bytes, offsets + 2, 2));
        mResChunkHeader.size = Utils.bytes2Int(Utils.copyBytes(bytes, offsets + 4, 4));
        return mResChunkHeader;
    }

    public static int extractStringList(ArrayList<String> arrayList, byte[] bytes, int count, int position) {
        for (int i = 0; i < count; i++) {
            byte[] styleLenByte = Utils.copyBytes(bytes, position, 2);
            int strLen = styleLenByte[1] & 0x7F;
            String stringContent = "";
            if (strLen != 0) {
                try {
                    stringContent = new String(Utils.copyBytes(bytes, position + 2, strLen), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    System.out.println(e.getMessage());
                }
            }
            arrayList.add(stringContent);
            position += strLen + 3;
        }
        return position;
    }
}
