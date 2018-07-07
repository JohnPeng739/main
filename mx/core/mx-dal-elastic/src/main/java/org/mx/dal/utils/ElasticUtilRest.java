package org.mx.dal.utils;

import com.alibaba.fastjson.JSON;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.open.OpenIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
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
import org.elasticsearch.index.query.QueryBuilder;
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
import org.mx.dal.entity.ElasticBaseEntity;
import org.mx.dal.error.UserInterfaceDalErrorException;
import org.mx.dal.service.GeneralAccessor;
import org.mx.dal.session.SessionDataStore;
import org.mx.error.UserInterfaceSystemErrorException;
import org.springframework.core.env.Environment;

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
 * Date time 2018/4/1 上午9:31
 */
public class ElasticUtilRest implements ElasticUtil {
    private static final Log logger = LogFactory.getLog(ElasticUtilRest.class);

    private Environment env;
    private SessionDataStore sessionDataStore;

    private RestHighLevelClient client = null;
    private Map<String, String> indexes = new HashMap<>(), revIndexes = new HashMap<>();

    /**
     * 默认的构造函数
     *
     * @param env              Spring环境上下文
     * @param sessionDataStore 会话数据服务接口
     */
    public ElasticUtilRest(Environment env, SessionDataStore sessionDataStore) {
        super();
        this.env = env;
        this.sessionDataStore = sessionDataStore;
    }

    /**
     * {@inheritDoc}
     *
     * @see ElasticUtil#destroy()
     */
    public void destroy() throws Exception {
        if (client != null) {
            // CloseIndexRequest request = new CloseIndexRequest(this.index);
            // client.indices().close(request);
            client.close();
            client = null;
        }
    }

    @SuppressWarnings("unchecked")
    private void scanElasticEntitiesAndInitialize() {
        String basePakcages = env.getProperty("elastic.entity.base");
        String[] packages = StringUtils.split(basePakcages, true, true);
        for (String p : packages) {
            ClassUtils.scanPackage(p, className -> {
                try {
                    Class<? extends ElasticBaseEntity> clazz = (Class<? extends ElasticBaseEntity>) Class.forName(className);
                    createIndex(clazz);
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

    /**
     * {@inheritDoc}
     *
     * @see ElasticUtil#createIndex(Class)
     */
    @Override
    public <T extends Base> void createIndex(Class<T> clazz) {
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
                    // 为了预防可能配置了分词器，却忘记配置正确的类型，自动更正为"text"类型。
                    fieldProperties.put("type", "text");
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
                    try {
                        client.indices().create(request);
                        if (logger.isDebugEnabled()) {
                            logger.debug(String.format("Create index[%s] successfully.", index));
                        }
                    } catch (IOException ex1) {
                        if (logger.isErrorEnabled()) {
                            logger.error(String.format("Create the index[%s] fail.", index), ex1);
                        }
                        throw new UserInterfaceDalErrorException(UserInterfaceDalErrorException.DalErrors.ES_INDEX_FAIL);
                    }
                }
            } catch (IOException ex) {
                if (logger.isWarnEnabled()) {
                    logger.warn(String.format("Create index[%s] fail.", index), ex);
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
     * @see ElasticUtil#deleteIndex(Class)
     */
    @Override
    public <T extends Base> void deleteIndex(Class<T> clazz) {
        ElasticIndex annotationIndex = clazz.getAnnotation(ElasticIndex.class);
        if (annotationIndex != null) {
            // 是一个Elastic实体类，需要处理索引
            String index = annotationIndex.value();
            if (StringUtils.isBlank(index)) {
                index = clazz.getName();
            }
            // 删除Index
            try {
                OpenIndexRequest reqOpen = new OpenIndexRequest(index);
                client.indices().open(reqOpen);
                DeleteIndexRequest reqDelete = new DeleteIndexRequest(index);
                client.indices().delete(reqDelete);
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Delete the index[%s] successfully.", index));
                }
            } catch (IOException ex) {
                if (logger.isWarnEnabled()) {
                    logger.warn(String.format("Delete the index[%s] fail.", index), ex);
                }
            }
            // 清理缓存
            String className = clazz.getName();
            indexes.remove(className);
            revIndexes.remove(index);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see ElasticUtil#init()
     */
    public void init() {
        int servers = env.getProperty("elastic.servers", Integer.class, 0);
        if (servers <= 0) {
            if (logger.isErrorEnabled()) {
                logger.error("There are not define the elastic api server.");
            }
            return;
        }

        HttpHost[] elasticServers = new HttpHost[servers];
        for (int index = 1; index <= servers; index++) {
            String protocol = env.getProperty(String.format("elastic.servers.%d.protocol", index), "http");
            String server = env.getProperty(String.format("elastic.servers.%d.server", index), "localhost");
            int port = env.getProperty(String.format("elastic.servers.%d.port", index), Integer.class, 9200);
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
                throw new UserInterfaceDalErrorException(UserInterfaceDalErrorException.DalErrors.ENTITY_INVALID_BASE);
            }
        } else {
            throw new UserInterfaceDalErrorException(UserInterfaceDalErrorException.DalErrors.ENTITY_INDEX_NOT_FOUND);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see ElasticUtil#search(org.mx.dal.service.GeneralAccessor.ConditionGroup, Class, Pagination)
     */
    @Override
    public <T extends Base> SearchResponse search(GeneralAccessor.ConditionGroup group, Class<? extends Base> clazz,
                                                  Pagination pagination)
            throws UserInterfaceDalErrorException {
        return search(group, Collections.singletonList(clazz), pagination);
    }

    /**
     * 对条件组创建查询
     *
     * @param group 条件组
     * @return 查询
     */
    private BoolQueryBuilder createQueryGroupBuilder(GeneralAccessor.ConditionGroup group) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (group.getItems().size() == 1) {
            return boolQueryBuilder.must(createQueryBuilder(group.getOperateType(),
                    (GeneralAccessor.ConditionTuple) group.getItems().get(0)));
        } else {
            for (GeneralAccessor.ConditionGroup subGroup : group.getItems()) {
                QueryBuilder subQueryBuilder = createQueryGroupBuilder(subGroup);
                if (group.getOperateType() == GeneralAccessor.ConditionGroup.OperateType.AND) {
                    boolQueryBuilder.must(subQueryBuilder);
                } else {
                    boolQueryBuilder.should(subQueryBuilder);
                }
            }
            return boolQueryBuilder;
        }
    }

    /**
     * 对条件创建查询
     *
     * @param operateType 连接类型
     * @param tuple       条件
     * @return 查询
     */
    private QueryBuilder createQueryBuilder(GeneralAccessor.ConditionGroup.OperateType operateType,
                                            GeneralAccessor.ConditionTuple tuple) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if (operateType == GeneralAccessor.ConditionGroup.OperateType.AND) {
            switch (tuple.operate) {
                case CONTAIN:
                    queryBuilder.must(QueryBuilders.termQuery(tuple.field, tuple.value));
                    break;
                case EQ:
                    queryBuilder.must(QueryBuilders.matchQuery(tuple.field, tuple.value));
                    break;
                case LT:
                    queryBuilder.must(QueryBuilders.rangeQuery(tuple.field).lt(tuple.value));
                    break;
                case GT:
                    queryBuilder.must(QueryBuilders.rangeQuery(tuple.field).gt(tuple.value));
                    break;
                case LTE:
                    queryBuilder.must(QueryBuilders.rangeQuery(tuple.field).lte(tuple.value));
                    break;
                case GTE:
                    queryBuilder.must(QueryBuilders.rangeQuery(tuple.field).gte(tuple.value));
                    break;
                default:
                    if (logger.isErrorEnabled()) {
                        logger.error(String.format("Unsupported the operate type: %s.", tuple.operate));
                    }
                    throw new UserInterfaceSystemErrorException(
                            UserInterfaceSystemErrorException.SystemErrors.SYSTEM_UNSUPPORTED_OPERATE);
            }
        } else {
            switch (tuple.operate) {
                case CONTAIN:
                    queryBuilder.should(QueryBuilders.termQuery(tuple.field, tuple.value));
                    break;
                case EQ:
                    queryBuilder.should(QueryBuilders.matchQuery(tuple.field, tuple.value));
                    break;
                case LT:
                    queryBuilder.should(QueryBuilders.rangeQuery(tuple.field).lt(tuple.value));
                    break;
                case GT:
                    queryBuilder.should(QueryBuilders.rangeQuery(tuple.field).gt(tuple.value));
                    break;
                case LTE:
                    queryBuilder.should(QueryBuilders.rangeQuery(tuple.field).lte(tuple.value));
                    break;
                case GTE:
                    queryBuilder.should(QueryBuilders.rangeQuery(tuple.field).gte(tuple.value));
                    break;
                default:
                    if (logger.isErrorEnabled()) {
                        logger.error(String.format("Unsupported the operate type: %s.", tuple.operate));
                    }
                    throw new UserInterfaceSystemErrorException(
                            UserInterfaceSystemErrorException.SystemErrors.SYSTEM_UNSUPPORTED_OPERATE);
            }
        }
        return queryBuilder;
    }

    /**
     * {@inheritDoc} <br>
     * 在没有传入分页对象的情况下，最多返回前1000条记录。
     *
     * @see ElasticUtil#search(org.mx.dal.service.GeneralAccessor.ConditionGroup, Class, Pagination)
     */
    @Override
    public <T extends Base> SearchResponse search(GeneralAccessor.ConditionGroup group, List<Class<? extends Base>> classes,
                                                  Pagination pagination)
            throws UserInterfaceDalErrorException {
        SearchSourceBuilder builder = new SearchSourceBuilder();
        if (group == null || group.getItems().isEmpty()) {
            builder.query(QueryBuilders.matchAllQuery());
        } else {
            builder.query(createQueryGroupBuilder(group));
        }
        if (pagination != null) {
            builder.from((pagination.getPage() - 1) * pagination.getSize());
            builder.size(pagination.getSize());
        } else {
            // 默认不分页的情况下，返回前1000条记录
            builder.size(1000);
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
     * 将指定实体转化为Elastic Rest请求
     *
     * @param t   实体对象
     * @param <T> 实体泛型
     * @return Elastic REST请求
     */
    @SuppressWarnings("unchecked")
    private <T extends Base> DocWriteRequest createRequest(T t) {
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
            return request;
        } else {
            UpdateRequest request = new UpdateRequest(index, index, t.getId());
            request.doc(JSON.toJSONString(t), XContentType.JSON);
            return request;
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
        if (t == null) {
            if (logger.isWarnEnabled()) {
                logger.warn("The entity is null.");
            }
            return t;
        }
        try {
            DocWriteRequest request = createRequest(t);
            if (request instanceof IndexRequest) {
                client.index((IndexRequest) request);
            } else {
                client.update((UpdateRequest) request);
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
     * @see ElasticUtil#index(List)
     */
    @Override
    public <T extends Base> List<T> index(List<T> ts) {
        if (ts == null || ts.isEmpty()) {
            if (logger.isWarnEnabled()) {
                logger.warn("The entity's list is null or empty.");
            }
            return ts;
        }
        BulkRequest bulkRequest = new BulkRequest();
        for (T t : ts) {
            DocWriteRequest request = createRequest(t);
            bulkRequest.add(request);
        }
        try {
            client.bulk(bulkRequest);
        } catch (IOException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Bulk save entities fail, total: %d.", ts.size()), ex);
            }
            throw new UserInterfaceDalErrorException(UserInterfaceDalErrorException.DalErrors.ES_INDEX_FAIL);
        }
        return ts;
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
