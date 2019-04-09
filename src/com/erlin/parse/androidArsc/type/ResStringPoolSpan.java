package com.erlin.parse.androidArsc.type;

/**
 * ResourceTypes.h -> struct ResStringPool_span
 */
public class ResStringPoolSpan {
    public static final int END = 0xFFFFFFFF;
    public ResStringPoolRef name;
    public int firstChar;
    public int lastChar;

    @Override
    public String toString() {
        return "ResStringPoolSpan{" +
                "name=" + name +
                ", firstChar=" + firstChar +
                ", lastChar=" + lastChar +
                '}';
    }
}
