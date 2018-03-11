package org.mx.dal.config;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.mx.dal.service.GeneralAccessor;
import org.mx.dal.service.GeneralDictAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.util.Assert;

import java.net.UnknownHostException;

/**
 * Created by john on 2017/10/8.
 */

/**
 * 基于Mongodb的DAL实现的Java Configure定义
 * 扫描：org.mx.dal.service.impl中的组件
 * 加载配置：classpath:mongodb.properties
 *
 * @author : john.peng date : 2017/10/8
 */
@Import({DalConfig.class})
@PropertySource("classpath:mongodb.properties")
@ComponentScan({"org.mx.dal.service.impl"})
public class DalMongodbConfig {
    @Autowired
    private Environment env = null;

    @Autowired
    private ApplicationContext context = null;

    /**
     * 默认的构造函数
     */
    public DalMongodbConfig() {
        super();
    }

    /**
     * 创建一个通用的数据访问器
     *
     * @return 数据访问器
     */
    @Bean(name = "generalAccessor")
    public GeneralAccessor generalAccessor() {
        return context.getBean("generalEntityAccessorMongodb", GeneralAccessor.class);
    }

    /**
     * 创建一个通用的字典数据访问器
     *
     * @return 数据访问器
     */
    @Bean(name = "generalDictAccessor")
    public GeneralDictAccessor generalDictAccessor() {
        return context.getBean("generalDictEntityAccessorMongodb", GeneralDictAccessor.class);
    }

    /**
     * 创建MongodDB客户端
     *
     * @return 客户端
     * @throws UnknownHostException Mongodb服务器配置异常
     */
    @Bean(name = "mongoClient")
    public MongoClient mongoClient() throws UnknownHostException {
        String uri = env.getProperty("mongodb.uri");
        Assert.notNull(uri, "The Mongodb Uri not configured.");
        return new MongoClient(new MongoClientURI(uri));
    }

    /**
     * 创建MongoDB模版工具
     *
     * @return 模版工具
     * @throws UnknownHostException Mongodb服务器配置异常
     * @see #mongoClient()
     */
    @Bean(name = "mongoTemplate")
    public MongoTemplate mongoTemplate() throws UnknownHostException {
        String database = env.getProperty("database");
        if (database == null || database.length() <= 0) {
            database = "database";
        }
        return new MongoTemplate(mongoClient(), database);
    }
}
