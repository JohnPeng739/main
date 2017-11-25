package com.ds.retl.mongo;

import com.alibaba.fastjson.JSON;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.Document;

import java.util.Map;

/**
 * MongoDB数据库基础操作工具类
 *
 * @author : john.peng created on date : 2017/9/13
 */
public class MongoOperate {
    private static final Log logger = LogFactory.getLog(MongoOperate.class);

    private MongoClient mongoClient = null;
    private MongoDatabase mongoDatabase = null;

    /**
     * 构造函数
     *
     * @param uri          MongoDB连接地址
     * @param databaseName 数据库名称
     */
    public MongoOperate(String uri, String databaseName) {
        super();
        this.mongoClient = new MongoClient(new MongoClientURI(uri));
        this.mongoDatabase = mongoClient.getDatabase(databaseName);
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Initialize MongoDatabase[%s] successfully.", databaseName));
        }
    }

    /**
     * 保存数据到数据库中
     *
     * @param collectionName 集合名称（类似于表名）
     * @param json           待存储的JSON字符串
     */
    public void saveData2Db(String collectionName, String json) {
        MongoCollection<Document> collection = this.mongoDatabase.getCollection(collectionName);
        Document document = new Document(JSON.parseObject(json, Map.class));
        collection.insertOne(document);
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Insert data into %s successfully, json: %s.", collectionName, json));
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see Object#finalize()
     */
    @Override
    protected void finalize() throws Throwable {
        this.mongoDatabase = null;
        if (this.mongoClient != null) {
            this.mongoClient.close();
            this.mongoClient = null;
        }
        super.finalize();
    }
}
