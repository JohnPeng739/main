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
import org.mx.ClassUtils;
import org.mx.DigestUtils;
import org.mx.StringUtils;
import org.mx.dal.Pagination;
import org.mx.dal.annotation.ElasticField;
import org.mx.dal.annotation.ElasticIndex;
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
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * 描述： 采用Restful方式进行Elastic访问的实现类
 *
 * @author John.Peng
 *         Date time 2018/4/1 上午9:31
 */
@Component("elasticUtilRest")
public class ElasticUtilRest implements ElasticUtil, InitializingBean, DisposableBean {
    private static final Log logger = LogFactory.getLog(ElasticUtilRest.class);

    @Autowired
    private Environment env = null;

    @Autowired
    private SessionDataStore sessionDataStore = null;

    private RestHighLevelClient client = null;
    private Map<String, String> indexes = new HashMap<>(), revIndexes = new HashMap<>();

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

    private void scanElasticEntitiesAndInitialize() throws Exception {
        String basePakcages = env.getProperty("elastic.entity.base");
        String[] packages = StringUtils.split(basePakcages, true, true);
        for (String p : packages) {
            ClassUtils.scanPackage(p, className -> {
                try {
                    Class<?> clazz = Class.forName(className);
                    initializeIndex(clazz);
                } catch (Exception ex) {
                    if (logger.isWarnEnabled()) {
                        logger.warn(String.format("Initialize the index of the class[%s] fail.", className), ex);
                    }
                }
            });
        }
    }

    private void scanClassFields(Class<?> clazz, BiConsumer<String, ElasticField> action) {
        if (clazz.getName().equalsIgnoreCase("java.lang.Object")) {
            // 已经到了顶层对象，跳出
            return;
        }
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            ElasticField annotationField = field.getAnnotation(ElasticField.class);
            if (annotationField != null) {
                action.accept(field.getName(), annotationField);
            }
        }
        scanClassFields(clazz.getSuperclass(), action);
    }

    private void initializeIndex(Class<?> clazz) throws Exception {
        ElasticIndex annotationIndex = clazz.getAnnotation(ElasticIndex.class);
        if (annotationIndex != null) {
            // 是一个Elastic实体类，需要处理索引
            String index = annotationIndex.value();
            if (StringUtils.isBlank(index)) {
                index = clazz.getName();
            }
            Map<String, Object> properties = new HashMap<>();
            scanClassFields(clazz, (name, field) -> {
                String type = field.type(), anaylyzer = field.analyzer();
                if (!StringUtils.isBlank(field.value())) {
                    name = field.value();
                }
                Map<String, Object> fieldProperties = new HashMap<>();
                fieldProperties.put("type", type);
                if (!StringUtils.isBlank(anaylyzer)) {
                    fieldProperties.put("analyzer", anaylyzer);
                }
                properties.put(name, fieldProperties);
            });
            // 初始化Index
            try {
                OpenIndexRequest request = new OpenIndexRequest(index);
                client.indices().open(request);
            } catch (ElasticsearchStatusException ex) {
                if (ex.status() == RestStatus.NOT_FOUND) {
                    CreateIndexRequest request = new CreateIndexRequest(index);
                    Map<String, Object> settings = new HashMap<>();
                    settings.put("number_of_shards",
                            env.getProperty("elastic.index.shards", Integer.class, 3));
                    settings.put("number_of_replicas",
                            env.getProperty("elastic.index.replicas", Integer.class, 2));
                    request.settings(settings);
                    Map<String, Map<String, Object>> mapping = new HashMap<>();
                    mapping.put(index, new HashMap<>());
                    mapping.get(index).put("properties", properties);
                    request.mapping(index, mapping);
                    client.indices().create(request);
                }
            } catch (IOException ex) {
                if (logger.isWarnEnabled()) {
                    logger.warn("Open index fail.", ex);
                }
            }
            String className = clazz.getName();
            indexes.put(className, index);
            revIndexes.put(index, className);
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

        HttpHost[] elasticServers = new HttpHost[servers];
        for (int index = 1; index <= servers; index++) {
            String protocol = env.getProperty("elastic.api.protocol", "http");
            String server = env.getProperty("elastic.api.server", "localhost");
            int port = env.getProperty("elastic.api.port", Integer.class, 9200);
            elasticServers[index - 1] = new HttpHost(server, port, protocol);
        }
        client = new RestHighLevelClient(RestClient.builder(elasticServers));
        scanElasticEntitiesAndInitialize();
        if (logger.isInfoEnabled()) {
            logger.info("Create the elastic search api connection successfully.");
        }
    }

    private String getIndex(Class<?> clazz) {
        String name = clazz.getName();
        if (indexes.containsKey(name)) {
            return indexes.get(name);
        } else {
            throw new UserInterfaceDalErrorException(UserInterfaceDalErrorException.DalErrors.ENTITY_INDEX_NOT_FOUND);
        }
    }

    public Class<?> getIndexClass(String index) {
        if (revIndexes.containsKey(index)) {
            String className = revIndexes.get(index);
            try {
                return Class.forName(className);
            } catch (ClassNotFoundException ex) {
                if (logger.isWarnEnabled()) {
                    logger.error(String.format("The class[%s] not existed.", className), ex);
                }
                throw new UserInterfaceDalErrorException(UserInterfaceDalErrorException.DalErrors.ENTITY_CLASS_INVALID);
            }
        } else {
            throw new UserInterfaceDalErrorException(UserInterfaceDalErrorException.DalErrors.ENTITY_INDEX_NOT_FOUND);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see ElasticUtil#search(List, Class, Pagination)
     */
    @Override
    public <T extends Base> SearchResponse search(List<GeneralAccessor.ConditionTuple> tuples, Class<T> clazz,
                                                  Pagination pagination)
            throws UserInterfaceDalErrorException {
        return search(tuples, Collections.singletonList(clazz), pagination);
    }

    /**
     * {@inheritDoc}
     *
     * @see ElasticUtil#search(List, Class, Pagination)
     */
    @Override
    public SearchResponse search(List<GeneralAccessor.ConditionTuple> tuples, List<Class<? extends Base>> classes,
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
        String[] indices;
        if (classes == null || classes.isEmpty()) {
            indices = new String[]{"*"};
        } else {
            indices = new String[classes.size()];
            for (int index = 0; index < classes.size(); index++) {
                indices[index] = getIndex(classes.get(index));
            }
        }
        SearchRequest request = new SearchRequest(indices);
        request.source(builder);
        try {
            return client.search(request);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Search fail from elastic.", ex);
            }
            throw new UserInterfaceDalErrorException(UserInterfaceDalErrorException.DalErrors.DB_OPERATE_FAIL);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see ElasticUtil#getById(String, Class)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends Base> T getById(String id, Class<T> clazz) {
        String index = getIndex(clazz);
        GetRequest request = new GetRequest(index, index, id);
        try {
            GetResponse response = client.get(request);
            return response.isExists() ? JSON.parseObject(response.getSourceAsString(),
                    (Class<T>) getIndexClass(request.index())) : null;
        } catch (IOException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Get data fail, index: %s, type: %s, id: %s.", index, index, id));
            }
            throw new UserInterfaceDalErrorException(UserInterfaceDalErrorException.DalErrors.DB_OPERATE_FAIL);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see ElasticUtil#index(Base)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends Base> T index(T t) {
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
            String index = getIndex(clazz);
            if (isNew) {
                IndexRequest request = new IndexRequest(index, index, t.getId());
                request.source(JSON.toJSONString(t), XContentType.JSON);
                client.index(request);
            } else {
                UpdateRequest request = new UpdateRequest(index, index, t.getId());
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
     * @see ElasticUtil#remove(Base, boolean)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends Base> T remove(T t, boolean logicRemove) {
        if (logicRemove) {
            // 逻辑删除
            t.setValid(false);
            return index(t);
        } else {
            // 物理删除
            Class<T> clazz = (Class<T>) t.getClass();
            t = getById(t.getId(), clazz);
            if (t == null) {
                throw new UserInterfaceDalErrorException(UserInterfaceDalErrorException.DalErrors.ENTITY_NOT_FOUND);
            }
            String index = getIndex(clazz);
            DeleteRequest request = new DeleteRequest(index, index, t.getId());
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
