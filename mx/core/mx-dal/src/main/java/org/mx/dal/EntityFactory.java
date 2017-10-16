package org.mx.dal;

import org.mx.dal.exception.EntityInstantiationException;
import org.springframework.util.Assert;

/**
 * 实体创建工厂类
 *
 * @author : john.peng date : 2017/8/18
 */
public class EntityFactory {
    /**
     * 根据指定的实体接口类定义创建一个实体对象
     *
     * @param entityInterfaceClass 实体接口类定义
     * @param <T>                  泛型类型
     * @return 创建后的实体对象
     * @throws EntityInstantiationException 创建实体过程中发生的异常
     */
    public static <T> T createEntity(Class<T> entityInterfaceClass) throws EntityInstantiationException {
        Assert.notNull(entityInterfaceClass, "The class of the entity interface is null.");
        String entityName = String.format("%sEntity", entityInterfaceClass.getName());
        try {
            Class<T> entityClass = (Class<T>) Class.forName(entityName);
            return entityClass.newInstance();
        } catch (ClassNotFoundException ex) {
            throw new EntityInstantiationException(String.format("The class[%s] not found.", entityName), ex);
        } catch (IllegalAccessException | InstantiationException ex) {
            throw new EntityInstantiationException(String.format("Instance the class[%s] fail.", entityName), ex);
        }
    }
}
