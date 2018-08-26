package org.mx.service.rest.auth;

import javax.ws.rs.NameBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 描述： RESTful服务需要进行身份鉴别的注解定义
 *
 * @author john peng
 * Date time 2018/8/26 下午8:07
 */
@NameBinding
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface RestAuthenticate {
}
