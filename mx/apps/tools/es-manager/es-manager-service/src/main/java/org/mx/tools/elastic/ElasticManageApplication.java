package org.mx.tools.elastic;

import org.mx.tools.elastic.config.ElasticManagerConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ElasticManageApplication {
    private static AnnotationConfigApplicationContext context;

    public static void main(String[] args) {
        context = new AnnotationConfigApplicationContext(ElasticManagerConfig.class);
    }

    public static void stopServer() {
        if (context != null) {
            context.close();
        }
    }
}
