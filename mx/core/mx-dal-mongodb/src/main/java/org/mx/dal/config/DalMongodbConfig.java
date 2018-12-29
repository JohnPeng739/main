package org.mx.dal.config;

import com.mongodb.MongoClient;
import org.mx.dal.service.GeneralAccessor;
import org.mx.dal.service.GeneralDictAccessor;
import org.mx.dal.service.impl.GeneralAccessorMongoImpl;
import org.mx.dal.service.impl.GeneralDictAccessorMongoImpl;
import org.mx.dal.utils.MongoDbConfigBean;
import org.mx.dal.utils.MongoDbUtils;
import org.mx.spring.config.SpringConfig;
import org.mx.spring.session.SessionDataStore;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

/**
 * 基于Mongodb的DAL实现的Java Configure定义
 * 扫描：org.mx.dal.service.impl中的组件
 * 加载配置：classpath:mongodb.properties
 *
 * @author : john.peng date : 2017/10/8
 */
@Import({DalConfig.class, SpringConfig.class})
@ComponentScan({"org.mx.dal.service.impl"})
public class DalMongodbConfig {

    private MongoTransactionManager transactionManager = null;

    /**
     * 默认的构造函数
     */
    public DalMongodbConfig() {
        super();
    }

    /**
     * 根据配置文件内容和默认内容创建MongoDB配置信息对象
     *
     * @param env Spring IoC上下文环境
     * @return MongoDB配置信息对象
     */
    @Bean
    public MongoDbConfigBean mongoDbConfigBean(Environment env) {
        return new MongoDbConfigBean(env);
    }

    /**
     * 根据配置创建MongoDB Client
     *
     * @param mongoDbConfigBean MongoDB配置信息对象
     * @return MongoDB Client
     */
    @Bean
    public MongoClient mongoClient(MongoDbConfigBean mongoDbConfigBean) {
        return MongoDbUtils.createMongoClient(mongoDbConfigBean);
    }

    /**
     * 根据配置创建MongoDB数据库工厂
     *
     * @param mongoClient       MongoDb Client
     * @param mongoDbConfigBean MongoDB配置信息对象
     * @return MongoDB数据库工厂
     */
    @Bean
    public MongoDbFactory mongoDbFactory(MongoClient mongoClient, MongoDbConfigBean mongoDbConfigBean) {
        return new SimpleMongoDbFactory(mongoClient, mongoDbConfigBean.getDatabase());
    }

    /**
     * 创建MongoDB模版工具
     *
     * @param mongoDbFactory MongoDB数据库工厂
     * @return 模版工具
     */
    @Bean(name = "mongoTemplate")
    public MongoTemplate mongoTemplate(MongoDbFactory mongoDbFactory) {
        return new MongoTemplate(mongoDbFactory);
    }

    @Bean(name = "generalAccessorMongodb")
    public GeneralAccessor generalAccessorMongodb(MongoTemplate template, SessionDataStore sessionDataStore) {
        return new GeneralAccessorMongoImpl(template, sessionDataStore);
    }

    @Bean(name = "generalDictAccessorMongodb")
    public GeneralDictAccessor generalDictAccessorMongodb(MongoTemplate template, SessionDataStore sessionDataStore) {
        return new GeneralDictAccessorMongoImpl(template, sessionDataStore);
    }

    /**
     * 创建一个通用的数据访问器
     *
     * @param context Spring IoC上下文
     * @return 数据访问器
     */
    @Bean(name = "generalAccessor")
    public GeneralAccessor generalAccessor(ApplicationContext context) {
        return context.getBean("generalAccessorMongodb", GeneralAccessor.class);
    }

    /**
     * 创建一个通用的字典数据访问器
     *
     * @param context Spring IoC上下文
     * @return 数据访问器
     */
    @Bean(name = "generalDictAccessor")
    public GeneralDictAccessor generalDictAccessor(ApplicationContext context) {
        return context.getBean("generalDictAccessorMongodb", GeneralDictAccessor.class);
    }
}
