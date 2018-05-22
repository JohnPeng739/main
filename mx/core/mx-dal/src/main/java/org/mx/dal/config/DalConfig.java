package org.mx.dal.config;

import org.mx.dal.session.SessionDataStore;
import org.mx.dal.session.impl.SessionDataThreadLocal;
import org.springframework.context.annotation.Bean;

/**
 * DAL（数据访问层）Java Configure类
 * <p>
 * 载入：org.mx.dal.session.impl中的组件
 *
 * @author : john.peng date : 2017/10/7
 */
public class DalConfig {
    /**
     * 基于线程局部堆方式创建SessionDataStore
     *
     * @return SessionDataStore对象
     */
    @Bean(name = "sessionDataStore")
    public SessionDataStore sessionDataThreadLocal() {
        return new SessionDataThreadLocal();
    }
}
