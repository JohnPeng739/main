package org.mx.dal.config;

import org.mx.StringUtils;
import org.springframework.beans.factory.annotation.Value;

/**
 * 描述： JPA配置对象
 *
 * @author john peng
 * Date time 2018/7/22 下午12:39
 */
public class JpaConfigBean {
    @Value("${jpa.database:H2}")
    private String jpaDatabase;
    @Value("${jpa.databasePlatform:org.hibernate.dialect.H2Dialect}")
    private String jpaDatabasePlatform;
    @Value("${jpa.entity.packages:}")
    private String jpaEntityPackagesString;
    @Value("${jpa.generateDDL:false}")
    private boolean generateDDL;
    @Value("${jpa.showSQL:false}")
    private boolean showSQL;

    /**
     * 获取JPA数据库枚举
     *
     * @return JPA数据库枚举
     */
    public String getJpaDatabase() {
        return jpaDatabase;
    }

    /**
     * 获取JPA数据库平台方言
     *
     * @return 数据库平台方言
     */
    public String getJpaDatabasePlatform() {
        return jpaDatabasePlatform;
    }

    /**
     * 获取需要扫描的JPA实体包路径
     *
     * @return JPA实体包路径数组
     */
    public String[] getJpaEntityPackages() {
        return StringUtils.split(jpaEntityPackagesString);
    }

    /**
     * 获取是否创建DDL
     *
     * @return 返回true表示创建，否则不创建
     */
    public boolean isGenerateDDL() {
        return generateDDL;
    }

    /**
     * 获取是否显示SQL
     *
     * @return 返回true表示显示，否则不显示。
     */
    public boolean isShowSQL() {
        return showSQL;
    }
}
