package org.mx.dal.service;

import org.mx.DigestUtils;
import org.mx.StringUtils;
import org.mx.dal.entity.Base;
import org.mx.dal.entity.BaseDict;
import org.mx.dal.entity.BaseDictTree;
import org.mx.spring.session.SessionDataStore;

import java.util.Date;

/**
 * 描述： 跨数据库平台的通用数据操作方法
 *
 * @author john peng
 * Date time 2019/3/22 5:22 PM
 */
public abstract class AbstractGeneralAccessor implements GeneralAccessor {
    protected SessionDataStore sessionDataStore;

    /**
     * 判断指定的实体是否已经存在
     *
     * @param t   实体对象
     * @param <T> 实体对象泛型定义
     * @return 如果存在，则返回对应的实体对象，否则返回null。
     */
    @SuppressWarnings("unchecked")
    protected <T extends Base> T checkExist(T t) {
        Class<T> clazz = (Class<T>) t.getClass();
        if (!StringUtils.isBlank(t.getId())) {
            T check = getById(t.getId(), clazz);
            if (check != null) {
                return check;
            }
        }
        if (t instanceof BaseDict) {
            String code = ((BaseDict) t).getCode();
            if (!StringUtils.isBlank(code)) {
                T check = findOne(ConditionTuple.eq("code", code), clazz);
                if (check != null) {
                    return check;
                }
            }
        }
        return null;
    }

    /**
     * 实体保存前相关处理，包括：创建、修改时间，ID，父子关系等。
     * @param t 实体对象
     * @param <T> 实体对象范型定义
     * @return 如果存在，则返回对应的实体对象，否则返回null。
     */
    @SuppressWarnings("unchecked")
    protected <T extends Base> T prepareSave(T t) {
        if (t.getUpdatedTime() <= 0) {
            // 如果外部传入过修改时间，则以外部传入为准
            t.setUpdatedTime(new Date().getTime());
        }
        if (StringUtils.isBlank(t.getOperator()) || "NA".equalsIgnoreCase(t.getOperator())) {
            t.setOperator(sessionDataStore.getCurrentUserCode());
        }
        Class<T> clazz = (Class<T>) t.getClass();
        T old = this.checkExist(t);
        if (t instanceof BaseDictTree) {
            BaseDictTree parent = ((BaseDictTree) t).getParent();
            if (!StringUtils.isBlank(((BaseDictTree) t).getParentId())) {
                T p = getById(((BaseDictTree) t).getParentId(), clazz);
                ((BaseDictTree) t).setParent((BaseDictTree) p);
            }
        }
        if (old == null) {
            // 新增操作
            if (StringUtils.isBlank(t.getId())) {
                // 如果ID为空，则自动生成UUID，否则使用外部传入的ID
                t.setId(DigestUtils.uuid());
            }
            if (t.getCreatedTime() <= 0) {
                t.setCreatedTime(new Date().getTime());
            }
        } else {
            // 修改操作
            t.setId(old.getId());
            t.setCreatedTime(old.getCreatedTime());
            if (t instanceof BaseDict) {
                // 代码字段一旦保存，则不允许被修改
                ((BaseDict) t).setCode(((BaseDict) old).getCode());
            }
        }
        return old;
    }
}
