package com.erlin.parse.androidArsc.type;

import java.util.Arrays;

/**
 * ResourceTypes.h -> struct ResTable_config
 */
public class ResTableConfig {
    //uiMode
    public final static int MASK_UI_MODE_TYPE = 0;
    public final static int UI_MODE_TYPE_ANY = 0x00;
    public final static int UI_MODE_TYPE_NORMAL =  0x01;
    public final static int UI_MODE_TYPE_DESK = 0x02;
    public final static int UI_MODE_TYPE_CAR = 0x03;
    public final static int UI_MODE_TYPE_TELEVISION = 0x04;
    public final static int UI_MODE_TYPE_APPLIANCE = 0x05;
    public final static int UI_MODE_TYPE_WATCH = 0x06;
    public final static int MASK_UI_MODE_NIGHT = 0;
    public final static int SHIFT_UI_MODE_NIGHT = 0;
    public final static int UI_MODE_NIGHT_ANY = 0x00;
    public final static int UI_MODE_NIGHT_NO = 0x01;
    public final static int UI_MODE_NIGHT_YES = 0x02;

    //screenLayout
    public final static int MASK_SCREENSIZE = 0;
    public final static int SCREENSIZE_ANY = 0x00;
    public final static int SCREENSIZE_SMALL = 0x01;
    public final static int SCREENSIZE_NORMAL = 0x02;
    public final static int SCREENSIZE_LARGE = 0x03;
    public final static int SCREENSIZE_XLARGE = 0x04;
    public final static int MASK_SCREENLONG = 0;
    public final static int SHIFT_SCREENLONG = 0;
    public final static int SCREENLONG_ANY = 0x00;
    public final static int SCREENLONG_NO = 0x01;
    public final static int SCREENLONG_YES = 0x02;
    public final static int MASK_LAYOUTDIR = 0;
    public final static int SHIFT_LAYOUTDIR = 0;
    public final static int LAYOUTDIR_ANY = 0x00;
    public final static int LAYOUTDIR_LTR =  0x01;
    public final static int LAYOUTDIR_RTL = 0x02;
    public int size;

    public short mcc;
    public short mnc;
    public int imsi;

    public char[] language = new char[2];
    public char[] country = new char[2];
    public int locale;


    byte orientation;
    byte touchscreen;
    short density;
    int screenType;


    public byte keyboard;
    public byte navigation;
    public byte inputFlags;
    public byte inputPad0;
    public int input;

    public static final int SCREENWIDTH_ANY = 0;
    public static final int SCREENHEIGHT_ANY = 0;

    public short screenWidth;
    public short screenHeight;
    public int screenSize;

    public static final int SDKVERSION_ANY = 0;
    public static final int MINORVERSION_ANY = 0;

    public short  sdkVersion;
    public short  minorVersion;
    public int  version;




    public byte screenLayout;
    public byte uiMode;
    public short smallestScreenWidthDp;
    public int screenConfig;


    public short screenWidthDp;
    public short screenHeightDp;
    public int screenSizeDp;

    @Override
    public String toString() {
        return "ResTableConfig{" +
                "size=" + size +
                ", mcc=" + mcc +
                ", mnc=" + mnc +
                ", imsi=" + imsi +
                ", language=" + Arrays.toString(language) +
                ", country=" + Arrays.toString(country) +
                ", locale=" + locale +
                ", orientation=" + orientation +
                ", touchscreen=" + touchscreen +
                ", density=" + density +
                ", screenType=" + screenType +
                ", keyboard=" + keyboard +
                ", navigation=" + navigation +
                ", inputFlags=" + inputFlags +
                ", inputPad0=" + inputPad0 +
                ", input=" + input +
                ", screenWidth=" + screenWidth +
                ", screenHeight=" + screenHeight +
                ", screenSize=" + screenSize +
                ", sdkVersion=" + sdkVersion +
                ", minorVersion=" + minorVersion +
                ", version=" + version +
                ", screenLayout=" + screenLayout +
                ", uiMode=" + uiMode +
                ", smallestScreenWidthDp=" + smallestScreenWidthDp +
                ", screenConfig=" + screenConfig +
                ", screenWidthDp=" + screenWidthDp +
                ", screenHeightDp=" + screenHeightDp +
                ", screenSizeDp=" + screenSizeDp +
                '}';
    }
}
