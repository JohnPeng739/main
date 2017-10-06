package com.ds.retl;

import com.ds.retl.rest.config.RETLRestConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by john on 2017/10/6.
 */
public class RETLRestApplication {
    public static void main(String[] args) {
        new AnnotationConfigApplicationContext(RETLRestConfig.class);
    }
}
