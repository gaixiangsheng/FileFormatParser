package com.erlin.parse.androidArsc;

/**
 * ResourceTypes.h -> struct Res_value
 */
public class ResValue {
    public short size;
    public byte res0;
    public byte dataType;
    public int data;

    // Contains no data.
    public static final int TYPE_NULL = 0x00;
    // The 'data' holds a ResTable_ref, a reference to another resource
    // table entry.
    public static final int TYPE_REFERENCE = 0x01;
    // The 'data' holds an attribute resource identifier.
    public static final int TYPE_ATTRIBUTE = 0x02;
    // The 'data' holds an index into the containing resource table's
    // global value string pool.
    public static final int TYPE_STRING = 0x03;
    // The 'data' holds a single-precision floating point number.
    public static final int TYPE_FLOAT = 0x04;
    // The 'data' holds a complex number encoding a dimension value,
    // such as "100in".
    public static final int TYPE_DIMENSION = 0x05;
    // The 'data' holds a complex number encoding a fraction of a
    // container.
    public static final int TYPE_FRACTION = 0x06;
    // Beginning of integer flavors...
    public static final int TYPE_FIRST_INT = 0x10;
    // The 'data' is a raw integer value of the form n..n.
    public static final int TYPE_INT_DEC = 0x10;
    // The 'data' is a raw integer value of the form 0xn..n.
    public static final int TYPE_INT_HEX = 0x11;
    // The 'data' is either 0 or 1, for input "false" or "true" respectively.
    public static final int TYPE_INT_BOOLEAN = 0x12;
    // Beginning of color integer flavors...
    public static final int TYPE_FIRST_COLOR_INT = 0x1c;
    // The 'data' is a raw integer value of the form #aarrggbb.
    public static final int TYPE_INT_COLOR_ARGB8 = 0x1c;
    // The 'data' is a raw integer value of the form #rrggbb.
    public static final int TYPE_INT_COLOR_RGB8 = 0x1d;
    // The 'data' is a raw integer value of the form #argb.
    public static final int TYPE_INT_COLOR_ARGB4 = 0x1e;
    // The 'data' is a raw integer value of the form #rgb.
    public static final int TYPE_INT_COLOR_RGB4 = 0x1f;
    // ...end of integer flavors.
    public static final int TYPE_LAST_COLOR_INT = 0x1f;
    // ...end of integer flavors.
    public static final int TYPE_LAST_INT = 0x1f;

    public static final int COMPLEX_UNIT_SHIFT = 0;
    public static final int COMPLEX_UNIT_MASK = 0xf;

    // TYPE_DIMENSION: Value is raw pixels.
    public static final int COMPLEX_UNIT_PX = 0;
    // TYPE_DIMENSION: Value is Device Independent Pixels.
    public static final int COMPLEX_UNIT_DIP = 1;
    // TYPE_DIMENSION: Value is a Scaled device independent Pixels.
    public static final int COMPLEX_UNIT_SP = 2;
    // TYPE_DIMENSION: Value is in points.
    public static final int COMPLEX_UNIT_PT = 3;
    // TYPE_DIMENSION: Value is in inches.
    public static final int COMPLEX_UNIT_IN = 4;
    // TYPE_DIMENSION: Value is in millimeters.
    public static final int COMPLEX_UNIT_MM = 5;

    // TYPE_FRACTION: A basic fraction of the overall size.
    public static final int COMPLEX_UNIT_FRACTION = 0;
    // TYPE_FRACTION: A fraction of the parent size.
    public static final int COMPLEX_UNIT_FRACTION_PARENT = 1;

    // Where the radix information is, telling where the decimal place
    // appears in the mantissa.  This give us 4 possible fixed point
    // representations as defined below.
    public static final int COMPLEX_RADIX_SHIFT = 4;
    public static final int COMPLEX_RADIX_MASK = 0x3;

    // The mantissa is an integral number -- i.e., 0xnnnnnn.0
    public static final int COMPLEX_RADIX_23p0 = 0;
    // The mantissa magnitude is 16 bits -- i.e, 0xnnnn.nn
    public static final int COMPLEX_RADIX_16p7 = 1;
    // The mantissa magnitude is 8 bits -- i.e, 0xnn.nnnn
    public static final int COMPLEX_RADIX_8p15 = 2;
    // The mantissa magnitude is 0 bits -- i.e, 0x0.nnnnnn
    public static final int COMPLEX_RADIX_0p23 = 3;

    // Where the actual value is.  This gives us 23 bits of
    // precision.  The top bit is the sign.
    public static final int COMPLEX_MANTISSA_SHIFT = 8;
    public static final int COMPLEX_MANTISSA_MASK = 0xffffff;

    @Override
    public String toString() {
        return "ResValue{" +
                "size=" + size +
                ", res0=" + res0 +
                ", dataType=" + dataType +
                ", data=" + data +
                '}';
    }
}
