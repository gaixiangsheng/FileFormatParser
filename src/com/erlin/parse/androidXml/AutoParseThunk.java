package com.erlin.parse.androidXml;

import com.erlin.parse.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AutoParseThunk {
    public static final String STRING_CHUNK = "0x001c0001";
    public static final String RESOURCE_CHUNK = "0x00080180";
    public static final String START_NAMESPACE_CHUNK = "0x00100100";
    public static final String END_NAMESPACE_CHUNK = "0x00100101";
    public static final String START_TAG_CHUNK = "0x00100102";
    public static final String END_TAG_CHUNK = "0x00100103";
    public static final String TEXT_TCHUNK = "0x00100104";

    private static int offsets = 8;
    private static ArrayList<String> stringChunkContent = new ArrayList<>();
    private static ArrayList<Integer> resourceIdList = new ArrayList<>();
    private static Map<String, String> uriMap = new HashMap<>();
    private static Map<String, String> prefixMap = new HashMap<>();
    private static ArrayList<AttributeData> attributeDataList = new ArrayList<>();

    public static void main(String[] args) {
        //byte[] manifest = Utils.readAndroidManifestToByteArray("./files/AndroidManifest.xml");
        byte[] manifest = Utils.readAndroidManifestToByteArray("./files/abc_alert_dialog_material.xml");

        int fileSize = Utils.bytes2Int(Utils.copyBytes(manifest, 4, 4));
        while (offsets < fileSize) {
            String hexChunk = "0x" + Utils.bytes2HexString(Utils.copyBytes(manifest, offsets, 4));
            switch (hexChunk) {
                case STRING_CHUNK:
                    System.out.println("STRING_CHUNK");
                    parseStringChunk(manifest);
                    break;
                case RESOURCE_CHUNK:
                    System.out.println("RESOURCE_CHUNK");
                    parseResourceChunk(manifest);
                    break;
                case START_NAMESPACE_CHUNK:
                    System.out.println("START_NAMESPACE_CHUNK");
                    parseStartNamespaceChunk(manifest, true);
                    break;
                case END_NAMESPACE_CHUNK:
                    System.out.println("END_NAMESPACE_CHUNK");
                    parseEndNamespaceChunk(manifest);
                    break;
                case START_TAG_CHUNK:
                    System.out.println("START_TAG_CHUNK");
                    parseStartTagChunk(manifest);
                    break;
                case END_TAG_CHUNK:
                    System.out.println("END_TAG_CHUNK");
                    parseEndTagChunk(manifest);
                    break;
                case TEXT_TCHUNK:
                    System.out.println("TEXT_TCHUNK");
                    parseTextChunk(manifest);
                    break;
            }
            offsets += Utils.bytes2Int(Utils.copyBytes(manifest, offsets + 4, 4));
        }
    }

    private static void parseTextChunk(byte[] bytes){
        if (!Utils.checkBytes(bytes)) {
            return;
        }
        int chunkSize = Utils.bytes2Int(Utils.copyBytes(bytes, offsets + 4, 4));
        int lineNumber = Utils.bytes2Int(Utils.copyBytes(bytes, offsets + 8, 4));
        String name = getStringChunkContent(Utils.bytes2Int(Utils.copyBytes(bytes,offsets+16,4)));
        System.out.println("chunkSize:" + chunkSize);
        System.out.println("lineNumber:" + lineNumber);
        System.out.println("name:" + name);
    }

    private static void parseEndTagChunk(byte[] bytes){
        if (!Utils.checkBytes(bytes)) {
            return;
        }
        int chunkSize = Utils.bytes2Int(Utils.copyBytes(bytes, offsets + 4, 4));
        int lineNumber = Utils.bytes2Int(Utils.copyBytes(bytes, offsets + 8, 4));

        String namespaceUri = getStringChunkContent(Utils.bytes2Int(Utils.copyBytes(bytes, offsets + 16, 4)));
        String name = getStringChunkContent(Utils.bytes2Int(Utils.copyBytes(bytes, offsets + 20, 4)));

        System.out.println("chunkSize:" + chunkSize);
        System.out.println("lineNumber:" + lineNumber);
        System.out.println("namespaceUri:" + namespaceUri);
        System.out.println("name:" + name);
    }

    private static void parseStartTagChunk(byte[] bytes) {
        if (!Utils.checkBytes(bytes)) {
            return;
        }

        int chunkSize = Utils.bytes2Int(Utils.copyBytes(bytes, offsets + 4, 4));
        int lineNumber = Utils.bytes2Int(Utils.copyBytes(bytes, offsets + 8, 4));

        String namespaceUri = getStringChunkContent(Utils.bytes2Int(Utils.copyBytes(bytes, offsets + 16, 4)));
        String name = getStringChunkContent(Utils.bytes2Int(Utils.copyBytes(bytes, offsets + 20, 4)));

        int attributeCount = Utils.bytes2Int(Utils.copyBytes(bytes, offsets + 28, 4));
        int classAttribute = Utils.bytes2Int(Utils.copyBytes(bytes, offsets + 32, 4));

        System.out.println("chunkSize:" + chunkSize);
        System.out.println("lineNumber:" + lineNumber);
        System.out.println("namespaceUri:" + namespaceUri);
        System.out.println("name:" + name);
        System.out.println("attributeCount:" + attributeCount);
        System.out.println("classAttribute:" + classAttribute);

        byte[] attributesContentBytes = Utils.copyBytes(bytes, offsets + 36, attributeCount * 5 * 4);

        for (int i = 0; i < attributeCount; i++) {
            AttributeData attrData = new AttributeData();
            for (int j = 0; j < 5; j++) {
                int index = Utils.bytes2Int(Utils.copyBytes(attributesContentBytes, i * 5 * 4 + j * 4, 4));
                switch (index) {
                    case 0://namespaceuri
                        attrData.nameSpaceUri = index;
                        break;
                    case 1://name
                        attrData.name = index;
                        break;
                    case 2://value string
                        attrData.valuestring = index;
                        break;
                    case 3://type
                        attrData.type = index >> 24;
                        break;
                    case 4://data
                        attrData.data = index;
                        break;
                }
            }
            attributeDataList.add(attrData);
        }
        System.out.println("==============");
        for (int i = 0; i < attributeDataList.size(); i++) {
            AttributeData aData = attributeDataList.get(i);
            System.out.println("namespaceuri = " + getStringChunkContent(aData.nameSpaceUri));
            System.out.println("name = " + getStringChunkContent(aData.name));
            System.out.println("valuestring = " + getStringChunkContent(aData.valuestring));
            System.out.println("type = " + (aData.type == -1 ? "null" : AttributeType.getAttrType(aData.type)));
            System.out.println("data = " + (aData.data == -1 ? "null" : AttributeType.getAttributeData(aData)));
            System.out.println("==============");
        }
    }

    public static String getStringChunkContent(int index) {
        if (index < 0 || index > stringChunkContent.size()) {
            return "null";
        }
        return stringChunkContent.get(index);
    }

    private static void parseEndNamespaceChunk(byte[] bytes) {
        parseStartNamespaceChunk(bytes, false);
    }

    private static void parseStartNamespaceChunk(byte[] bytes, boolean isStart) {
        if (!Utils.checkBytes(bytes)) {
            return;
        }
        int chunkSize = Utils.bytes2Int(Utils.copyBytes(bytes, offsets + 4, 4));
        int lineNumber = Utils.bytes2Int(Utils.copyBytes(bytes, offsets + 8, 4));
        int prefixIndex = Utils.bytes2Int(Utils.copyBytes(bytes, offsets + 16, 4));
        int uriIndex = Utils.bytes2Int(Utils.copyBytes(bytes, offsets + 20, 4));

        String uri = stringChunkContent.get(uriIndex);
        String prefix = stringChunkContent.get(prefixIndex);

        System.out.println((isStart ? "start" : "end") + " chunkSize:" + chunkSize);
        System.out.println((isStart ? "start" : "end") + " lineNumber:" + lineNumber);
        System.out.println((isStart ? "start" : "end") + " name space uri:" + uri);
        System.out.println((isStart ? "start" : "end") + " name space prefix:" + prefix);

        uriMap.put(uri, prefix);
        prefixMap.put(prefix, uri);
    }

    private static void parseResourceChunk(byte[] bytes) {
        if (!Utils.checkBytes(bytes)) {
            return;
        }

        int chunkSize = Utils.bytes2Int(Utils.copyBytes(bytes, offsets + 4, 4));
        int resourceIds = (Utils.bytes2Int(Utils.copyBytes(bytes, offsets + 8, 4)) / 4 - 2) * 4;

        byte[] resourceIdsBytes = Utils.copyBytes(bytes, offsets, chunkSize);
        int resourceIdsCount = resourceIdsBytes.length / 4;

        while (resourceIdList.size() < resourceIdsCount) {
            resourceIdList.add(Utils.bytes2Int(Utils.copyBytes(resourceIdsBytes, resourceIdList.size() * 4, 4)));
            System.out.println(resourceIdList.get(resourceIdList.size() - 1));
        }
    }

    private static void parseStringChunk(byte[] bytes) {
        if (!Utils.checkBytes(bytes)) {
            return;
        }
        int stringOffsetsPos = 8 + Utils.bytes2Int(Utils.copyBytes(bytes, 28, 4));
        int stringCount = Utils.bytes2Int(Utils.copyBytes(bytes, 16, 4));
        int chunkTypeSize = Utils.bytes2Int(Utils.copyBytes(bytes, 12, 4));

        byte[] stringContentBytes = Utils.copyBytes(bytes, stringOffsetsPos, chunkTypeSize);
        int firstStringPosition = Utils.bytes2Short(Utils.copyBytes(stringContentBytes, 0, 2)) * 2;
        stringChunkContent.add(new String(Utils.filterInvalidBytes(Utils.copyBytes(stringContentBytes, 2, firstStringPosition))));
        firstStringPosition += 2 + 2;
        while (stringChunkContent.size() < stringCount) {
            int size = Utils.bytes2Short(Utils.copyBytes(stringContentBytes, firstStringPosition, 2)) * 2;
            stringChunkContent.add(new String(Utils.filterInvalidBytes(Utils.copyBytes(stringContentBytes, firstStringPosition + 2, size))));
            firstStringPosition += size + 4;
        }
    }
}
