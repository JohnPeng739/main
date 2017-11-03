package com.ds.retl.dal.entity;

import org.mx.dal.entity.BaseDictEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 * Created by john on 2017/11/2.
 */
@Entity
@Table(name = "TB_CONFIG_JSON")
public class ConfigJsonEntity extends BaseDictEntity implements ConfigJson {
    @Column(name = "CONTENT", nullable = false)
    @Lob
    private String configContent;

    @Override
    public String getConfigContent() {
        return configContent;
    }

    @Override
    public void setConfigContent(String configContent) {
        this.configContent = configContent;
    }
}
