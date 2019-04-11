package com.erlin.parse.androidArsc.type;

import java.util.Arrays;

/**
 * ResourceTypes.h -> struct ResTable_package
 */

public class ResTablePackage {
    public ResChunkHeader resChunkHeader;
    public String packageName;
    public int packageId;
    public char[] name = new char[128];
    public int typeStrings;
    public int lastPublicType;
    public int keyStrings;
    public int lastPublicKey;

    @Override
    public String toString() {
        return "ResTablePackage{" +
                "resChunkHeader=" + resChunkHeader +
                ", packageName='" + packageName + '\'' +
                ", packageId=" + packageId +
                ", name=" + Arrays.toString(name) +
                ", typeStrings=" + typeStrings +
                ", lastPublicType=" + lastPublicType +
                ", keyStrings=" + keyStrings +
                ", lastPublicKey=" + lastPublicKey +
                '}';
    }
}
