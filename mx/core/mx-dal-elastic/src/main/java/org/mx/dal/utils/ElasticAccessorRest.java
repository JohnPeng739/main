package org.mx.dal.utils;

import com.alibaba.fastjson.JSON;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.open.OpenIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.mx.DigestUtils;
import org.mx.StringUtils;
import org.mx.dal.Pagination;
import org.mx.dal.entity.Base;
import org.mx.dal.entity.BaseDict;
import org.mx.dal.error.UserInterfaceDalErrorException;
import org.mx.dal.service.GeneralAccessor;
import org.mx.dal.session.SessionDataStore;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 描述： 采用Restful方式进行Elastic访问的实现类
 *
 * @author John.Peng
 *         Date time 2018/4/1 上午9:31
 */
@Component("elasticAccessorRest")
public class ElasticAccessorRest implements ElasticAccessor, InitializingBean, DisposableBean {
    private static final Log logger = LogFactory.getLog(ElasticAccessorRest.class);

    @Autowired
    private Environment env = null;

    @Autowired
    private SessionDataStore sessionDataStore = null;

    private RestHighLevelClient client = null;
    private String index = "default";

    /**
     * {@inheritDoc}
     *
     * @see DisposableBean#destroy()
     */
    @Override
    public void destroy() throws Exception {
        if (client != null) {
            // CloseIndexRequest request = new CloseIndexRequest(this.index);
            // client.indices().close(request);
            client.close();
            client = null;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        int servers = env.getProperty("elastic.servers", Integer.class, 0);
        if (servers <= 0) {
            if (logger.isErrorEnabled()) {
                logger.error("There are not define the elastic api server.");
            }
            return;
        }
        this.index = env.getProperty("elastic.index", "default");
        HttpHost[] elasticServers = new HttpHost[servers];
        for (int index = 1; index <= servers; index++) {
            String protocol = env.getProperty("elastic.api.protocol", "http");
            String server = env.getProperty("elastic.api.server", "localhost");
            int port = env.getProperty("elastic.api.port", Integer.class, 9200);
            elasticServers[index - 1] = new HttpHost(server, port, protocol);
        }
        client = new RestHighLevelClient(RestClient.builder(elasticServers));
        // 初始化Index
        try {
            OpenIndexRequest request = new OpenIndexRequest(this.index);
            client.indices().open(request);
        } catch (ElasticsearchStatusException ex) {
            if (ex.status() == RestStatus.NOT_FOUND) {
                CreateIndexRequest request = new CreateIndexRequest(this.index);
                client.indices().create(request);
            }
        } catch (IOException ex) {
            if (logger.isWarnEnabled()) {
                logger.warn("Open index fail.", ex);
            }
        }
        if (logger.isInfoEnabled()) {
            logger.info("Create the elastic search api connection successfully.");
        }
    }

    private <T extends Base> SearchResponse search(List<GeneralAccessor.ConditionTuple> tuples, Class<T> clazz,
                                                   Pagination pagination)
            throws UserInterfaceDalErrorException {
        SearchSourceBuilder builder = new SearchSourceBuilder();
        if (tuples == null || tuples.isEmpty()) {
            builder.query(QueryBuilders.matchAllQuery());
        } else {
            BoolQueryBuilder query = QueryBuilders.boolQuery();
            tuples.forEach(tuple -> query.must(QueryBuilders.termQuery(tuple.field, tuple.value)));
            builder.query(query);
        }
        if (pagination != null) {
            builder.from((pagination.getPage() - 1) * pagination.getSize());
            builder.size(pagination.getSize());
        }
        SearchRequest request = new SearchRequest(index);
        request.types(clazz.getName());
        request.source(builder);
        try {
            SearchResponse response = client.search(request);
            return response;
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Search fail from elastic.", ex);
            }
            throw new UserInterfaceDalErrorException(UserInterfaceDalErrorException.DalErrors.DB_OPERATE_FAIL);
        }
    }

    private List<GeneralAccessor.ConditionTuple> attachValidCondition(List<GeneralAccessor.ConditionTuple> tuples,
                                                                      boolean isValid) {
        if (isValid) {
            if (tuples == null) {
                tuples = Arrays.asList(new GeneralAccessor.ConditionTuple("valid", true));
            } else {
                tuples.add(new GeneralAccessor.ConditionTuple("valid", true));
            }
        }
        return tuples;
    }

    /**
     * {@inheritDoc}
     *
     * @see ElasticAccessor#count(Class, boolean)
     */
    @Override
    public <T extends Base> long count(Class<T> clazz, boolean isValid) {
        SearchResponse response = search(attachValidCondition(null, isValid), clazz, null);
        return response.getHits().getTotalHits();
    }

    /**
     * {@inheritDoc}
     *
     * @see ElasticAccessor#list(Class, boolean)
     */
    @Override
    public <T extends Base> List<T> list(Class<T> clazz, boolean isValid) throws UserInterfaceDalErrorException {
        SearchResponse response = search(attachValidCondition(null, isValid), clazz, null);
        List<T> list = new ArrayList<T>();
        response.getHits().forEach(hit -> list.add(JSON.parseObject(hit.getSourceAsString(), clazz)));
        return list;
    }

    /**
     * {@inheritDoc}
     *
     * @see ElasticAccessor#list(Pagination, Class, boolean)
     */
    @Override
    public <T extends Base> List<T> list(Pagination pagination, Class<T> clazz, boolean isValid) throws UserInterfaceDalErrorException {
        SearchResponse response = search(attachValidCondition(null, isValid), clazz, null);
        if (response.status() == RestStatus.OK) {
            List<T> list = new ArrayList<T>();
            pagination.setTotal((int) response.getHits().getTotalHits());
            response.getHits().forEach(hit -> {
                list.add(JSON.parseObject(hit.getSourceAsString(), clazz));
            });
            return list;
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see ElasticAccessor#getById(String, Class)
     */
    @Override
    public <T extends Base> T getById(String id, Class<T> clazz) throws UserInterfaceDalErrorException {
        GetRequest request = new GetRequest(index, clazz.getName(), id);
        try {
            GetResponse response = client.get(request);
            return response.isExists() ? JSON.parseObject(response.getSourceAsString(), clazz) : null;
        } catch (IOException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Get data fail, index: %s, type: %s, id: %s.", index, clazz.getName(), id));
            }
            throw new UserInterfaceDalErrorException(UserInterfaceDalErrorException.DalErrors.DB_OPERATE_FAIL);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see ElasticAccessor#find(List, Class)
     */
    @Override
    public <T extends Base> List<T> find(List<GeneralAccessor.ConditionTuple> tuples, Class<T> clazz)
            throws UserInterfaceDalErrorException {
        SearchResponse response = search(tuples, clazz, null);
        List<T> list = new ArrayList<T>();
        response.getHits().forEach(hit -> list.add(JSON.parseObject(hit.getSourceAsString(), clazz)));
        return list;
    }

    /**
     * {@inheritDoc}
     *
     * @see ElasticAccessor#save(Base)
     */
    @Override
    public <T extends Base> T save(T t) throws UserInterfaceDalErrorException {
        try {
            Class<T> clazz = (Class<T>) t.getClass();
            boolean isNew;
            if (StringUtils.isBlank(t.getId())) {
                // 新数据
                t.setId(DigestUtils.uuid());
                t.setCreatedTime(System.currentTimeMillis());
                isNew = true;
            } else {
                T check = getById(t.getId(), clazz);
                if (check != null) {
                    // 修改数据
                    t.setCreatedTime(check.getCreatedTime());
                    if (check instanceof BaseDict) {
                        ((BaseDict) t).setCode(((BaseDict) check).getCode());
                    }
                    isNew = false;
                } else {
                    isNew = true;
                }
            }
            t.setUpdatedTime(System.currentTimeMillis());
            t.setOperator(sessionDataStore.getCurrentUserCode());
            if (isNew) {
                IndexRequest request = new IndexRequest(index, t.getClass().getName(), t.getId());
                request.source(JSON.toJSONString(t), XContentType.JSON);
                client.index(request);
            } else {
                UpdateRequest request = new UpdateRequest(index, t.getClass().getName(), t.getId());
                request.doc(JSON.toJSONString(t), XContentType.JSON);
                client.update(request);
            }
            return getById(t.getId(), (Class<T>) t.getClass());
        } catch (IOException ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Save the data into elastic fail.", ex);
            }
            throw new UserInterfaceDalErrorException(UserInterfaceDalErrorException.DalErrors.DB_OPERATE_FAIL);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see ElasticAccessor#remove(Base, boolean)
     */
    @Override
    public <T extends Base> T remove(T t, boolean logicRemove) throws UserInterfaceDalErrorException {
        if (logicRemove) {
            // 逻辑删除
            t.setValid(false);
            return save(t);
        } else {
            // 物理删除
            t = getById(t.getId(), (Class<T>) t.getClass());
            if (t == null) {
                throw new UserInterfaceDalErrorException(UserInterfaceDalErrorException.DalErrors.ENTITY_NOT_FOUND);
            }
            DeleteRequest request = new DeleteRequest(index, t.getClass().getName(), t.getId());
            try {
                client.delete(request);
                return t;
            } catch (IOException ex) {
                if (logger.isErrorEnabled()) {
                    logger.error("Delete the data from elastic fail.", ex);
                }
                throw new UserInterfaceDalErrorException(UserInterfaceDalErrorException.DalErrors.DB_OPERATE_FAIL);
            }
        }
    }
}
