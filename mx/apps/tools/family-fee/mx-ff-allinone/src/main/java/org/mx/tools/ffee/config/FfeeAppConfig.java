package org.mx.tools.ffee.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

/**
 * 描述：
 *
 * @author : john date : 2018/9/15 下午9:09
 */
@Configuration
@PropertySource({
        "classpath:server.properties",
        "classpath:database.properties",
        "classpath:jpa.properties",
        "classpath:ffee.properties"
})
@Import({
        FfeeConfig.class
})
public class FfeeAppConfig {
}
