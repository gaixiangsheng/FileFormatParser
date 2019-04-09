package com.erlin.parse.androidArsc.type;
/**
 * ResourceTypes.h -> struct ResTable_entry
 */
public class ResTableEntry {
    public static final int FLAG_COMPLEX = 0x0001;
    public static final int FLAG_PUBLIC = 0x0002;

    public short size;
    public short flags;

    public ResStringPoolRef key;

    @Override
    public String toString() {
        return "ResTableEntry{" +
                "size=" + size +
                ", flags=" + flags +
                ", key=" + key +
                '}';
    }
}
