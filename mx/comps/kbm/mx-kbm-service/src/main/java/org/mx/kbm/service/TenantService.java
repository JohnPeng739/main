package org.mx.kbm.service;

import org.mx.kbm.entity.KnowledgeTenant;
import org.mx.kbm.service.bean.TenantRegisterRequest;

/**
 * 描述： 知识租户管理服务接口定义
 *
 * @author John.Peng
 *         Date time 2018/4/29 下午8:47
 */
public interface TenantService {
    /**
     * 根据申请信息注册一个知识租户
     *
     * @param registerRequest 知识租户请求信息对象
     * @return 注册成功的知识租户对象，失败抛出异常UserInterface异常
     */
    KnowledgeTenant register(TenantRegisterRequest registerRequest);
}
