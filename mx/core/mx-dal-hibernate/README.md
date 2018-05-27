# mx-dal-hibernate模块
[![License](http://img.shields.io/:license-mit-blue.svg?style=flat)](http://doge.mit-license.org)

## 安装和引用
*Maven*
```xml
<dependency>
    <groupId>org.mx</groupId>
    <artifactId>mx-dal-hibernate</artifactId>
    <version>1.3.8</version>
</dependency>
```
*Gradle*
```gradle
compile 'org.mx:mx-dal-hibernate:1.3.8'
```

## Hello world

## 基于DBCP的数据库连接池
本模块内置了基于Apache DBCP2实现的关系型数据库的连接池，其配置信息如下所示：
```properties
db.driver=org.h2.Driver
db.url=jdbc:h2:./h2/test
db.user=sa
db.password=
db.initialSize=10
db.maxSize=100
db.idleTime=3000
```
在Spring IoC容器中已经注入了"dataSource"的对象实例，可以直接取用来进行基于JDBC的数据库调用。

## 抽象的底层实体ORM
本模块已经在包`org.mx.dal.entity`下定义了基于Hibernate JPA实现的常见实体基类，可以根据需要取用：
- org.mx.dal.entity.BaseEntity
- org.mx.dal.entity.BaseDictEntity
- org.mx.dal.entity.BaseDictTreeEntity
- org.mx.dal.entity.OperateLogEntity

*注意*：这些实体映射的数据库字段名称默认采用匈牙利命名，没有采用"音头"命名法；如果需要使用"音头"命名法，需要根据需要重新实现这些基类。

## 基于JPA的DAL实现<
基于Hibernate的JPA实现了DAL的相关Accessor接口。在Spring IoC容器中包括了：
- generalAccessorJpa：对应于`org.mx.dal.service.GeneralAccessor`接口的实现；
- generalDictAccessorJpa：对应于`org.mx.dal.service.GeneralDictAccessor`接口的实现。

要使用基于JPA的DAL实现，需要以下配置文件：
- 数据库连接及连接池配置：详见"基于DBCP的数据库连接池"；
- JPA配置：
```properties
jpa.database=H2
jpa.databasePlatform=org.hibernate.dialect.H2Dialect
jpa.generateDDL=true
jpa.showSQL=true
```

## 事务控制

*详细说明参阅本模块的Javadoc。*