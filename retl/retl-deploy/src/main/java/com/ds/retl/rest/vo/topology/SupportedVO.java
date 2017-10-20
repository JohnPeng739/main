package com.ds.retl.rest.vo.topology;

import com.ds.retl.jms.JmsManager;
import com.ds.retl.rest.vo.LabelValueVO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by john on 2017/10/11.
 */
public class SupportedVO {
    private List<LabelValueVO> topologyTypes;
    private List<LabelValueVO> spoutTypes;
    private List<LabelValueVO> boltTypes;
    private List<LabelValueVO> jdbcDriverTypes;
    private List<LabelValueVO> validateTypes;
    private List<LabelValueVO> validateRuleTypes;
    private List<LabelValueVO> transformTypes;
    private List<LabelValueVO> jmsTypes;

    public SupportedVO() {
        super();
        topologyTypes = Arrays.asList(new LabelValueVO("1. 数据抽取、校验、转换", "retl"),
                new LabelValueVO("2. 数据存储", "persist"));
        spoutTypes = Arrays.asList(new LabelValueVO("1. 推送型JMS","jms"),
                new LabelValueVO("2. 拉取型JMS（推荐使用）","jmsPull"),
                new LabelValueVO("3. 关系型数据库","jdbc"));
        boltTypes = Arrays.asList(new LabelValueVO("1. 归一化处理","structure"),
                new LabelValueVO("2. 数据校验","validate"),
                new LabelValueVO("3. 数据转换","transform"),
                new LabelValueVO("4. 错误处理","error"),
                new LabelValueVO("5. JMS存储","jms"),
                new LabelValueVO("6. 关系型数据库存储","jdbc"));
        jdbcDriverTypes = Arrays.asList(new LabelValueVO("1. MySQL", "com.mysql.jdbc.Driver"),
                new LabelValueVO("2. Oracle", "oracle.jdbc.driver.OracleDriver"),
                new LabelValueVO("3. H2","org.h2.Driver"));
    }

    public void setTopologyTypes(List<LabelValueVO> topologyTypes) {
        this.topologyTypes = topologyTypes;
    }

    public void setSpoutTypes(List<LabelValueVO> spoutTypes) {
        this.spoutTypes = spoutTypes;
    }

    public void setBoltTypes(List<LabelValueVO> boltTypes) {
        this.boltTypes = boltTypes;
    }

    public void setJdbcDriverTypes(List<LabelValueVO> jdbcDriverTypes) {
        this.jdbcDriverTypes = jdbcDriverTypes;
    }

    public void setValidateTypes(List<LabelValueVO> validateTypes) {
        this.validateTypes = validateTypes;
    }

    public void setValidateRuleTypes(List<LabelValueVO> validateRuleTypes) {
        this.validateRuleTypes = validateRuleTypes;
    }

    public void setTransformTypes(List<LabelValueVO> transformTypes) {
        this.transformTypes = transformTypes;
    }

    public void setJmsTypes(List<LabelValueVO> jmsTypes) {
        this.jmsTypes = jmsTypes;
    }

    public List<LabelValueVO> getTopologyTypes() {
        return topologyTypes;
    }

    public List<LabelValueVO> getSpoutTypes() {
        return spoutTypes;
    }

    public List<LabelValueVO> getBoltTypes() {
        return boltTypes;
    }

    public List<LabelValueVO> getJdbcDriverTypes() {
        return jdbcDriverTypes;
    }

    public List<LabelValueVO> getValidateTypes() {
        return validateTypes;
    }

    public List<LabelValueVO> getValidateRuleTypes() {
        return validateRuleTypes;
    }

    public List<LabelValueVO> getTransformTypes() {
        return transformTypes;
    }

    public List<LabelValueVO> getJmsTypes() {
        return jmsTypes;
    }
}
