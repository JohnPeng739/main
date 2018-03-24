package org.mx.kbm.service;

import org.mx.kbm.entity.Tenant;

/**
 * 描述： 租户相关服务接口定义。
 *
 * @author John.Peng
 *         Date time 2018/3/24 下午9:32
 */
public interface TenantService {
    Tenant getTenant(String code);
    Tenant saveTenant(Tenant tenant);
}
