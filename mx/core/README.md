# core项目
[![License](http://img.shields.io/:license-mit-blue.svg?style=flat)](http://doge.mit-license.org)

*面向Java应用的常用模块*：为了提高Java项目的开发效率和质量，所有项目都可以应用本项目中的相关模块。

本项目包括以下模块：
1. [mx-utils](mx-utils)：*面向Java应用的常用工具类项目*
2. [mx-spring](mx-spring)：*面向Spring应用的常用工具类项目*
3. [mx-service-utils](mx-service-utils)：*基于Spring和Jetty提供的关于RESTful、Servlet、Websocket等微服务的框架项目*
4. [mx-hanlp-utils](mx-hanlp-utils)：*基于Spring和HanLP提供基础的语义分析工具项目*
5. [mx-dal](mx-dal)：*高度抽象的DAL（Data Access Layer），目前对关系型数据库、MongoDB数据库、Elastic Search提供了统一接口抽象*
6. [mx-dal-hibernate](mx-dal-hibernate)：*基于Hibernate JPA实现的mx-dal实现，可以对所有关系型数据进行支持*
7. [mx-dal-mongodb](mx-dal-mongodb)：*基于Mongodb实现的mx-dal实现*
8. [mx-dal-elastic](mx-dal-elastic)：*基于Elastic Search High Level RESTful实现的mx-dal实现*

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
            <!-- 根据模块的需要，引入相应的模块组件 -->
        </dependency>
    </dependencies>

</project>
```
*注意*：所有jar包托管在jcenter中，请在Maven2的`settings.xml`或者项目的`pom.xml`中添加`https://jcenter.bintray.com/`，如果网络不支持https协议，也可以使用`http://jcenter.bintray.com/`。
- 第二步<br>
创建测试类：com.dscomm.test.TestApplication，其中的Java代码可能如：
```java
package com.dscomm.test;

public class TestApplication {
    public static void main(String[] args) {
        String str = "abcd,ergh;1234 5678";
        String[] segments = StringUtils.split(str);
        for (String segment : segments) {
            System.out.println(segment);
        }
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
