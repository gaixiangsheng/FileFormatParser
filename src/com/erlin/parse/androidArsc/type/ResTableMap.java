package com.erlin.parse.androidArsc.type;

/**
 * ResourceTypes.h -> struct ResTable_map
 */
public class ResTableMap {
    ResTableRef name;
    ResValue value;

    @Override
    public String toString() {
        return "ResTableMap{" +
                "name=" + name +
                ", value=" + value +
                '}';
    }
}
