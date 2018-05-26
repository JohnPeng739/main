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

*注意*：
1. 为了使用后续介绍的实体访问工具，项目自定义的实体（Entity类）必须要根据实体需求实现以上接口中的一个。
2. 本模块提供了`EntityFactory`的实体工厂类来实现面向接口定义和类定义的实体创建方法：`public static <T extends Base> T createEntity(Class<T> clazz) throws UserInterfaceDalErrorException`，应用程序可以在任何时候以实体接口定义或实体类定义来创建对应的实体对象。

*特别提示*：如果您识别到您的实体可能将出现不同类型的实现，比如：基于JPA、ES、MongoDB等实现，强烈建议您针对您的实体类抽象出对应的实体接口，并确保接口和对应的实体类在不同的Java项目中的相同包路径，并确保实体类的命名为"接口名+Entity"的方式，例如：接口名为"Org"的话，那么实体类名应该为"OrgEntity"。

例如：
我们假设有一个组织机构树，该数据实体我们可能根据需要要保存到Oracle、MongoDB和ES数据库中；而且该组织机构需要包括以下字段：
- 基本字段：id、createdTime、updatedTime、operator、valid；
- 基本字典字段：code、name、desc；
- 基本树状字典字段：parent、children；
- 扩展字段：manager（部门领导，关联到Person实体）、employees（直属员工，关联到Person实体）、responsibility（职责）

- 组织机构实体接口定义如下所示：
```java
package com.dscomm.test.dal.entity;

public interface Org extends BaseDictTree {
    Person getManager();
    void setManager(Person manager);
    Set<Person> getEmployees();
    void setEmployees(Set<Person> employees);
    String getResponsibility();
    void setResponsibility(String responsibility);
}
```
*注意*：由于基本字段、基本字典字段、基本树状字典字典在BaseDictTree中已经定义，因此这里仅需要定义扩展字段即可！

- 组织机构实体的JPA实现类定义如下所示：
```java
package com.dscomm.test.dal.entity;

@Entity
@Table(name = "T_ORG")
public class OrgEntity extends BaseDictTreeEntity implements Org {
    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "MANAGER_ID")
    private Person manager;
    
    @OneToMany(cascade = {CascadeType.REFRESH})
    @JoinTable(name = "T_ORG_EMPLOYEE", 
        joinColumns = { @JoinColumn(name = "ORG_ID") },
        inverseJoinColumns = { @JoinColumn(name = "PERSON_ID") })
    private Set<Person> employees;
    
    @Column(name = "RESPONSIBILITY")
    private String responsibility;
    
    // ... 这里添加对应属性的getter和setter方法
}
```
- 组织机构实体的MongoDB实现类定义如下所示：
```java
package com.dscomm.test.dal.entity;

@Document
public class OrgEntity extends BaseDictTreeEntity implements Org {
    @DBRef
    private Person manager;
    
    @DBRef
    private Set<Person> employees;
    
    // ... 这里添加对应属性的getter和setter方法
}
```
- 组织机构实体的ES实现类定如下所示：
```java
package com.dscomm.test.dal.entity;

@ElasticIndex("idx_org")
public class OrgEntity extends BaseDictTreeEntity implements Org {
    private Person manager;
    private Set<Person> employees;
    @ElasticField(analyzer = "hanlp")
    private String responsibility;
    
    // ... 这里添加对应属性的getter和setter方法
}
```

## 面向实体的访问接口
本模块定义了面向任意实体进行操作的访问接口，定义在`org.mx.dal.service`中，如下所示：
- GeneralAccessor接口：针对任意一个实体对象（BaseEntity类）进行存储访问操作的接口，提供获取数据、查询数据、保存数据、删除数据（逻辑删除和物理删除）等API方法；
```java
/**
 * 通用的实体存取接口定义
 *
 * @author : john.peng date : 2017/8/18
 */
public interface GeneralAccessor {
    /**
     * 对指定的实体类型进行计数，实体接口必须继承Base接口。
     *
     * @param clazz 实体接口类型
     * @param <T>   实现Base接口的泛型对象类型
     * @return 指定实体的数量
     * @throws UserInterfaceDalErrorException 计数过程中发生的异常
     */
    <T extends Base> long count(Class<T> clazz) throws UserInterfaceDalErrorException;

    /**
     * 对指定的实体类型进行计数，实体接口必须继承Base接口。
     *
     * @param clazz   实体接口类型
     * @param isValid 如果设置为true，仅返回有效的记录；否则对所有记录计数。
     * @param <T>     实现Base接口的泛型对象类型
     * @return 指定实体的数量
     * @throws UserInterfaceDalErrorException 计数过程中发生的异常
     */
    <T extends Base> long count(Class<T> clazz, boolean isValid) throws UserInterfaceDalErrorException;

    /**
     * 获取指定实体类型的数据集合，实体接口必须继承Base接口。
     *
     * @param clazz 实体接口类型
     * @param <T>   实现Base接口的泛型对象类型
     * @return 指定实体对象类别
     * @throws UserInterfaceDalErrorException 获取过程中发生的异常
     */
    <T extends Base> List<T> list(Class<T> clazz) throws UserInterfaceDalErrorException;

    /**
     * 获取指定实体类型的数据集合，实体接口必须继承Base接口。
     *
     * @param clazz   实体接口类型
     * @param isValid 如果设置为true，仅返回有效的记录；否则返回所有记录。
     * @param <T>     实现Base接口的泛型对象类型
     * @return 指定实体对象类别
     * @throws UserInterfaceDalErrorException 获取过程中发生的异常
     */
    <T extends Base> List<T> list(Class<T> clazz, boolean isValid) throws UserInterfaceDalErrorException;

    /**
     * 根据分页信息获取指定实体类型的数据子集合，实体接口必须继承Base接口。
     *
     * @param pagination 分页信息
     * @param clazz      实体接口类型
     * @param <T>        实现Base接口的泛型对象类型
     * @return 指定实体对象集合
     * @throws UserInterfaceDalErrorException 获取过程中发生的异常
     */
    <T extends Base> List<T> list(Pagination pagination, Class<T> clazz) throws UserInterfaceDalErrorException;

    /**
     * 根据分页信息获取指定实体类型的数据子集合，实体接口必须继承Base接口。
     *
     * @param pagination 分页信息
     * @param clazz      实体接口类型
     * @param isValid    如果设置为true，仅返回有效的记录；否则返回所有记录。
     * @param <T>        实现Base接口的泛型对象类型
     * @return 指定实体对象集合
     * @throws UserInterfaceDalErrorException 获取过程中发生的异常
     */
    <T extends Base> List<T> list(Pagination pagination, Class<T> clazz, boolean isValid) throws UserInterfaceDalErrorException;

    /**
     * 根据实体ID和实体接口类型获取实体，实体接口必须继承Base接口。
     *
     * @param id    实体ID
     * @param clazz 实体接口类
     * @param <T>   实现Base接口的泛型对象类型
     * @return 实体，如果实体不存在，则返回null。
     * @throws UserInterfaceDalErrorException 获取实体过程中发生的异常
     */
    <T extends Base> T getById(String id, Class<T> clazz) throws UserInterfaceDalErrorException;

    /**
     * 根据指定字段的值获取数据记录集合，多个条件采用and组合。
     *
     * @param tuples 条件元组（包括字段名和字段值）
     * @param clazz  实体接口类
     * @param <T>    实现Base接口的泛型对象类型
     * @return 实体对象集合
     * @throws UserInterfaceDalErrorException 获取过程中发生的异常
     * @see ConditionTuple
     */
    public <T extends Base> List<T> find(List<ConditionTuple> tuples, Class<T> clazz)
            throws UserInterfaceDalErrorException;

    /**
     * 根据指定字段的值获取一条数据记录，多个条件采用and组合。
     *
     * @param tuples 条件元组（包括字段名和字段值）
     * @param clazz  实体接口类
     * @param <T>    实现Base接口的泛型对象类型
     * @return 实体对象，如果不存在则返回null
     * @throws UserInterfaceDalErrorException 获取过程中发生的异常
     * @see ConditionTuple
     * @see #find(List, Class)
     */
    <T extends Base> T findOne(List<ConditionTuple> tuples, Class<T> clazz)
            throws UserInterfaceDalErrorException;

    /**
     * 保存指定的实体。如果可能，自动保存一条操作日志。
     *
     * @param t   实体对象
     * @param <T> 实现Base接口的泛型对象类型
     * @return 返回保存后的实体对象
     * @throws UserInterfaceDalErrorException 保存实体过程中发生的异常
     */
    <T extends Base> T save(T t) throws UserInterfaceDalErrorException;

    /**
     * 逻辑删除指定关键字ID的数据实体
     *
     * @param id    关键字ID
     * @param clazz 实体接口类
     * @param <T>   实现Base接口的泛型对象类型
     * @return 删除的实体
     * @throws UserInterfaceDalErrorException 删除过程中发生的异常
     */
    <T extends Base> T remove(String id, Class<T> clazz) throws UserInterfaceDalErrorException;

    /**
     * 逻辑删除指定关键字ID的数据实体
     *
     * @param id          关键字ID
     * @param clazz       实体接口类
     * @param logicRemove 设置为true表示逻辑删除，否则物理删除。
     * @param <T>         实现Base接口的泛型对象类型
     * @return 删除的实体
     * @throws UserInterfaceDalErrorException 删除过程中发生的异常
     */
    <T extends Base> T remove(String id, Class<T> clazz, boolean logicRemove)
            throws UserInterfaceDalErrorException;

    /**
     * 逻辑删除指定的实体
     *
     * @param t   实体对象
     * @param <T> 实现Base接口的泛型对象类型
     * @return 删除的实体
     * @throws UserInterfaceDalErrorException 删除过程中发生的异常
     * @see #remove(Base, boolean)
     */
    <T extends Base> T remove(T t) throws UserInterfaceDalErrorException;

    /**
     * 删除指定的实体，支持逻辑删除和物理删除。
     *
     * @param t           实体对象
     * @param <T>         实现Base接口的泛型对象类型
     * @param logicRemove 设置为true表示逻辑删除，否则物理删除。
     * @return 删除的实体
     * @throws UserInterfaceDalErrorException 删除过程中发生的异常
     */
    <T extends Base> T remove(T t, boolean logicRemove) throws UserInterfaceDalErrorException;
```
- GeneralDictAccessor接口：针对任意一个一个字典类型的实体对象（BaseDictEntity类）进行存储访问操作的接口，继承了`GeneralAccessor`接口，增加了基于Code查询数据的API方法；
- OperateLogService接口：针对人机交互审计日志进行存储访问操作的接口，提供了写入审计日志的API方法。

例如：
依据上面的组织机构实体的数据访问操作如下所示：
```java
package com.dscomm.test.dal.service;

public class TestOrg {
    private AnnotationConfigApplicationContext context;
    
    @Before
    public void before() {
        context = new AnnotationConfigApplicationContext(TestConfig.class);
    }
    
    @After
    public void after() {
        if (context != null) {
            context.close();
        }
    }
    
    @Test
    public void test() {
        // 检查GeneralAccessor是否已经被装配
        // 注意：由于Org是一个字典类实体，因此我们这里使用GeneralDictAccessor接口
        GeneralDictAccessor accessor = context.getBean("generalDictAccessor", GernalAccessor.class);
        assertNotNull(accessor);
        
        // 构造一个简单Org实体对象并保存
        Org org = EntityFactory.createEntity(Org.class);
        org.setCode("ds");
        // ... 根据需要对机构属性进行赋值
        Org check = accessor.save(org);
        assertNotNull(check);
        assertTrue(!StringUtils.isBlank(check.getId()));
        assertEquals(org.getCode(), check.getCode());
        
        // 修改一个知道Org Id的组织机构并保存
        Org editOrg1 = accessor.getById(orgId, Org.class);
        // ...根据需要对机构属性进行赋值
        assertFalse(StringUtils.isBlank(editOrg1.getId()));
        Org editCheck1 = accessor.save(editOrg1);
        assertNotNull(editCheck1);
        
        // 修改一个知道Org Code的组织机构并保存
        Org editOrg2 = accessor.getByCode(orgCode, Org.class);
        // ...根据需要对机构属性进行赋值
        assertFalse(StringUtils.isBlank(editOrg2.getId()));
        Org editCheck2 = accessor.save(editOrg2);
        assertNotNull(editCheck2);
        
        // 逻辑删除一个组织机构
        accessor.remove(orgId, Org.class);
        // 或者
        Org delOrg = accessor.getByCode(orgCode, Org.class);
        assertNotNull(delOrg);
        accessor.remove(delOrg);
        
        // 物理删除一个组织机构
        accessor.remove(orgId, Org.class, false);
        // 或者
        Org delOrg = accessor.getByCode(orgCode, Org.class);
        assertNotNull(delOrg);
        accessor.remove(delOrg, false);
    }
}
```

*注意*：`OperateLogService`需要根据需要进行选用！在某些环境中如果已经有*统一日志*的话，应当直接使用统一日志的要求来实现审计类日志的需求。

## 基于线程局部堆的数据传递
为了避免在一个人机交互过程中向服务层代码层层传递当前系统（System）、当前模块（Module）、当前操作用户（Operator）信息，本模块提供了基于线程局部堆（Thread Local）的数据存储服务。
该服务能够跟当前线程绑定，并传递System、Module、User等信息，供底层服务无缝调用。
该服务接口定义在`org.mx.dal.session.SessionDataStore`中，并已经被Java Config配置类`org.mx.dal.config.DalConfig`中加载。
要无缝使用该服务，需要在业务的JavaConfig（如：TestConfig）中使用`@Import`引入`DalConfig`即可。

*详细说明参阅本模块的Javadoc。*