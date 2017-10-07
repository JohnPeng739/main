package org.mx.dal.service.impl;

import org.mx.entity.BaseDict;
import org.mx.exception.EntityAccessException;
import org.mx.service.GeneralDictEntityAccessor;

/**
 * Created by john on 2017/10/6.
 */
public class GeneralDictEntityAccessorImpl extends GeneralEntityAccessorImpl implements GeneralDictEntityAccessor {
    @Override
    public <T extends BaseDict> T getByCode(String code, Class<T> entityInterfaceClass) throws EntityAccessException {
        return null;
    }
}
