package org.mx.comps.jwt;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注明进行身份认证的注解定义
 *
 * @author : john.peng created on date : 2018/1/6
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AuthenticateAround {
    /**
     * 身份认证中记录JWT的字段名
     *
     * @return JWT字段名
     */
    String value() default "authenticate";

    /**
     * 拦截方法的返回值类型
     *
     * @return 返回值类型
     */
    Class<?> returnValueClass();
}
