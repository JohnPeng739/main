package org.mx.dal.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
import org.elasticsearch.action.search.*;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.geo.GeoDistance;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.AbstractQueryBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.mx.ClassUtils;
import org.mx.StringUtils;
import org.mx.dal.Pagination;
import org.mx.dal.annotation.ElasticField;
import org.mx.dal.annotation.ElasticIndex;
import org.mx.dal.entity.Base;
import org.mx.dal.entity.ElasticBaseEntity;
import org.mx.dal.entity.ElasticGeoPointBaseEntity;
import org.mx.dal.entity.GeoPointLocation;
import org.mx.dal.error.UserInterfaceDalErrorException;
import org.mx.dal.service.GeneralAccessor;
import org.mx.dal.utils.bean.IndicesInfoBean;
import org.mx.dal.utils.bean.NodeInfoBean;
import org.mx.error.UserInterfaceSystemErrorException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * 描述： 采用Restful方式进行Elastic访问的实现类
 *
 * @author John.Peng
 * Date time 2018/4/1 上午9:31
 */
public class ElasticUtilRest implements ElasticUtil, ElasticLowLevelUtil {
    private static final Log logger = LogFactory.getLog(ElasticUtilRest.class);

    private ElasticConfigBean elasticConfigBean;

    private RestHighLevelClient client = null;
    private Map<String, String> indexes = new HashMap<>(), revIndexes = new HashMap<>();

    /**
     * 默认的构造函数
     *
     * @param elasticConfigBean Elastic配置对象
     */
    public ElasticUtilRest(ElasticConfigBean elasticConfigBean) {
        super();
        this.elasticConfigBean = elasticConfigBean;
    }

    /**
     * {@inheritDoc}
     *
     * @see ElasticUtil#destroy()
     */
    public void destroy() throws Exception {
        if (client != null) {
            client.close();
            client = null;
        }
    }

    /**
     * 初始化扫描包路径下所有的Elastic Entity
     */
    @SuppressWarnings("unchecked")
    private void scanElasticEntitiesAndInitialize() {
        String[] packages = elasticConfigBean.getEntityBasePackages();
        if (packages.length > 0) {
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
        } else {
            if (logger.isWarnEnabled()) {
                logger.warn("There are not any elastic entity.");
            }
        }
    }

    /**
     * 扫描指定Elastic Entity类中的索引属性
     * @param clazz Elastic Entity类定义
     * @param action 扫描到索引属性后执行的Action
     */
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
     * @see ElasticLowLevelUtil#createIndex(String, Map, Map)
     */
    @Override
    public <T extends Base> void createIndex(String index, Map<String, Object> settings, Map<String, Object> properties) {
        // 初始化Index
        try {
            OpenIndexRequest request = new OpenIndexRequest(index);
            client.indices().open(request);
        } catch (ElasticsearchStatusException ex) {
            if (ex.status() == RestStatus.NOT_FOUND) {
                CreateIndexRequest request = new CreateIndexRequest(index);
                request.settings(settings);
                Map<String, Map<String, Object>> mapping = new HashMap<>();
                mapping.put("properties", properties);
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
                fieldProperties.put("store", field.store());
                if (!StringUtils.isBlank(anaylyzer)) {
                    fieldProperties.put("analyzer", anaylyzer);
                    // 为了预防可能配置了分词器，却忘记配置正确的类型，自动更正为"text"类型。
                    fieldProperties.put("type", "text");
                }
                properties.put(name, fieldProperties);
            });

            Map<String, Object> settings = new HashMap<>();
            settings.put("number_of_shards", elasticConfigBean.getShards());
            settings.put("number_of_replicas", elasticConfigBean.getReplicas());
            createIndex(index, settings, properties);
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
            deleteIndex(index);
            // 清理缓存
            String className = clazz.getName();
            indexes.remove(className);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see ElasticLowLevelUtil#deleteIndex(String)
     */
    public void deleteIndex(String id, String index) {
        if (getById(id, index) != null) {
            DeleteRequest request = new DeleteRequest(index, index, id);
            try {
                client.delete(request);
            } catch (IOException ex) {
                if (logger.isErrorEnabled()) {
                    logger.error("Delete the data from elastic fail.", ex);
                }
                throw new UserInterfaceDalErrorException(UserInterfaceDalErrorException.DalErrors.DB_OPERATE_FAIL);
            }
        } else {
            if (logger.isWarnEnabled()) {
                logger.warn(String.format("The document[%s] not found in index[%s].", id, index));
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see ElasticUtil#deleteIndex(List)
     */
    @Override
    public <T extends Base> void deleteIndex(List<T> ts) {
        if (ts == null || ts.isEmpty()) {
            if (logger.isWarnEnabled()) {
                logger.warn("The entity's list is null or empty.");
            }
            return;
        }
        BulkRequest bulkRequest = new BulkRequest();
        for (T t : ts) {
            DocWriteRequest request = createDeleteRequest(t);
            bulkRequest.add(request);
        }
        try {
            client.bulk(bulkRequest);
        } catch (IOException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Bulk delete entities fail, total: %d.", ts.size()), ex);
            }
            throw new UserInterfaceDalErrorException(UserInterfaceDalErrorException.DalErrors.ES_INDEX_FAIL);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see ElasticLowLevelUtil#deleteIndex(String)
     */
    @Override
    public void deleteIndex(String index) {
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
        revIndexes.remove(index);
    }

    /**
     * 触发一次Low Level的Elastic REST调用
     * @param path 访问REST路径
     * @param action 访问成功后数据处理Action
     */
    private void performLowLevelGetRequest(String path, Consumer<String> action) {
        try {
            InputStream in = client.getLowLevelClient()
                    .performRequest("GET", path).getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while (!StringUtils.isBlank(line = reader.readLine())) {
                action.accept(line);
            }
            in.close();
        } catch (IOException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Low level rest client invoke %s fail.", path), ex);
            }
            throw new UserInterfaceDalErrorException(UserInterfaceDalErrorException.DalErrors.ES_REST_FAIL);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see ElasticLowLevelUtil#getAllNodes() ()
     */
    @Override
    public List<NodeInfoBean> getAllNodes() {
        List<NodeInfoBean> list = new ArrayList<>();
        performLowLevelGetRequest("_cat/nodes", line -> list.add(new NodeInfoBean(line)));
        return list;
    }

    /**
     * {@inheritDoc}
     *
     * @see ElasticLowLevelUtil#getAllIndexes()
     */
    @Override
    public List<IndicesInfoBean> getAllIndexes() {
        List<IndicesInfoBean> list = new ArrayList<>();
        performLowLevelGetRequest("/_cat/indices", line -> list.add(new IndicesInfoBean(line)));
        return list;
    }

    /**
     * {@inheritDoc}
     *
     * @see ElasticUtil#init()
     */
    public void init() {
        if (elasticConfigBean.getServerNum() <= 0) {
            if (logger.isErrorEnabled()) {
                logger.error("There are not define the elastic api server.");
            }
            return;
        }

        HttpHost[] elasticServers = new HttpHost[elasticConfigBean.getServerNum()];
        for (int index = 0; index < elasticConfigBean.getServerNum(); index++) {
            ElasticConfigBean.ElasticServerConfig serverConfig = elasticConfigBean.getServerConfigs().get(index);
            elasticServers[index] = new HttpHost(serverConfig.getServer(), serverConfig.getPort(),
                    serverConfig.getProtocol());
        }
        client = new RestHighLevelClient(RestClient.builder(elasticServers));
        scanElasticEntitiesAndInitialize();
        if (logger.isInfoEnabled()) {
            logger.info("Create the elastic search api connection successfully.");
        }
    }

    /**
     * 获取指定Elastic Entity类对应的索引名
     * @param clazz Elastic Entity类定义
     * @return 索引名
     */
    private String getIndex(Class<?> clazz) {
        String name = clazz.getName();
        if (indexes.containsKey(name)) {
            return indexes.get(name);
        } else {
            throw new UserInterfaceDalErrorException(UserInterfaceDalErrorException.DalErrors.ENTITY_INDEX_NOT_FOUND);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see ElasticUtil#getIndexClass(String)
     */
    @Override
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
     * 对条件组创建查询
     *
     * @param group 条件组
     * @param clazz 实体类定义
     * @param highlightFields 搜索结果高亮显示字段列表（根据条件自动扫描）
     * @return 查询
     */
    private AbstractQueryBuilder createQueryGroupBuilder(GeneralAccessor.ConditionGroup group, Class<?> clazz,
                                                         Set<String> highlightFields) {
        if (group == null) {
            return QueryBuilders.matchAllQuery();
        }
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (group.getItems().size() == 1 && group instanceof GeneralAccessor.ConditionTuple) {
            // 添加field到高亮字段
            if (highlightFields != null &&
                    ((GeneralAccessor.ConditionTuple)group).operate == GeneralAccessor.ConditionTuple.ConditionOperate.CONTAIN) {
                highlightFields.add(((GeneralAccessor.ConditionTuple) group).field);
            }
            return boolQueryBuilder.must(createQueryBuilder(group.getOperateType(),
                    (GeneralAccessor.ConditionTuple) group.getItems().get(0), clazz));
        } else {
            for (GeneralAccessor.ConditionGroup subGroup : group.getItems()) {
                QueryBuilder subQueryBuilder = createQueryGroupBuilder(subGroup, clazz, highlightFields);
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
     * 判断Elastic Entity类中指定的属性是否定义为全文检索属性
     * @param field 字段
     * @param clazz Elastic Entity类定义
     * @return 如果是全文检索（TEXT）类型返回true，否则返回false。
     */
    private boolean isTextType(String field, Class<?> clazz) {
        if (clazz.getName().equalsIgnoreCase("java.lang.Object")) {
            return true;
        }
        try {
            ElasticField elasticField = clazz.getDeclaredField(field).getAnnotation(ElasticField.class);
            return "text".equalsIgnoreCase(elasticField.type());
        } catch (NoSuchFieldException ex) {
            return isTextType(field, clazz.getSuperclass());
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
                                            GeneralAccessor.ConditionTuple tuple, Class<?> clazz) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if (operateType == GeneralAccessor.ConditionGroup.OperateType.AND) {
            switch (tuple.operate) {
                case CONTAIN:
                    if (isTextType(tuple.field, clazz)) {
                        // 已经有全文检索索引，使用TERM搜索
                        queryBuilder.must(QueryBuilders.termQuery(tuple.field, tuple.value));
                    } else {
                        // 无全文检索索引，使用正则表达式搜索
                        queryBuilder.must(QueryBuilders.regexpQuery(tuple.field, String.format(".*(%s).*", tuple.value)));
                    }
                    break;
                case PREFIX:
                    queryBuilder.must(QueryBuilders.prefixQuery(tuple.field, (String) tuple.value));
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
                case IS_NULL:
                    queryBuilder.mustNot(QueryBuilders.existsQuery(tuple.field));
                    break;
                case IS_NOT_NULL:
                    queryBuilder.must(QueryBuilders.existsQuery(tuple.field));
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
                case PREFIX:
                    queryBuilder.should(QueryBuilders.prefixQuery(tuple.field, (String) tuple.value));
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
     * 根据输入的实体类，获取对应的索引列表
     *
     * @param classes 实体类列表
     * @return 索引列表
     */
    private String[] getIndices(List<Class<? extends Base>> classes) {
        String[] indices;
        if (classes == null || classes.isEmpty()) {
            indices = new String[]{"*"};
        } else {
            indices = new String[classes.size()];
            for (int index = 0; index < classes.size(); index++) {
                indices[index] = getIndex(classes.get(index));
            }
        }
        return indices;
    }

    /**
     * {@inheritDoc}
     *
     * @see ElasticUtil#search(org.mx.dal.service.GeneralAccessor.ConditionGroup, Class, Pagination)
     */
    @Override
    public <T extends Base> List<T> search(GeneralAccessor.ConditionGroup group, Class<? extends Base> clazz,
                                           Pagination pagination)
            throws UserInterfaceDalErrorException {
        return search(group, null, Collections.singletonList(clazz), pagination);
    }

    /**
     * 根据条件组、排序组等输入创建对应的全文检索请求
     * @param group 条件组
     * @param orderGroup 排序组
     * @param classes Elastic Entity类定义列表
     * @param pagination 分页对象
     * @return Elastic Search请求
     * @see SearchRequest
     */
    private SearchRequest createSearchRequest(GeneralAccessor.ConditionGroup group,
                                              GeneralAccessor.RecordOrderGroup orderGroup,
                                              List<Class<? extends Base>> classes,
                                              Pagination pagination) {
        SearchSourceBuilder builder = new SearchSourceBuilder();
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        Set<String> highlightFields = new HashSet<>();
        builder.query(createQueryGroupBuilder(group, classes.get(0), highlightFields));
        highlightFields.forEach(highlightBuilder::field);
        builder.highlighter(highlightBuilder);
        if (pagination != null) {
            builder.from((pagination.getPage() - 1) * pagination.getSize());
            builder.size(pagination.getSize());
        }
        if (orderGroup != null && !orderGroup.getOrders().isEmpty()) {
            orderGroup.getOrders().forEach(order -> {
                if (order.getType() == GeneralAccessor.RecordOrder.OrderType.ASC) {
                    builder.sort(order.getField(), SortOrder.ASC);
                } else {
                    builder.sort(order.getField(), SortOrder.DESC);
                }
            });
        }
        SearchRequest request = new SearchRequest(getIndices(classes));
        request.source(builder);
        return request;
    }

    /**
     * 执行一次Elastic查询
     * @param searchRequest Elastic Search请求
     * @param pagination 分页对象
     * @param <T> 泛型定义
     * @return 符合条件的Elastic Entity列表
     */
    @SuppressWarnings("unchecked")
    private <T extends Base> List<T> search(SearchRequest searchRequest, Pagination pagination) {
        try {
            // 设置滚动操作
            Scroll scroll = new Scroll(TimeValue.timeValueHours(1L));
            searchRequest.scroll(scroll);
            SearchResponse response = client.search(searchRequest);
            String scrollId = response.getScrollId();
            if (pagination != null) {
                pagination.setTotal((int) response.getHits().getTotalHits());
            }
            ArrayList<T> result = new ArrayList<>();
            while (response != null && response.getHits() != null && response.getHits().getHits() != null &&
                    response.getHits().getHits().length > 0) {
                for (SearchHit hit : response.getHits().getHits()) {
                    T t = JSON.parseObject(hit.getSourceAsString(), (Class<T>) getIndexClass(hit.getIndex()));
                    ((ElasticBaseEntity) t).setScore(hit.getScore());
                    // 注入高亮数据
                    if (hit.getHighlightFields() != null) {
                        hit.getHighlightFields().values().forEach(f -> {
                            String key = f.getName();
                            List<String> fragments = new ArrayList<>();
                            for (Text text : f.getFragments()) {
                                fragments.add(text.string());
                            }
                            ((ElasticBaseEntity)t).getHighlights().put(key, fragments);
                        });
                    }
                    result.add(t);
                }
                if (pagination == null) {
                    SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
                    scrollRequest.scroll(scroll);
                    response = client.searchScroll(scrollRequest);
                } else {
                    response = null;
                }
            }
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Search successfully, total: %d.", result.size()));
            }
            if (pagination == null) {
                // 清理滚动操作
                ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
                clearScrollRequest.addScrollId(scrollId);
                ClearScrollResponse clearScrollResponse = client.clearScroll(clearScrollRequest);
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Clear the scroll search %s.",
                            clearScrollResponse.isSucceeded() ? "successfully" : "fail"));
                }
            }
            return result;
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Search fail from elastic.", ex);
            }
            throw new UserInterfaceDalErrorException(UserInterfaceDalErrorException.DalErrors.ES_REST_FAIL);
        }
    }

    /**
     * {@inheritDoc} <br>
     *
     * @see ElasticUtil#search(GeneralAccessor.ConditionGroup, GeneralAccessor.RecordOrderGroup, List, Pagination)
     */
    @Override
    public <T extends Base> List<T> search(GeneralAccessor.ConditionGroup group,
                                           GeneralAccessor.RecordOrderGroup orderGroup,
                                           List<Class<? extends Base>> classes,
                                           Pagination pagination) {
        SearchRequest request = createSearchRequest(group, orderGroup, classes, pagination);
        return search(request, pagination);
    }

    /**
     * {@inheritDoc}
     * @see ElasticUtil#count(GeneralAccessor.ConditionGroup, List)
     */
    @Override
    public <T extends Base> long count(GeneralAccessor.ConditionGroup group, List<Class<? extends Base>> classes) {
        SearchRequest request = createSearchRequest(group, null, classes, null);
        try {
            SearchResponse response = client.search(request);
            if (response != null && response.getHits() != null) {
                return response.getHits().getTotalHits();
            } else {
                return 0;
            }
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Search fail from elastic.", ex);
            }
            throw new UserInterfaceDalErrorException(UserInterfaceDalErrorException.DalErrors.ES_REST_FAIL);
        }
    }

    /**
     * 执行一次空间查询
     *
     * @param builder    查询源构建器
     * @param pagination 分页对象
     * @param indices    索引列表
     * @param <T>        泛型定义
     * @return 符合条件的实体列表
     */
    private <T extends ElasticGeoPointBaseEntity> List<T> geoQuery(SearchSourceBuilder builder, Pagination pagination,
                                                                   String... indices) {
        SearchRequest request = new SearchRequest(indices);
        request.source(builder);
        return search(request, pagination);
    }

    /**
     * {@inheritDoc}
     *
     * @see ElasticUtil#geoNearBy(GeoPointLocation, double, GeneralAccessor.ConditionGroup, List)
     */
    @Override
    public <T extends ElasticGeoPointBaseEntity> List<T> geoNearBy(GeoPointLocation centerPoint, double distanceMeters,
                                                                   GeneralAccessor.ConditionGroup group,
                                                                   List<Class<? extends Base>> classes) {
        return geoNearBy(centerPoint, distanceMeters, group, classes, null);
    }

    /**
     * {@inheritDoc}
     *
     * @see ElasticUtil#geoNearBy(GeoPointLocation, double, GeneralAccessor.ConditionGroup, List, Pagination)
     */
    @Override
    public <T extends ElasticGeoPointBaseEntity> List<T> geoNearBy(GeoPointLocation centerPoint, double distanceMeters,
                                                                   GeneralAccessor.ConditionGroup group,
                                                                   List<Class<? extends Base>> classes,
                                                                   Pagination pagination) {
        if (centerPoint == null) {
            if (logger.isErrorEnabled()) {
                logger.error("The center point is null.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        SearchSourceBuilder builder = new SearchSourceBuilder();
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        QueryBuilder boolBuilder = QueryBuilders.boolQuery();
        Set<String> highlightFields = new HashSet<>();
        if (group != null) {
            QueryBuilder groupBuilder = createQueryGroupBuilder(group, classes.get(0), highlightFields);
            ((BoolQueryBuilder) boolBuilder).must(groupBuilder);
        }
        QueryBuilder geoBuilder = QueryBuilders.geoDistanceQuery("location")
                .point(centerPoint.get())
                .distance(distanceMeters, DistanceUnit.METERS)
                .geoDistance(GeoDistance.ARC);
        ((BoolQueryBuilder) boolBuilder).must(geoBuilder);
        builder.query(boolBuilder);
        highlightFields.forEach(highlightBuilder::field);
        builder.highlighter(highlightBuilder);
        if (pagination != null) {
            builder.from((pagination.getPage() - 1) * pagination.getSize());
            builder.size(pagination.getSize());
        }
        builder.sort(new GeoDistanceSortBuilder("location", centerPoint.getLat(), centerPoint.getLon())
                .unit(DistanceUnit.METERS)
                .order(SortOrder.ASC)
                .geoDistance(GeoDistance.ARC));
        return geoQuery(builder, pagination, getIndices(classes));
    }

    /**
     * {@inheritDoc}
     *
     * @see ElasticUtil#geoWithInPolygon(GeoPointLocation, List, GeneralAccessor.ConditionGroup, List)
     */
    @Override
    public <T extends ElasticGeoPointBaseEntity> List<T> geoWithInPolygon(GeoPointLocation centerPoint,
                                                                          List<GeoPointLocation> polygon,
                                                                          GeneralAccessor.ConditionGroup group,
                                                                          List<Class<? extends Base>> classes) {
        return geoWithInPolygon(centerPoint, polygon, group, classes, null);
    }

    /**
     * {@inheritDoc}
     *
     * @see ElasticUtil#geoWithInPolygon(GeoPointLocation, List, GeneralAccessor.ConditionGroup, List, Pagination)
     */
    @Override
    public <T extends ElasticGeoPointBaseEntity> List<T> geoWithInPolygon(GeoPointLocation centerPoint,
                                                                          List<GeoPointLocation> polygon,
                                                                          GeneralAccessor.ConditionGroup group,
                                                                          List<Class<? extends Base>> classes,
                                                                          Pagination pagination) {
        if (polygon == null || polygon.size() < 3) {
            if (logger.isErrorEnabled()) {
                logger.error("The polygon's point is less than 3.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        SearchSourceBuilder builder = new SearchSourceBuilder();
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        List<GeoPoint> points = new ArrayList<>(polygon.size());
        polygon.forEach(point -> points.add(point.get()));
        QueryBuilder boolBuilder = QueryBuilders.boolQuery();
        Set<String> highlightFields = new HashSet<>();
        if (group != null) {
            QueryBuilder groupBuilder = createQueryGroupBuilder(group, classes.get(0), highlightFields);
            ((BoolQueryBuilder) boolBuilder).must(groupBuilder);
        }
        QueryBuilder geoBuilder = QueryBuilders.geoPolygonQuery("location", points);
        ((BoolQueryBuilder) boolBuilder).must(geoBuilder);
        builder.query(boolBuilder);
        highlightFields.forEach(highlightBuilder::field);
        builder.highlighter(highlightBuilder);
        if (pagination != null) {
            builder.from((pagination.getPage() - 1) * pagination.getSize());
            builder.size(pagination.getSize());
        }
        if (centerPoint != null) {
            builder.sort(new GeoDistanceSortBuilder("location", centerPoint.getLat(), centerPoint.getLon())
                    .unit(DistanceUnit.METERS)
                    .order(SortOrder.ASC)
                    .geoDistance(GeoDistance.ARC));
        }
        return geoQuery(builder, pagination, getIndices(classes));
    }

    /**
     * {@inheritDoc}
     *
     * @see ElasticLowLevelUtil#getById(String, String)
     */
    @Override
    public JSONObject getById(String id, String index) {
        GetRequest request = new GetRequest(index, index, id);
        try {
            GetResponse response = client.get(request);
            return response.isExists() ? JSON.parseObject(response.getSourceAsString()) : null;
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
     * 根据指定的Elastic Entity对象创建一个创建索引的请求
     * @param t Elastic实体对象
     * @param <T> 泛型定义
     * @return 转换后的DocWriteRequest
     */
    @SuppressWarnings("unchecked")
    private <T extends Base> DocWriteRequest createIndexRequest(T t) {
        Class<T> clazz = (Class<T>) t.getClass();
        String index = getIndex(clazz);
        IndexRequest request = new IndexRequest(index, index, t.getId());
        request.source(JSON.toJSONString(t), XContentType.JSON);
        return request;
    }

    /**
     * 根据指定的Elastic Entity对象创建一个更新索引的请求
     * @param t Elastic实体对象
     * @param <T> 泛型定义
     * @return 转换后的DocWriteRequest
     */
    @SuppressWarnings("unchecked")
    private <T extends Base> DocWriteRequest createUpdateRequest(T t) {
        Class<T> clazz = (Class<T>) t.getClass();
        String index = getIndex(clazz);
        UpdateRequest request = new UpdateRequest(index, index, t.getId());
        request.doc(JSON.toJSONString(t), XContentType.JSON);
        return request;
    }

    /**
     * 根据指定的Elastic Entity对象创建一个删除索引的请求
     * @param t Elastic实体对象
     * @param <T> 泛型定义
     * @return 转换后的DocWriteRequest
     */
    @SuppressWarnings("unchecked")
    private <T extends Base> DocWriteRequest createDeleteRequest(T t) {
        Class<T> clazz = (Class<T>) t.getClass();
        String index = getIndex(clazz);
        return new DeleteRequest(index, index, t.getId());
    }

    /**
     * {@inheritDoc}
     *
     * @see ElasticUtil#index(Base, boolean)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends Base> T index(T t, boolean isNew) {
        if (t == null) {
            if (logger.isWarnEnabled()) {
                logger.warn("The entity is null.");
            }
            return null;
        }
        try {
            DocWriteRequest request = isNew ? createIndexRequest(t) : createUpdateRequest(t);
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
     * @see ElasticUtil#index(List, List)
     */
    @Override
    public <T extends Base> List<T> index(List<T> ts, List<Boolean> isNews) {
        if (ts == null || ts.isEmpty()) {
            if (logger.isWarnEnabled()) {
                logger.warn("The entity's list is null or empty.");
            }
            return ts;
        }
        BulkRequest bulkRequest = new BulkRequest();
        for (int index = 0; index < ts.size(); index++) {
            T t = ts.get(index);
            boolean isNew = isNews.get(index);
            DocWriteRequest request = isNew ? createIndexRequest(t) : createUpdateRequest(t);
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
}
