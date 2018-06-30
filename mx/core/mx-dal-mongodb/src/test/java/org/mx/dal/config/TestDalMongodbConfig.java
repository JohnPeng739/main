package org.mx.dal.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

/**
 * 描述：
 *
 * @author : john date : 2018/6/30 下午3:52
 */
@Configuration
@PropertySource({
        "classpath:mongodb.properties"
})
@Import({DalMongodbConfig.class})
public class TestDalMongodbConfig {
}
