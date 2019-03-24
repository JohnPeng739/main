package org.mx.dal.service;

import org.mx.StringUtils;
import org.mx.dal.entity.Base;
import org.mx.dal.entity.BaseDict;

/**
 * 描述： 跨数据库平台的通用数据操作方法
 *
 * @author john peng
 * Date time 2019/3/22 5:22 PM
 */
public abstract class AbstractGeneralAccessor implements GeneralAccessor {
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
}
