# mx-utils模块
[![License](http://img.shields.io/:license-mit-blue.svg?style=flat)](http://doge.mit-license.org)

## 安装和引用
*Maven*
```xml
<dependency>
    <groupId>org.mx</groupId>
    <artifactId>mx-utils</artifactId>
    <version>2.1.17</version>
</dependency>
```
*Gradle*
```gradle
compile 'org.mx:mx-utils:2.1.17'
```
## 工具类
目前，mx-utils模块中包括了面向：Class、Collections、Config、Date、Digest、File、Password、Random、String、System、
Type等方面的工具类，以供选择使用，项目介绍如下：

| 工具类 | 描述 |
| :--- | :--- |
| org.mx.StringUtils | 面向字符串处理的工具类，例如：字符串判空（isBlank）、字符串分解（split）、字符串合并（merge）等 |
| org.mx.ClassUtils  | 面向类扫描的工具类，例如：指定的包下的类扫描，支持jar包和目录扫描 |
| org.mx.CollectionsUtils | 面向集合处理的工具类，包括：集合类中元素的处理。 |
| org.mx.ConfigUtils | 面向配置信息的工具类，支持从Properties或XML文件中加载配置信息，支持各种常见的数据类型。 |
| org.mx.DateUtils | 面向日期处理的工具类，支持日期的计算、日期的转换等操作。 |
| org.mx.DigestUtils | 面向加解密、摘要等算法的工具类 |
| org.mx.FileUtils | 面向文件处理的工具类，采用Paths、Files等对文件的快速处理。 |
| org.mx.PasswordUtils | 面向密码处理的工具类，提供了密码强度检测的方法。 |
| org.mx.RandomUtils | 面向随机数处理的工具类，提供了各种常见随机数的工具。 |
| org.mx.SystemUtils | 面向系统监控参数的工具类，提供了各个操作系统平台的CPU、内存、线程等资源耗用的检测工具。 |
| org.mx.TypeUtils | 面向类型处理的工具类，提供了各种常见的数据类型转换工具。 |
| org.mx.error.* | 提供了一个面向UI的错误异常定义体系，便于后端系统跟前端APP的无缝错误信息结合。 |
| org.mx.rate.* | 提供了面向数值类型的TopN计算框架，便于各种周期的速率计算。 |

## 使用技巧和样例
*对于所有的Utils工具类，所有方法均提供静态访问方式，在引入本模块后可以直接调用。*例如：使用StringUtils工具类中的split方法对指定的字符串进行分割：
```java
String str = "abc,123 456";
if (!StringUtils.isBlank(str)) {
    String[] segments = StringUtils.split(str, ",; ", true, true);
}
```
以上样例代码将把输入的字符串分解成三个元素：abc，123，456。
