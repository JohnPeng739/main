package org.mx.tools.ffee;

import org.mx.tools.ffee.rest.config.FfeeConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * TODO 请输入类描述
 *
 * @author : John.Peng on 2018/2/18 下午4:50.
 */
public class FfeeApplication {
    public static void main(String[] args) {
        new AnnotationConfigApplicationContext(FfeeConfig.class);
    }
}
