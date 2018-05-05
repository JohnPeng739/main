package org.mx.dal.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

/**
 * 描述： 基于Elasticsearch的DAL基础实现
 *
 * @author John.Peng
 * Date time 2018/4/1 上午8:40
 */
@Import(DalConfig.class)
@PropertySource({
        "classpath:elastic.properties"
})
@ComponentScan({
        "org.mx.dal.utils",
        "org.mx.dal.service.impl"
})
public class DalElasticConfig {
}
