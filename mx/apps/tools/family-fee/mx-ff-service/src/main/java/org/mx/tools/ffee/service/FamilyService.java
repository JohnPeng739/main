package org.mx.tools.ffee.service;

import org.mx.tools.ffee.dal.entity.Family;

/**
 * TODO 请输入类描述
 *
 * @author : John.Peng on 2018/2/18 上午11:49.
 */
public interface FamilyService {
    Family createFamily(String name);

    Family joinFamily(String name);
}
