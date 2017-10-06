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
 * Created by john on 2017/9/13.
 */
public class MongoOperate {
    private static final Log logger = LogFactory.getLog(MongoOperate.class);

    private MongoClient mongoClient = null;
    private MongoDatabase mongoDatabase = null;

    public MongoOperate(String uri, String databaseName) {
        super();
        this.mongoClient = new MongoClient(new MongoClientURI(uri));
        this.mongoDatabase = mongoClient.getDatabase(databaseName);
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Initialize MongoDatabase[%s] successfully.", databaseName));
        }
    }

    public void saveData2Db(String collectionName, String json) {
        MongoCollection<Document> collection = this.mongoDatabase.getCollection(collectionName);
        Document document = new Document(JSON.parseObject(json, Map.class));
        collection.insertOne(document);
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Insert data into %s successfully, json: %s.", collectionName, json));
        }
    }

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
