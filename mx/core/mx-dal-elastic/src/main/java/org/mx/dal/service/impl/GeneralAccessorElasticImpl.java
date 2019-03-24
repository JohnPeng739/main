package org.mx.dal.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.DigestUtils;
import org.mx.StringUtils;
import org.mx.dal.Pagination;
import org.mx.dal.entity.Base;
import org.mx.dal.entity.BaseDict;
import org.mx.dal.entity.ElasticBaseEntity;
import org.mx.dal.error.UserInterfaceDalErrorException;
import org.mx.dal.service.AbstractGeneralAccessor;
import org.mx.dal.service.ElasticAccessor;
import org.mx.dal.service.GeneralAccessor;
import org.mx.dal.utils.ElasticUtil;
import org.mx.spring.session.SessionDataStore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 描述： 基于Elastic实现的数据基础操作（CRUD）
 *
 * @author John.Peng
 * Date time 2018/4/1 上午8:56
 */
public class GeneralAccessorElasticImpl extends AbstractGeneralAccessor implements GeneralAccessor, ElasticAccessor {
    private static final Log logger = LogFactory.getLog(GeneralAccessorElasticImpl.class);

    private SessionDataStore sessionDataStore;
    private ElasticUtil elasticUtil;

    /**
     * 默认的构造函数
     *
     * @param sessionDataStore 会话上下文数据存储
     * @param elasticUtil      ES工具
     */
    public GeneralAccessorElasticImpl(SessionDataStore sessionDataStore, ElasticUtil elasticUtil) {
        super();
        this.sessionDataStore = sessionDataStore;
        this.elasticUtil = elasticUtil;
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#count(Class)
     */
    @Override
    public <T extends Base> long count(Class<T> clazz) {
        return count(clazz, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#count(Class, boolean)
     */
    @Override
    public <T extends Base> long count(Class<T> clazz, boolean isValid) {
        return count(isValid ? ConditionTuple.eq("valid", true) : null, clazz);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#count(ConditionGroup, Class)
     */
    @Override
    public <T extends Base> long count(ConditionGroup group, Class<T> clazz) {
        return elasticUtil.count(group, Collections.singletonList(clazz));
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#list(Class)
     */
    @Override
    public <T extends Base> List<T> list(Class<T> clazz) {
        return list(clazz, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#list(Pagination, Class, boolean)
     */
    @Override
    public <T extends Base> List<T> list(Class<T> clazz, boolean isValid) {
        return list(null, clazz, isValid);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#list(Pagination, Class, boolean)
     */
    @Override
    public <T extends Base> List<T> list(Pagination pagination, Class<T> clazz) {
        return list(pagination, clazz, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#find(Pagination, ConditionGroup, RecordOrderGroup, Class)
     */
    @Override
    public <T extends Base> List<T> list(Pagination pagination, Class<T> clazz, boolean isValid) {
        return find(pagination,
                isValid ? ConditionGroup.and(ConditionTuple.eq("valid", true)) : null,
                null, clazz);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#getById(String, Class)
     */
    @Override
    public <T extends Base> T getById(String id, Class<T> clazz) {
        return elasticUtil.getById(id, clazz);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#find(org.mx.dal.service.GeneralAccessor.ConditionGroup, Class)
     */
    @Override
    public <T extends Base> List<T> find(ConditionGroup group, Class<T> clazz) {
        return find(null, group, null, Collections.singletonList(clazz));
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#find(Pagination, ConditionGroup, RecordOrderGroup, Class)
     */
    @Override
    public <T extends Base> List<T> find(Pagination pagination, ConditionGroup group, RecordOrderGroup orderGroup,
                                         Class<T> clazz) {
        return find(pagination, group, orderGroup, Collections.singletonList(clazz));
    }

    /**
     * 根据条件组进行分页查询
     *
     * @param pagination 分页对象
     * @param group      条件组
     * @param orderGroup 排序组
     * @param classes    实体类定义列表
     * @param <T>        实体泛型
     * @return 符合条件的实体列表
     */
    public <T extends Base> List<T> find(Pagination pagination, ConditionGroup group, RecordOrderGroup orderGroup,
                                         List<Class<? extends Base>> classes) {
        return elasticUtil.search(group, orderGroup, classes, pagination);
    }

    /**
     * {@inheritDoc} <br>
     * 如果预计记录数超过1000，则推荐使用带分页条件的查询 {@link ElasticUtil#search(org.mx.dal.service.GeneralAccessor.ConditionGroup, Class, Pagination)}。否则只返回前1000条记录。
     *
     * @see ElasticAccessor#find(GeneralAccessor.ConditionGroup, List)
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends Base> List<T> find(ConditionGroup group, List<Class<? extends Base>> classes) {
        return find(null, group, null, classes);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#findOne(ConditionGroup, Class)
     */
    @Override
    public <T extends Base> T findOne(ConditionGroup group, Class<T> clazz) {
        List<T> result = find(group, clazz);
        if (result != null && result.size() > 0) {
            return result.get(0);
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends Base> T prepareSave(T t) {
        if (t.getUpdatedTime() <= 0) {
            // 如果外部传入过修改时间，则以外部传入为准
            t.setUpdatedTime(new Date().getTime());
        }
        if (StringUtils.isBlank(t.getOperator()) || "NA".equalsIgnoreCase(t.getOperator())) {
            t.setOperator(sessionDataStore.getCurrentUserCode());
        }
        Class<T> clazz = (Class<T>) t.getClass();
        return super.checkExist(t);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#save(Base)
     */
    @Override
    public <T extends Base> T save(T t) {
        T old = prepareSave(t);
        if (old == null) {
            // 新增操作
            if (StringUtils.isBlank(t.getId())) {
                // 如果ID为空，则自动生成UUID，否则使用外部传入的ID
                t.setId(DigestUtils.uuid());
            }
            if (t.getCreatedTime() <= 0) {
                t.setCreatedTime(new Date().getTime());
            }
            return elasticUtil.index(t, true);
        } else {
            // 修改操作
            t.setCreatedTime(old.getCreatedTime());
            if (t instanceof BaseDict) {
                // 代码字段一旦保存，则不允许被修改
                ((BaseDict) t).setCode(((BaseDict) old).getCode());
            }
            return elasticUtil.index(t, false);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#save(List)
     */
    @Override
    public <T extends Base> List<T> save(List<T> ts) {
        List<Boolean> isNews = new ArrayList<>(ts.size());
        for (T t : ts) {
            T old = prepareSave(t);
            isNews.add(old == null);
        }
        return elasticUtil.index(ts, isNews);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#clear(Class)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends Base> void clear(Class<T> clazz) {
        if (ElasticBaseEntity.class.isAssignableFrom(clazz)) {
            elasticUtil.deleteIndex(clazz);
        } else {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("The class[%s] not extends from ElasticBaseEntity.", clazz.getName()));
            }
            throw new UserInterfaceDalErrorException(UserInterfaceDalErrorException.DalErrors.ENTITY_INDEX_INVALID_BASE);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#remove(String, Class)
     */
    @Override
    public <T extends Base> T remove(String id, Class<T> clazz) {
        return remove(id, clazz, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#remove(String, Class, boolean)
     */
    @Override
    public <T extends Base> T remove(String id, Class<T> clazz, boolean logicRemove) {
        T t = getById(id, clazz);
        if (t == null) {
            throw new UserInterfaceDalErrorException(UserInterfaceDalErrorException.DalErrors.ENTITY_NOT_FOUND);
        }
        return remove(t, logicRemove);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#remove(Base)
     */
    @Override
    public <T extends Base> T remove(T t) {
        return remove(t, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralAccessor#remove(Base, boolean)
     */
    @Override
    public <T extends Base> T remove(T t, boolean logicRemove) {
        return elasticUtil.remove(t, logicRemove);
    }
}
