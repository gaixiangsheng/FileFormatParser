# Android-Manifest.xml文件格式分析

## 获取AndroidManifest.xml
```
erlin@erlin-Terrans-Force-S5:release$ apktool d -r app-release.apk 
I: Using Apktool 2.4.1-dirty on app-release.apk
I: Copying raw resources...
I: Baksmaling classes.dex...
I: Copying assets and libs...
I: Copying unknown files...
I: Copying original files...
```
目录：
```
erlin@erlin-Terrans-Force-S5:app-release$ ll
total 380
drwxr-xr-x  5 erlin erlin   4096 3月  21 12:06 ./
drwxrwxr-x  3 erlin erlin   4096 3月  21 12:06 ../
-rw-r--r--  1 erlin erlin   2244 3月  21 12:06 AndroidManifest.xml
-rw-r--r--  1 erlin erlin   8659 3月  21 12:06 apktool.yml
drwxr-xr-x  3 erlin erlin   4096 3月  21 12:06 original/
drwxr-xr-x 44 erlin erlin   4096 3月  21 12:06 res/
-rw-r--r--  1 erlin erlin 350460 3月  21 12:06 resources.arsc
drwxr-xr-x  5 erlin erlin   4096 3月  21 12:06 smali/
```
大概浏览一下二进制的AndroidManifest.xml文件，使用sublimt text显示为十六进制内容：
![Screenshot from 2019-03-21 12-13-01.png-14.8kB][1]

## 进入解析AndroidManifest.xml的主题
AndroidManifest.xml的文件格式图：
![android-manifest.xml-文件格式.png-167.3kB][2]

***注意：***下面所有的解析部分将根据上面的图进行分析，所以上图要熟记于心里。

### 一、文件头部解析：
![image_1d6qd0d8s110ummi1n821oi516n09.png-13.2kB][3]

![image_1d6qdrp2e1koolv4lga4n819qsm.png-17.9kB][4]

|头部信息|占用空间|解释|
|---|---|---|
|Magic Number(0x00080003)|4bytes|文件魔数|
|File Size|4bytes|AndroidManifest文件大小,后面的完整解析会用到FileSize|

```java
public static void main(String[] args){
    byte[] manifestBytes = Utils.readAndroidManifestToByteArray("./AndroidManifest.xml");
    printFileHeader(manifestBytes);
}

public static void printFileHeader(byte[] xmlByte) {
    if (!Utils.checkBytes(xmlByte)) {
        return;
    }

    byte[] magicNumberBytes = Utils.copyBytes(xmlByte, 0, 4);
    byte[] fileSizeBytes = Utils.copyBytes(xmlByte, 4, 4);

    String magicNumberHex = Utils.bytes2HexString(magicNumberBytes);
    String fileSize = Utils.bytes2Int(fileSizeBytes)+" byte";
    System.out.println("MagicNumber :0x"+magicNumberHex);
    System.out.println("File Size   :"+fileSize);
}
```

### 二、String Chunk解析
![image_1d6qfkpdpmhn22fh639k01p2o13.png-44.4kB][5]
![image_1d6qg5qsk1fjv1nk8mo11g93l61g.png-15.3kB][6]
 
    1. Chunk Type(0x001c0001)：StringChunk类型，固定4字节
    2. Chunk Size：String Chunk大小，固定4字节
    3. String Count：String Chunk中字符串总数，固定4字节，解析字符串时会用到。
    4. Style Count：String Chunk中样式总数，固定4字节，实际解析过程中一直为0x00000000
    5. Unknown：未知区域，固定4字节，实际解析过程中路过不解析
    6. String Pool Offset：字符池的偏移值，固定4字节，偏移位置是相对于StringChunk头部位置即8+0x000000A0
    7. Style Pool Offset：样式池的偏移值，固定4字节
    8. String Pool：每一个字符串的偏移值，为String Count * 4字节
    9. Style Pool：每一个样式的偏移值，为Style Count * 4字节
    
#### A) 代码解析StringChunk头部信息：

```Java
public static void parseStringChunk(byte[] bytes){
    if (!Utils.checkBytes(bytes)) {
        return;
    }

    // 取出StringChunk中所有的内容体
    byte[] stringChunkTypeBytes = Utils.copyBytes(bytes,STRING_CHUNK_BASE_POSITION,4);
    byte[] stringSizeBytes = Utils.copyBytes(bytes,STRING_CHUNK_BASE_POSITION+4,4);
    byte[] stringCountBytes = Utils.copyBytes(bytes,STRING_CHUNK_BASE_POSITION+8,4);
    byte[] styleCountBytes = Utils.copyBytes(bytes,STRING_CHUNK_BASE_POSITION+12,4);
    byte[] unKnown = Utils.copyBytes(bytes,STRING_CHUNK_BASE_POSITION+16,4);
    byte[] stringPoolOffsetBytes = Utils.copyBytes(bytes,STRING_CHUNK_BASE_POSITION+20,4);
    byte[] stylePoolOffsetBytes = Utils.copyBytes(bytes,STRING_CHUNK_BASE_POSITION+24,4);
    byte[] stringOffsetsBytes = Utils.copyBytes(bytes,STRING_CHUNK_BASE_POSITION+28,4);
    byte[] styleOffsetsBytes = Utils.copyBytes(bytes,STRING_CHUNK_BASE_POSITION+32,4);

    //进行打印值
    System.out.println("StringChunkType:0x"+Utils.bytes2HexString(stringChunkTypeBytes));
    System.out.println("StringChunkSize:"+Utils.bytes2Int(stringSizeBytes));
    System.out.println("StringCount:"+Utils.bytes2Int(stringCountBytes));
    System.out.println("StyleCount:"+Utils.bytes2Int(styleCountBytes));
    System.out.println("unKnown:0x"+Utils.bytes2HexString(unKnown));
    System.out.println("stringPoolOffset:0x"+Utils.bytes2HexString(stringPoolOffsetBytes));
    System.out.println("stylePoolOffset:0x"+Utils.bytes2HexString(stylePoolOffsetBytes));
    System.out.println("stringOffsets:"+Utils.bytes2Int(stringOffsetsBytes));
    System.out.println("styleOffsets:"+Utils.bytes2Int(styleOffsetsBytes));
}
```

#### B) 解析StringChunk内容体信息
![image_1d6qma4us2g61v6q12mq1kd91e9u2q.png-49kB][7]

    1. 08为StringChunk头部位置
    2. 0x000000A0为字符池偏移值，注：字符池偏移值是相对StringChunk头部8来说的，所以8+0x000000A0 = 0x000000A8
    3. 0x0005位置即是0x000000A8的所在位置，0x0005代表字符串长度，一个字符占两个字节，所以需要0x00005*2
    4. 0x0005后面的10个字节为字符串长度
    5. 10个字节后面的00 00为字符串结束，注：UTF-8以00结尾，UTF-16以00 00结尾
    
代码解析为：
```Java
public static void parseStringChunkContent(byte[] bytes){
    if (!Utils.checkBytes(bytes)) {
        return;
    }

    // 取出StringChunk中所有的内容体
    byte[] stringSizeBytes = Utils.copyBytes(bytes,STRING_CHUNK_BASE_POSITION+4,4);
    byte[] stringCountBytes = Utils.copyBytes(bytes,STRING_CHUNK_BASE_POSITION+8,4);
    byte[] stringPoolOffsetBytes = Utils.copyBytes(bytes,STRING_CHUNK_BASE_POSITION+20,4);

    ArrayList<String> stringArrayList = new ArrayList<>();
    //1. 偏移到StringChunk字符池内容开始位置： STRING_CHUNK_BASE_POSITION + stringPoolOffsetBytes字符池的偏移值
    int start = STRING_CHUNK_BASE_POSITION+Utils.bytes2Int(stringPoolOffsetBytes);
    //2. StringChunk字符池结束位置：stringSizeBytes
    int end = Utils.bytes2Int(stringSizeBytes);
    //3. 读取start开始位置到结束位置，即为字符池的全部内容
    byte[] stringChunkContentBytes = Utils.copyBytes(bytes,start,end);

    //一个字符占两个字节，所以*2
    int firstStringSize = Utils.bytes2Short(Utils.copyBytes(stringChunkContentBytes,0,2))*2;
    //跳过字符长度的两个字节，取出字符串,并过滤无效字符
    stringArrayList.add(new String(Utils.filterInvalidBytes(Utils.copyBytes(stringChunkContentBytes,2,firstStringSize))));
    System.out.println(stringArrayList.get(stringArrayList.size()-1));//打印

    //计算字符串池中一共有多少个字符串
    int stringCount = Utils.bytes2Int(stringCountBytes);
    //每一个字符串+跳过字符长度的两个字节+跳过字符00 00结尾的字节，等于下一字符串开始的位置
    firstStringSize+=2+2;
    
    while (stringArrayList.size()<stringCount){
        //计算字符串长度
        int stringSize = Utils.bytes2Short(Utils.copyBytes(stringChunkContentBytes,firstStringSize,2))*2;
        //提取字符串
        stringArrayList.add(new String(Utils.filterInvalidBytes(Utils.copyBytes(stringChunkContentBytes,firstStringSize+2,stringSize))));
        //计算下一个字符串开始位置
        firstStringSize+=2+stringSize+2;
        System.out.println(stringArrayList.get(stringArrayList.size()-1));
    }
}
```

### 三、ResourceId Chunk解析
![image_1d6qneiem1motv5pqe51eb47gh37.png-18.4kB][8]

![image_1d77jq5lp6hl1jkgrs21t8t19ch16.png-38.7kB][9]

**<font color="#ff0000">注：50Ch的位置计算是：StringChunk位置08+StringChunk大小，即：8+0x00000504 = 1292 [50Ch]</font>**

    1. ChunkType:ResourceId Chunk类型,固定4个字节：0x00080180
    2. ChunkSize:ResourceIdChunkSize,固定4个字节
    3. ResourceIds:Resource的资源id所占字节总数(ChunkSize/4-2)*4bytes。
        (ChunkSize/4-2)*4bytes拆分:
        a).ChunkSize即:ResourceIdChunkSize(0x00000040转化为10进制是64)
        b).ChunkSize/4即：有多少个资源段，4为4个字节的意思
        c).-2即：减去ChunkType和ChunkSize的头部偏移即:(8/4)=2
        d).乘4字节：即ResourceIds所占用的空间大小
        以本例计算：
            (64/4-2)*4 = 56 [54Ch] 如下图：

![image_1d792thlrql1namlo155t1nj228.png-26.1kB][10]

代码解析：
```Java
public static void parseResourceChunk(byte[] bytes) {
    if (!Utils.checkBytes(bytes)) {
        return;
    }

    //取出ResourceChunk中所有的内容体
    int resourceOffsets = STRING_CHUNK_BASE_POSITION + 0x00000504;//StringChunkSize:0x00000504
    byte[] resourceChunkBytes = Utils.copyBytes(bytes, resourceOffsets, 4);
    byte[] resourceChunkSizeBytes = Utils.copyBytes(bytes, resourceOffsets + 4, 4);
    byte[] resourceIdsBytes = Utils.copyBytes(bytes, resourceOffsets + 8, 4);

    System.out.println("ResourceChunkType:0x" + Utils.bytes2HexString(resourceChunkBytes));
    System.out.println("ResourceChunkSize:0x" + Utils.bytes2HexString(resourceChunkSizeBytes)+" ,int :"+Utils.bytes2Int(resourceChunkSizeBytes));
    System.out.println("ResourceIds:0x" + Utils.bytes2HexString(resourceIdsBytes));

    //获取资源ID bytes数组
    byte[] resourceIdsContentBytes = Utils.copyBytes(bytes, resourceOffsets+STRING_CHUNK_BASE_POSITION, Utils.bytes2Int(resourceChunkSizeBytes) - STRING_CHUNK_BASE_POSITION);
    
    //计算有多少个资源ID
    int resouceIdCount = resourceIdsContentBytes.length / 4;
    System.out.println("Resource id Size : "+resouceIdCount);
    ArrayList<Integer> resourceIdList = new ArrayList<>(resouceIdCount);
    int index = 1;
    for (int i = 0; i < resourceIdsContentBytes.length; i += 4) {
        int resId = Utils.bytes2Int(Utils.copyBytes(resourceIdsContentBytes, i, 4));
        System.out.println((index++)+" ,resource id:" + resId + " ,hex: 0x" + Utils.bytes2HexString(Utils.copyBytes(resourceIdsContentBytes, i, 4)));
        resourceIdList.add(resId);
    }
}
```

## 四、Start Namespace Chunk
![image_1d799vuen15fnrvvmkjf81qjm9.png-28.9kB][11]
![image_1d79agct42sjje87ou1rln1qsum.png-34.3kB][12]

***注：54Ch的计算：8+0x00000504+0x00000040 = 1356 [54Ch]***

    1. ChunkType:StartNamespaceChunk类型，固定4字节0x00100100
    2. ChunkSize:StartNamespaceChunkSize大小，固定4字节
    3. LineNumber:Manifestxml的行号，固定4字节
    4. 未知类型
    5. Prefix:命名空间前缀，如：android:xxx
    6. Uri:命名空间Uri，如：http://schemas.android.com/tools 如：http://schemas.android.com/apk/res/android
    
代码分析：
```Java
public static ArrayList<String> parseStringChunkContent(byte[] bytes){
    if (!Utils.checkBytes(bytes)) {
        return null;
    }

    // 取出StringChunk中所有的内容体
    byte[] stringSizeBytes = Utils.copyBytes(bytes,STRING_CHUNK_BASE_POSITION+4,4);
    byte[] stringCountBytes = Utils.copyBytes(bytes,STRING_CHUNK_BASE_POSITION+8,4);
    byte[] stringPoolOffsetBytes = Utils.copyBytes(bytes,STRING_CHUNK_BASE_POSITION+20,4);


    ArrayList<String> stringArrayList = new ArrayList<>();
    //1. 偏移到StringChunk字符池内容开始位置： STRING_CHUNK_BASE_POSITION + stringPoolOffsetBytes字符池的偏移值
    int start = STRING_CHUNK_BASE_POSITION+Utils.bytes2Int(stringPoolOffsetBytes);
    //2. StringChunk字符池结束位置：stringSizeBytes
    int end = Utils.bytes2Int(stringSizeBytes);
    //3. 读取start开始位置到结束位置，即为字符池的全部内容
    byte[] stringChunkContentBytes = Utils.copyBytes(bytes,start,end);

    int firstStringSize = Utils.bytes2Short(Utils.copyBytes(stringChunkContentBytes,0,2))*2;//一个字符占两个字节，所以*2
    stringArrayList.add(new String(Utils.filterInvalidBytes(Utils.copyBytes(stringChunkContentBytes,2,firstStringSize))));//跳过字符长度的两个字节，取出字符串。

    int stringCount = Utils.bytes2Int(stringCountBytes);//计算字符串池中一共有多少个字符串

    firstStringSize+=2+2;//每一个字符串+跳过字符长度的两个字节+跳过字符00 00结尾的字节，等于下一字符串开始的位置
    while (stringArrayList.size()<stringCount){
        int stringSize = Utils.bytes2Short(Utils.copyBytes(stringChunkContentBytes,firstStringSize,2))*2;//计算下一个字符串长度
        stringArrayList.add(new String(Utils.filterInvalidBytes(Utils.copyBytes(stringChunkContentBytes,firstStringSize+2,stringSize))));//提取字符串
        firstStringSize+=2+stringSize+2;//计算下一个字符串开始位置
    }
    return  stringArrayList;
}


public static void parseStartNamespaceChunk(byte[] bytes) {
    if (!Utils.checkBytes(bytes)) {
        return;
    }

    int startNamespaceChunkOffsets = STRING_CHUNK_BASE_POSITION + 0x00000504 + 0x00000040;
    byte[] startNamespaceChunkBytes = Utils.copyBytes(bytes,startNamespaceChunkOffsets,4);
    byte[] startNamespaceChunkSizeBytes = Utils.copyBytes(bytes,startNamespaceChunkOffsets+4,4);
    byte[] lineNumberBytes = Utils.copyBytes(bytes,startNamespaceChunkOffsets+8,4);
    byte[] unknownBytes = Utils.copyBytes(bytes,startNamespaceChunkOffsets+12,4);
    byte[] prefixBytes = Utils.copyBytes(bytes,startNamespaceChunkOffsets+16,4);
    byte[] uriBytes = Utils.copyBytes(bytes,startNamespaceChunkOffsets+20,4);

    ArrayList<String> stringChunkContent = parseStringChunkContent(bytes);

    System.out.println("startNamespaceChunk 0x:"+Utils.bytes2HexString(startNamespaceChunkBytes));
    System.out.println("startNamespaceChunkSize 0x:"+Utils.bytes2HexString(startNamespaceChunkBytes)+" , int :"+Utils.bytes2Int(startNamespaceChunkSizeBytes));
    System.out.println("lineNumberBytes 0x:"+Utils.bytes2HexString(lineNumberBytes)+" , int :"+Utils.bytes2Int(lineNumberBytes));
    System.out.println("unknownBytes 0x:"+Utils.bytes2HexString(unknownBytes)+" , int :"+Utils.bytes2Int(unknownBytes));
    System.out.println("prefixBytes 0x:"+Utils.bytes2HexString(prefixBytes)+" , string chunk index :"+Utils.bytes2Int(prefixBytes)+" , prefix string :"+stringChunkContent.get(Utils.bytes2Int(prefixBytes)));
    System.out.println("uriBytes 0x:"+Utils.bytes2HexString(uriBytes)+" , string chunk index :"+Utils.bytes2Int(uriBytes)+" , uri string :"+stringChunkContent.get(Utils.bytes2Int(uriBytes)));
}
```

上面是AndroidManifest.xml的命名空间的解析，可能会有多个命名空间。

## 五、Start Tag Thunk
![image_1d79e3if1tm81ips8n3hrjtbh1g.png-49.4kB][13]
![image_1d79eq98ps4f1c281l2k1ao554f3c.png-31.5kB][14]

***注：564h的计算：8+0x00000504+0x00000040+0x00000018 = 1380 [564h]***
    
    1. ChunkType：Start Tag Thunk类型，固定四个字节：0x00100102
    2. ChunkSize：Start Tag Thunk Size，固定四个字节
    3. Line Number:AndroidManifest.xml行号，固定四字节
    4. 未知，固定四字节
    5. Namespace Uri:命名空间的Uri，如：android="http://schemas.android.com/apk/res/android" 固定4字节
    6. Name:标签名字，注意是在StringChunkContent中的索引
    7. Flags(0x00140014):标签类型，固定4字节
    8. Attribute Count:标签的属性个数，固定4字节
    9. Class Attribute:标签类属性，固定4字节
    10. Attributes: 属性内容，每个属性由5*4bytes构成，实际上是一个一维大小为5的数组组成，数组中每个值的含义为[Namespace Uri, Name, Value String, Type, Data]，这里需要注意的是Type值的获取，需要右移24位。

代码解析：
```Java
public static void parseStartTagChunk1(byte[] bytes) {
    if (!Utils.checkBytes(bytes)) {
        return;
    }

    int startTagChunkOffsets = STRING_CHUNK_BASE_POSITION + 0x00000504 + 0x00000040 + 0x00000018;
    byte[] startTagThunkBytes = Utils.copyBytes(bytes, startTagChunkOffsets, 4);
    byte[] startTagThunkSizeBytes = Utils.copyBytes(bytes, startTagChunkOffsets + 4, 4);

    System.out.println("startTagThunkBytes 0x:" + Utils.bytes2HexString(startTagThunkBytes));
    System.out.println("startTagThunkSizeBytes 0x:" + Utils.bytes2HexString(startTagThunkSizeBytes) + " , int :" + Utils.bytes2Int(startTagThunkSizeBytes));

    byte[] startTagThunkContentBytes = Utils.copyBytes(bytes, startTagChunkOffsets + STRING_CHUNK_BASE_POSITION, Utils.bytes2Int(startTagThunkSizeBytes));
    byte[] lineNubmerBytes = Utils.copyBytes(startTagThunkContentBytes, 0, 4);
    System.out.println("lineNubmer 0x:" + Utils.bytes2HexString(lineNubmerBytes) + " , int : " + Utils.bytes2Int(lineNubmerBytes));

    byte[] namespaceBytes = Utils.copyBytes(startTagThunkContentBytes, 8, 4);
    System.out.println("namespace 0x:" + Utils.bytes2HexString(namespaceBytes) + " , int : " + Utils.bytes2Int(namespaceBytes) + " , url : " + getStringChunkContent(Utils.bytes2Int(namespaceBytes)));

    byte[] name = Utils.copyBytes(startTagThunkContentBytes, 12, 4);
    System.out.println("name 0x:" + Utils.bytes2HexString(name) + " , int : " + Utils.bytes2Int(name) + " , string : " + getStringChunkContent(Utils.bytes2Int(name)));

    byte[] flags = Utils.copyBytes(startTagThunkContentBytes, 16, 4);
    System.out.println("flags 0x:" + Utils.bytes2HexString(flags) + " , int : " + Utils.bytes2Int(flags));

    byte[] attr = Utils.copyBytes(startTagThunkContentBytes, 20, 4);
    int attrCount = Utils.bytes2Int(attr);
    System.out.println("attr 0x:" + Utils.bytes2HexString(attr) + " , attr count : " + attrCount);

    byte[] claAttr = Utils.copyBytes(startTagThunkContentBytes, 24, 4);
    System.out.println("class attr 0x:" + Utils.bytes2HexString(claAttr) + " , class attr count : " + Utils.bytes2Int(claAttr));

    byte[] attributesContentBytes = Utils.copyBytes(startTagThunkContentBytes, 28, attrCount * 5 * 4);
    System.out.println("attributesContentBytes len : " + attributesContentBytes.length);
    ArrayList<AttributeData> attrs = new ArrayList<>();
    for (int i = 0; i < attrCount; i++) {
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
        attrs.add(attrData);
    }
    System.out.println("==============");
    for (int i = 0; i < attrCount; i++) {
        AttributeData aData = attrs.get(i);
        System.out.println("namespaceuri = "+getStringChunkContent(aData.nameSpaceUri));
        System.out.println("name = "+getStringChunkContent(aData.name));
        System.out.println("valuestring = "+getStringChunkContent(aData.valuestring));
        System.out.println("type = "+(aData.type == -1 ?"null":AttributeType.getAttrType(aData.type)));
        System.out.println("data = "+(aData.data == -1 ?"null":AttributeType.getAttributeData(aData)));
        System.out.println("==============");
    }
}
```

## 六、其他解析大致相同，全部Thunk的自动解析代码
```Java
package com.erlin.xml;

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
        byte[] manifest = Utils.readAndroidManifestToByteArray("./AndroidManifest.xml");

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
```


  [1]: http://static.zybuluo.com/gaierlin/jef44de38tpztzbcmj20w51y/Screenshot%20from%202019-03-21%2012-13-01.png
  [2]: http://static.zybuluo.com/gaierlin/hi9862py1wffi7fpie0y6csk/android-manifest.xml-%E6%96%87%E4%BB%B6%E6%A0%BC%E5%BC%8F.png
  [3]: http://static.zybuluo.com/gaierlin/cvigg69e17mini123lvs65x6/image_1d6qd0d8s110ummi1n821oi516n09.png
  [4]: http://static.zybuluo.com/gaierlin/l0zes07bxu1ew3g2azcawten/image_1d6qdrp2e1koolv4lga4n819qsm.png
  [5]: http://static.zybuluo.com/gaierlin/dval9oix9dyrva26a1ccejtp/image_1d6qfkpdpmhn22fh639k01p2o13.png
  [6]: http://static.zybuluo.com/gaierlin/p2d9bd3c8xjyagb3sja3yfx0/image_1d6qg5qsk1fjv1nk8mo11g93l61g.png
  [7]: http://static.zybuluo.com/gaierlin/wziriezrkuhk7fbevhd1iy41/image_1d6qma4us2g61v6q12mq1kd91e9u2q.png
  [8]: http://static.zybuluo.com/gaierlin/wrrk0lvr8ebkq3gik5lyt8jf/image_1d6qneiem1motv5pqe51eb47gh37.png
  [9]: http://static.zybuluo.com/gaierlin/1vz5s9l61yglnw3sh9aszrer/image_1d77jq5lp6hl1jkgrs21t8t19ch16.png
  [10]: http://static.zybuluo.com/gaierlin/0jqlqw6ljdx9qdq18ylii2xa/image_1d792thlrql1namlo155t1nj228.png
  [11]: http://static.zybuluo.com/gaierlin/9bnukoh4p4rgwe3nd16mii8c/image_1d799vuen15fnrvvmkjf81qjm9.png
  [12]: http://static.zybuluo.com/gaierlin/98zpf0c5ukx00v8a7xyjty7o/image_1d79agct42sjje87ou1rln1qsum.png
  [13]: http://static.zybuluo.com/gaierlin/561rx1hcapl9glesb4dfvqnf/image_1d79e3if1tm81ips8n3hrjtbh1g.png
  [14]: http://static.zybuluo.com/gaierlin/5wkk7sweb40k655s38nie0ob/image_1d79eq98ps4f1c281l2k1ao554f3c.png