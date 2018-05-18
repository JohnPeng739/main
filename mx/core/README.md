# core项目
[![License](http://img.shields.io/:license-mit-blue.svg?style=flat)](http://doge.mit-license.org)

*面向Java应用的常用模块*：为了提高Java项目的开发效率和质量，所有项目都可以应用本项目中的相关模块。

本项目包括以下模块：
1. mx-utils
2. mx-spring
3. mx-service-utils
4. mx-hanlp-utils
5. mx-dal
6. mx-dal-hibernate
7. mx-dal-mongodb
8. mx-dal-elastic

## Hello world
- 第一步<br>
创建一个标准的Maven Java项目，其中的POM可能如：
```maven
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.dscomm.test</groupId>
    <artifactId>TestProject</artifactId>
    <version>1.0-SNAPSHOT</version>

    <dependencies>
        <dependency>
            <groupId>org.mx</groupId>
            <artifactId>mx-utils</artifactId>
            <version>2.1.17</version>
        </dependency>
    </dependencies>

</project>
```
- 第二步<br>
创建测试类：com.dscomm.test.TestApplication，其中的Java代码可能如：
```java
public static void main(String[] args) {
    String str = "abcd,ergh;1234 5678";
    String[] segments = StringUtils.split(str);
    for (String segment : segments) {
        System.out.println(segment);
    }
}
```
以上代码将输出：
```text
abcd
ergh
1234
5678
```

## mx-utils
*[mx-utils](mx-utils)：面向Java应用的常用工具类项目*

## mx-spring
*[mx-spring](mx-spring)：面向Spring应用的常用工具类项目*

## mx-service-utils


## mx-hanlp-utils


## mx-dal

## mx-dal-hibernate

## mx-dal-mongodb

## mx-dal-elastic

