package org.mx.dal.annotation;

import java.lang.annotation.*;

/**
 * 描述： 定义指定的Entity是一个Elastic索引的注解
 *
 * @author John.Peng
 * Date time 2018/4/5 上午7:38
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ElasticIndex {
    /**
     * 指定的索引名字，如果没有指定，则使用该实体类的名字<pre>Class.getName()</pre>。
     *
     * @return 索引名称
     */
    String value() default "";
}
