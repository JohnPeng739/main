# mx-dal模块
[![License](http://img.shields.io/:license-mit-blue.svg?style=flat)](http://doge.mit-license.org)

## 安装和引用
*Maven*
```xml
<dependency>
    <groupId>org.mx</groupId>
    <artifactId>mx-dal</artifactId>
    <version>1.3.6</version>
</dependency>
```
*Gradle*
```gradle
compile 'org.mx:mx-dal:1.3.6'
```
## 实体类接口
本模块定义了面向后续实体操作的抽象接口定义在包`org.mx.dal.entity`中，如下所示：
- Base接口：任何一个数据对象实体接口，包括：ID、CreatedTime、UpdatedTime、Opertator、Valid属性；
- BaseDict接口：任何一个字典类型的数据对象实体接口，继承了Base接口，还包括：Code、Name、Desc属性；
- BaseDictTree接口：任何一个树状结构的字典类型数据对象实体接口，继承了BaseDict接口，还包括：ParentId、Parent、Children属性；
- OperateLog接口：基本的人机交互审计日志数据对象实体接口，继承了Base接口，还包括：System、Module、OperateType、Content属性。

*注意*：为了使用后续介绍的实体访问工具，项目自定义的实体（Entity类）必须要根据实体需求实现以上接口中的一个。

## 面向实体的访问接口
本模块定义了面向任意实体进行操作的访问接口，定义在`org.mx.dal.service`中，如下所示：
- GeneralAccessor接口：针对任意一个实体对象（BaseEntity类）进行存储访问操作的接口，提供获取数据、查询数据、保存数据、删除数据（逻辑删除和物理删除）等API方法；
- GeneralDictAccessor接口：针对任意一个一个字典类型的实体对象（BaseDictEntity类）进行存储访问操作的接口，继承了`GeneralAccessor`接口，增加了基于Code查询数据的API方法；
- OperateLogService接口：针对人机交互审计日志进行存储访问操作的接口，提供了写入审计日志的API方法。

## 基于线程局部堆的数据传递
为了避免在一个人机交互过程中向服务层代码层层传递当前系统（System）、当前模块（Module）、当前操作用户（Operator）信息，本模块提供了基于线程局部堆（Thread Local）的数据存储服务。
该服务能够跟当前线程绑定，并传递System、Module、User等信息，供底层服务无缝调用。
该服务接口定义在`org.mx.dal.session.SessionDataStore`中，并已经被Java Config配置类`org.mx.dal.config.DalConfig`中加载。
要无缝使用该服务，需要在业务的JavaConfig（如：TestConfig）中使用`@Import`引入`DalConfig`即可。

*详细说明参阅本模块的Javadoc。*