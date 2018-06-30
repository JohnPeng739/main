package org.mx.dal.config;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.mx.dal.service.GeneralAccessor;
import org.mx.dal.service.GeneralDictAccessor;
import org.mx.dal.service.OperateLogService;
import org.mx.dal.service.impl.GeneralAccessorMongoImpl;
import org.mx.dal.service.impl.GeneralDictAccessorMongoImpl;
import org.mx.dal.service.impl.OperateLogServiceMongoImpl;
import org.mx.dal.session.SessionDataStore;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.util.Assert;

/**
 * 基于Mongodb的DAL实现的Java Configure定义
 * 扫描：org.mx.dal.service.impl中的组件
 * 加载配置：classpath:mongodb.properties
 *
 * @author : john.peng date : 2017/10/8
 */
@Import({DalConfig.class})
@ComponentScan({"org.mx.dal.service.impl"})
public class DalMongodbConfig {
    /**
     * 默认的构造函数
     */
    public DalMongodbConfig() {
        super();
    }

    /**
     * 创建MongodDB客户端
     *
     * @param env Spring IoC上下文环境
     * @return 客户端
     */
    @Bean(name = "mongoClient")
    public MongoClient mongoClient(Environment env) {
        String uri = env.getProperty("mongodb.uri");
        Assert.notNull(uri, "The Mongodb Uri not configured.");
        return new MongoClient(new MongoClientURI(uri));
    }

    /**
     * 创建MongoDB模版工具
     *
     * @param env Spring IoC上下文环境
     * @return 模版工具
     * @see #mongoClient(Environment)
     */
    @Bean(name = "mongoTemplate")
    public MongoTemplate mongoTemplate(Environment env) {
        String database = env.getProperty("database");
        if (database == null || database.length() <= 0) {
            database = "database";
        }
        return new MongoTemplate(mongoClient(env), database);
    }

    @Bean(name = "generalAccessorMongodb")
    public GeneralAccessor generalAccessorMongodb(MongoTemplate template, SessionDataStore sessionDataStore) {
        return new GeneralAccessorMongoImpl(template, sessionDataStore);
    }

    @Bean(name = "generalDictAccessorMongodb")
    public GeneralDictAccessor generalDictAccessorMongodb(MongoTemplate template, SessionDataStore sessionDataStore) {
        return new GeneralDictAccessorMongoImpl(template, sessionDataStore);
    }

    @Bean(name = "operateLogService")
    public OperateLogService operateLogServiceMongoDb(MongoTemplate template, SessionDataStore sessionDataStore) {
        return new OperateLogServiceMongoImpl(generalAccessorMongodb(template, sessionDataStore), sessionDataStore);
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
