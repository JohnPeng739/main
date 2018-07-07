package org.mx.dal.annotation;

import java.lang.annotation.*;

/**
 * 描述： 定义Entity中属性进行Elastic索引的注解
 *
 * @author John.Peng
 * Date time 2018/4/5 上午7:41
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ElasticField {
    /**
     * 指定的属性名字，如果没有指定，则使用实体类中该属性的名字<pre>field.getName()</pre>。
     *
     * @return 属性名称
     */
    String value() default "";

    /**
     * 属性字段的索引类型，默认为"keyword"类型，进行精确匹配。<br>
     * 如果需要全文检索，必须配置成"text"！<br>
     * 类型一般包括：<br>
     * <ol>
     * <li>简单类型：text, keyword, date, long, double, boolean, ip</li>
     * <li>对象类型：object, nested</li>
     * <li>特殊类型：geo_point, geo_shape, completion</li>
     * </ol>
     *
     * @return 索引类型
     */
    String type() default "keyword";

    /**
     * 属性字段使用的分词器，如果不指定，则使用系统默认的分词器。
     *
     * @return 分词器
     */
    String analyzer() default "";
}
