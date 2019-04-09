package com.erlin.parse.androidArsc.type;

/**
 * ResourceTypes.h -> struct ResTable_map_entry
 */
public class ResTableMapEntry extends ResTableEntry{
    public ResTableRef parent;
    public int count;

    @Override
    public String toString() {
        return "ResTableMapEntry{" +
                "parent=" + parent +
                ", count=" + count +
                '}';
    }
}
