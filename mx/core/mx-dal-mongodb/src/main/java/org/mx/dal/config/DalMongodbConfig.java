package org.mx.dal.config;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.util.Assert;

import java.net.UnknownHostException;

/**
 * Created by john on 2017/10/8.
 */
@Configuration
@PropertySource("classpath:mongodb.properties")
@ComponentScan({"org.mx.dal.service.impl"})
public class DalMongodbConfig {
    @Autowired
    private Environment env = null;

    public DalMongodbConfig() {
        super();
    }

    @Bean(name = "mongoClient")
    public MongoClient mongoClient() throws UnknownHostException {
        String uri = env.getProperty("mongodb.uri");
        Assert.notNull(uri, "The Mongodb Uri not configured.");
        return new MongoClient(new MongoClientURI(uri));
    }

    @Bean(name="mongoTemplate")
    public MongoTemplate mongoTemplate() throws UnknownHostException {
        String database = env.getProperty("database");
        if (database == null || database.length() <= 0) {
            database = "database";
        }
        return new MongoTemplate(mongoClient(), database);
    }
}
