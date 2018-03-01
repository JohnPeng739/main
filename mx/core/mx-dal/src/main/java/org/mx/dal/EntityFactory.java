package org.mx.dal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
     * 根据指定的实体接口类定义创建一个实体对象
     *
     * @param entityInterfaceClass 实体接口类定义
     * @param <T>                  泛型类型
     * @return 创建后的实体对象
     * @throws UserInterfaceDalErrorException 创建实体过程中发生的异常
     */
    public static <T> T createEntity(Class<T> entityInterfaceClass) throws UserInterfaceDalErrorException {
        Assert.notNull(entityInterfaceClass, "The class of the entity interface is null.");
        String entityName = entityInterfaceClass.getName();
        if (entityInterfaceClass.isInterface()) {
            entityName = String.format("%sEntity", entityName);
        }
        try {
            Class<T> entityClass = (Class<T>) Class.forName(entityName);
            return entityClass.newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            throw new UserInterfaceDalErrorException(UserInterfaceDalErrorException.DalErrors.ENTITY_INSTANCE_FAIL);
        }
    }
}
