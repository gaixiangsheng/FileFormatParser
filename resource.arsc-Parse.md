# resource.arsc资源文件解析

## 获取resources.arsc
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

![image](img/Resource.arsc-format.png)

注：resources.arsc 文件格式的数据结构定义：android-4.1.1_r1/frameworks/base/include/androidfw/ResourceTypes.h

根据ResourceTypes.h文件结构，转换成的Java文件结构如下：
```Java
./src/com/erlin/parse/androidArsc/type
├── ./src/com/erlin/parse/androidArsc/type/ResChunkHeader.java
├── ./src/com/erlin/parse/androidArsc/type/ResourceType.java
├── ./src/com/erlin/parse/androidArsc/type/ResStringPoolHeader.java
├── ./src/com/erlin/parse/androidArsc/type/ResStringPoolRef.java
├── ./src/com/erlin/parse/androidArsc/type/ResStringPoolSpan.java
├── ./src/com/erlin/parse/androidArsc/type/ResTableConfig.java
├── ./src/com/erlin/parse/androidArsc/type/ResTableEntry.java
├── ./src/com/erlin/parse/androidArsc/type/ResTableHeader.java
├── ./src/com/erlin/parse/androidArsc/type/ResTableMapEntry.java
├── ./src/com/erlin/parse/androidArsc/type/ResTableMap.java
├── ./src/com/erlin/parse/androidArsc/type/ResTablePackage.java
├── ./src/com/erlin/parse/androidArsc/type/ResTableRef.java
├── ./src/com/erlin/parse/androidArsc/type/ResTableType.java
├── ./src/com/erlin/parse/androidArsc/type/ResTableTypeSpec.java
└── ./src/com/erlin/parse/androidArsc/type/ResValue.java
```

有了上面的逆向结构体，就可以进行下面逆向工作工具了。

## 一、ResChunk_header -> ResChunkHeader基本结构体

resources.arsc文件是有一系列的Chunk构成，每一个chunk均包含如下结构的ResChunk_header,用来描述这个chunk的基本信息，因此
ResChunk_header是每个chunk的基本结构体。

![image](img/res_table_type.png)
![image](img/res_table_package_type.png)

ResChunkHeader的代码如下：
```Java
package com.erlin.parse.androidArsc.type;

/**
 * ResourceTypes.h -> struct ResChunk_header
 */
public class ResChunkHeader {
    public short type;//2个字节
    public short headerSize;//2个字节
    public int size;//4个字节

    public int getOffsets(){
        return 2+2+4;
    }

    @Override
    public String toString() {
        return "ResChunkHeader{" +
                "type=" + type +
                ", headerSize=" + headerSize +
                ", size=" + size +
                '}';
    }
}

```
1. type: ChunkType类型，固定占用2个字节
2. headerSize：ChunkHeader的ChunkSize大小，固定占用2个字节
3. size：该段Chunk的大小

## 二、ResTable_header -> ResTableHeader解析分析

resources.arsc的Resource Table Header Chunk如下结构体：
```Java
package com.erlin.parse.androidArsc.type;

/**
 * ResourceTypes.h -> struct ResTable_header
 */

public class ResTableHeader {
    public ResChunkHeader resChunkHeader;
    public int packageCount;

    @Override
    public String toString() {
        return "ResTableHeader{" +
                "resChunkHeader=" + resChunkHeader +
                ", packageCount=" + packageCount +
                '}';
    }
}
```

![image](img/resource.arsc-reschunkheader.png)

1. resChunkHeader:Chunk的头部信息结构
    * type: Resource Table Header Chunk 类型：0x0002
    * headerSize:Resource Table Header Chunk Size 的大小：0x000C
    * size:Resource Table Chunk Size的大小，在此资源段内，代表整个resources.arsc文件大小
2. packageCount: 编译的资源包的个数。一般情况下只有一个资源包，就是应用包名所在的资源包。

```Java
public static void parseResourcTableTypeChunk(byte[] bytes){
    if(!Utils.checkBytes(bytes)){
        return;
    }
    ResChunkHeader mResChunkHeader = new ResChunkHeader();
    mResChunkHeader.type = ResourceType.RES_TABLE_TYPE;
    mResChunkHeader.headerSize = Utils.bytes2Short(Utils.copyBytes(bytes,offsets+2,2));
    mResChunkHeader.size = Utils.bytes2Int(Utils.copyBytes(bytes,offsets+4,4));

    mResTableHeader = new ResTableHeader();
    mResTableHeader.resChunkHeader = mResChunkHeader;
    mResTableHeader.packageCount = Utils.bytes2Int(Utils.copyBytes(bytes,offsets+8,4));
    System.out.println("ResTableHeader :"+mResTableHeader);
}
```
## 二、资源字符串池ResStringPool_header -> ResStringPoolHeader Chunk解析：
