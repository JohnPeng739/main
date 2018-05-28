package org.mx.hanlp.config;

import org.mx.hanlp.TextExtracter;
import org.mx.hanlp.factory.SuggesterFactory;
import org.mx.hanlp.impl.TextExtracterImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

/**
 * 描述： 推荐服务Java Config类
 *
 * @author John.Peng
 * Date time 2018/4/20 上午8:44
 */
@PropertySource({
        "classpath:suggester.properties"
})
public class SuggesterConfig {
    @Bean(name = "textExtracter")
    public TextExtracter textExtracter() {
        return new TextExtracterImpl();
    }

    @Bean(name = "suggesterFactory", initMethod = "init", destroyMethod = "destroy")
    public SuggesterFactory suggesterFactory(Environment env) {
        return new SuggesterFactory(env);
    }
}
