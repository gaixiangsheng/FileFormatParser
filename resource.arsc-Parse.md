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

