package org.mx.dal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

/**
 * 基于Mongodb的事务控制实现的Java Configure定义，事务必须在MongoDB V4.0+的副本集中使用。
 *
 * @author : john.peng date : 2018/12/27
 */
@Import({DalMongodbConfig.class})
@EnableTransactionManagement
public class DalMongodbTransactionConfig implements TransactionManagementConfigurer {
    private MongoTransactionManager transactionManager = null;

    /**
     * 默认的构造函数
     */
    public DalMongodbTransactionConfig() {
        super();
    }

    /**
     * 创建MongoDB数据库事务管理器
     *
     * @param mongoDbFactory MongoDB数据库工厂
     * @return 数据库事务管理器
     */
    @Bean
    public MongoTransactionManager transactionManager(MongoDbFactory mongoDbFactory) {
        transactionManager = new MongoTransactionManager(mongoDbFactory);
        return transactionManager;
    }

    /**
     * 创建注解驱动的事务管理器
     *
     * @return 事务管理器
     */
    @Override
    public MongoTransactionManager annotationDrivenTransactionManager() {
        return transactionManager;
    }
}
