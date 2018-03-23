package org.mx.dal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.dal.entity.Base;
import org.mx.dal.error.UserInterfaceDalErrorException;
import org.springframework.util.Assert;

/**
 * 实体创建工厂类
 *
 * @author : john.peng date : 2017/8/18
 */
public class EntityFactory {
    private static final Log logger = LogFactory.getLog(EntityFactory.class);

    /**
     * 根据指定的实体接口定义返回对应的实体定义类
     *
     * @param clazz 实体接口类
     * @param <T>   泛型类型
     * @return 实体类
     * @throws ClassNotFoundException 实体类型没有定义
     */
    public static <T extends Base> Class<T> getEntityClass(Class<T> clazz) throws ClassNotFoundException {
        String entityClassName = String.format("%sEntity", clazz.getName());
        Class<?> entityClass = Class.forName(entityClassName);
        boolean found = false;
        String baseName = Base.class.getName();
        Class<?> tmpClass = entityClass;
        do {
            Class<?>[] is = tmpClass.getInterfaces();
            if (is != null && is.length > 0) {
                for (Class<?> c : is) {
                    if (baseName.equals(c.getName())) {
                        found = true;
                        break;
                    }
                }
            }
            tmpClass = tmpClass.getSuperclass();
        } while(tmpClass != null && !found);
        if (!found) {
            throw new UserInterfaceDalErrorException(UserInterfaceDalErrorException.DalErrors.ENTITY_CLASS_INVALID);
        }
        return (Class<T>) entityClass;
    }

    /**
     * 根据指定的实体接口类定义创建一个实体对象
     *
     * @param clazz 实体接口类定义
     * @param <T>                  泛型类型
     * @return 创建后的实体对象
     * @throws UserInterfaceDalErrorException 创建实体过程中发生的异常
     */
    public static <T extends Base> T createEntity(Class<T> clazz) throws UserInterfaceDalErrorException {
        Assert.notNull(clazz, "The class of the entity interface is null.");
        try {
            if (clazz.isInterface()) {
                clazz = EntityFactory.getEntityClass(clazz);
            }
            return clazz.newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Create entity[%s] fail.", clazz.getName()), ex);
            }
            throw new UserInterfaceDalErrorException(UserInterfaceDalErrorException.DalErrors.ENTITY_INSTANCE_FAIL);
        }
    }
}
