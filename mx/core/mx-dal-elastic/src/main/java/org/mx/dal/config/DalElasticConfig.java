package org.mx.dal.config;

import org.mx.dal.service.GeneralAccessor;
import org.mx.dal.service.GeneralDictAccessor;
import org.mx.dal.service.impl.GeneralAccessorElasticImpl;
import org.mx.dal.service.impl.GeneralDictAccessorElasticImpl;
import org.mx.dal.utils.ElasticConfigBean;
import org.mx.dal.utils.ElasticUtil;
import org.mx.dal.utils.ElasticUtilRest;
import org.mx.spring.session.SessionDataStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

/**
 * 描述： 基于Elasticsearch的DAL基础实现
 *
 * @author John.Peng
 * Date time 2018/4/1 上午8:40
 */
@Import(DalConfig.class)
public class DalElasticConfig {

    @Bean(name = "elasticConfigBean")
    public ElasticConfigBean elasticConfigBean(Environment env) {
        return new ElasticConfigBean(env);
    }

    @Bean(name = "elasticUtilRest", initMethod = "init", destroyMethod = "destroy")
    public ElasticUtilRest elasticUtilRest(ElasticConfigBean elasticConfigBean) {
        return new ElasticUtilRest(elasticConfigBean);
    }

    @Bean(name = "generalAccessorElastic")
    public GeneralAccessor generalAccessorElastic(SessionDataStore sessionDataStore, ElasticUtil elasticUtil) {
        return new GeneralAccessorElasticImpl(sessionDataStore, elasticUtil);
    }

    @Bean(name = "generalDictAccessorElastic")
    public GeneralDictAccessor generalDictAccessorElastic(SessionDataStore sessionDataStore, ElasticUtil elasticUtil) {
        return new GeneralDictAccessorElasticImpl(sessionDataStore, elasticUtil);
    }
}
