package com.erlin.parse.util;


import java.io.*;

public class Utils {
    public static byte[] readAndroidManifestToByteArray(String androidManifestPath) {
        if (androidManifestPath == null || androidManifestPath.length() == 0) {
            return null;
        }

        File mManifestFile = new File(androidManifestPath);
        if (!mManifestFile.exists() || !mManifestFile.isFile()) {
            return null;
        }

        byte[] mAndroidManifestByte = null;
        FileInputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(mManifestFile);
            outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            mAndroidManifestByte = outputStream.toByteArray();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Error :" + e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error :" + e.toString());
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return mAndroidManifestByte;
    }

    public static int bytes2Short(byte[] bytes) {
        if (!Utils.checkBytes(bytes)) {
            return -1;
        }

        return (bytes[0] & 0xff) | ((bytes[1] << 8) & 0xff00);
    }

    public static int bytes2Int(byte[] bytes) {
        if (!Utils.checkBytes(bytes)) {
            return -1;
        }

        return (bytes[0] & 0xFF) | ((bytes[1] << 8) & 0xFF00) | ((bytes[2] << 24) >>> 8) | (bytes[3] << 24);
    }

    public static int bytes2Hex(byte[] bytes) {
        String hex = bytes2HexString(bytes);
        if (hex == null || hex.length() == 0) {
            return -1;
        }


        return Integer.parseInt(hex, 16);
    }


    public static String bytes2HexString(byte[] bytes) {
        if (!checkBytes(bytes)) {
            return null;
        }

        bytes = reverseBytes(bytes);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            int hex = bytes[i] & 0xFF;
            String hexStr = Integer.toHexString(hex);
            sb.append((hexStr.length() < 2 ? "0" : "") + hexStr);
        }
        return sb.toString();
    }

    public static byte[] reverseBytes(byte[] bytes) {
        if (!checkBytes(bytes)) {
            return null;
        }

        int len = bytes.length;
        int i = 0;
        byte[] dest = new byte[len];
        while (i < (len / 2)) {
            dest[i] = bytes[len - i - 1];
            dest[len - i - 1] = bytes[i];
            i++;
        }
        return dest;
    }


    public static boolean checkBytes(byte[] bytes) {
        return (bytes != null && bytes.length > 0);
    }

    public static byte[] copyBytes(byte[] srcByte, int startPos, int len) {
        if (!checkBytes(srcByte)) {
            return null;
        }

        byte[] desByte = new byte[len];
        System.arraycopy(srcByte, startPos, desByte, 0, len);
        return desByte;
    }

    public static byte[] filterInvalidBytes(byte[] bytes){
        if(!checkBytes(bytes)){
            return null;
        }

        byte[] newByte = new byte[bytes.length];
        int validLen = 0;
        for(byte b:bytes){
            if(b!=0){
                newByte[validLen]=b;
                validLen++;
            }
        }
        return copyBytes(newByte,0,validLen);
    }
}
