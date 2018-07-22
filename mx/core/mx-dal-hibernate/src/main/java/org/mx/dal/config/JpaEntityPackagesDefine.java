package org.mx.dal.config;

import org.mx.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * JPA的实体包定义类，用于JPA EntityManager进行实体扫描。
 *
 * @author : john.peng created on date : 2017/12/03
 * @deprecated 建议直接使用jpa.properties中的jpa.entity.packages配置项替代，未来可能被删除。
 */
public class JpaEntityPackagesDefine {
    private Set<String> packages;

    public JpaEntityPackagesDefine() {
        super();
        this.packages = new HashSet<>();
    }

    public JpaEntityPackagesDefine(String... packages) {
        this();
        for (String p : packages) {
            if (!StringUtils.isBlank(p)) {
                this.packages.add(p);
            }
        }
    }

    public Set<String> getPackages() {
        return packages;
    }
}
