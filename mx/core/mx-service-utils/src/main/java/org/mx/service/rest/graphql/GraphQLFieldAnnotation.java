package org.mx.service.rest.graphql;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 描述： 注解GraphQL操作字段的注解，只能注解到方法上。
 *
 * @author john peng
 * Date time 2018/11/6 9:11 PM
 */
@Target({ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface GraphQLFieldAnnotation {
    String value();
}
