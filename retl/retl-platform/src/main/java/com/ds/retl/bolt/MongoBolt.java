package com.ds.retl.bolt;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ds.retl.mongo.MongoOperate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;

import java.util.Map;

/**
 * 基于MongoDB数据库的数据持久化Bolt类，能够将处理后的数据或错误信息持久化保存到MongoDB数据库中。
 *
 * @author : john.peng created on date : 2017/9/13
 */
public class MongoBolt extends BasePersistBolt {
    private static final Log logger = LogFactory.getLog(MongoBolt.class);

    private MongoOperate mongoOperate = null;
    private String collectionName = null;

    /**
     * 默认的构造函数
     */
    public MongoBolt() {
        super();
    }

    /**
     * 默认的构造函数
     *
     * @param configuration 初始化配置信息
     */
    public MongoBolt(JSONObject configuration) {
        super(configuration);
    }

    /**
     * {@inheritDoc}
     *
     * @see BasePersistBolt#initialize(Map, JSONObject)
     */
    @Override
    public void initialize(Map stormConf, JSONObject configuration) {
        // 初始化MongoDatabase
        String uri = configuration.getString("uri"),
                databaseName = configuration.getString("databaseName"),
                collectionName = configuration.getString("collectionName");
        if (StringUtils.isBlank(uri)) {
            String message = "Not define the URI for mongodb.";
            if (logger.isErrorEnabled()) {
                logger.error(message);
            }
            throw new IllegalArgumentException(message);
        }
        if (StringUtils.isBlank(databaseName)) {
            String message = "Not define the database name for mongodb.";
            if (logger.isErrorEnabled()) {
                logger.error(message);
            }
            throw new IllegalArgumentException(message);
        }
        if (StringUtils.isBlank(collectionName)) {
            String message = "Not define the collection name for mongodb.";
            if (logger.isErrorEnabled()) {
                logger.error(message);
            }
            throw new IllegalArgumentException(message);
        }
        this.mongoOperate = new MongoOperate(uri, databaseName);
        this.collectionName = collectionName;
    }

    /**
     * {@inheritDoc}
     *
     * @see BasePersistBolt#saveData2Db(JSONObject, JSONObject)
     */
    @Override
    public void saveData2Db(JSONObject managedJson, JSONObject data) throws Exception {
        mongoOperate.saveData2Db(collectionName, data.toJSONString());
    }

    /**
     * {@inheritDoc}
     *
     * @see BasePersistBolt#saveError2Db(JSONObject, JSONObject, JSONArray)
     */
    @Override
    public void saveError2Db(JSONObject managedJson, JSONObject data, JSONArray errors) throws Exception {
        long _structuredDate = managedJson.getLongValue("_structuredDate");
        for (int index = 0; index < errors.size(); index++) {
            JSONObject error = errors.getJSONObject(index);
            error.put("_structuredDate", _structuredDate);
            error.put("_errorDate", error.getLongValue("updatedtime"));
            mongoOperate.saveData2Db("errors", error.toJSONString());
        }
    }
}
