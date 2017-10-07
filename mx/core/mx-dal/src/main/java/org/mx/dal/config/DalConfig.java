package org.mx.dal.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by john on 2017/10/7.
 */
@Configuration
@ComponentScan({"org.mx.dal.session.impl"})
public class DalConfig {
    public DalConfig() {
        super();
    }
}
