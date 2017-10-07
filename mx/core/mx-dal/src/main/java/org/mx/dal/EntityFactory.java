package org.mx.dal;

import org.mx.dal.exception.EntityInstantiationException;
import org.springframework.util.Assert;

/**
 * Created by john on 2017/8/18.
 */
public class EntityFactory {
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
