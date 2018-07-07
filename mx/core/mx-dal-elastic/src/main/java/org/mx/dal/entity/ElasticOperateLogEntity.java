package org.mx.dal.entity;

import org.mx.dal.annotation.ElasticField;
import org.mx.dal.annotation.ElasticIndex;

/**
 * 描述： 基于Elastic实现的操作审计日志实体
 *
 * @author John.Peng
 * Date time 2018/4/1 上午8:50
 */
@ElasticIndex("operateLog")
public class ElasticOperateLogEntity extends ElasticBaseEntity implements OperateLog {
    @ElasticField
    private String system, module;
    @ElasticField(type = "text", analyzer = "hanlp")
    private String content;
    @ElasticField
    private OperateType operateType = OperateType.QUERY;

    /**
     * {@inheritDoc}
     *
     * @see OperateLog#getSystem()
     */
    @Override
    public String getSystem() {
        return system;
    }

    /**
     * {@inheritDoc}
     *
     * @see OperateLog#setSystem(String)
     */
    @Override
    public void setSystem(String system) {
        this.system = system;
    }

    /**
     * {@inheritDoc}
     *
     * @see OperateLog#getModule()
     */
    @Override
    public String getModule() {
        return module;
    }

    /**
     * {@inheritDoc}
     *
     * @see OperateLog#setModule(String)
     */
    @Override
    public void setModule(String module) {
        this.module = module;
    }

    /**
     * {@inheritDoc}
     *
     * @see OperateLog#getContent()
     */
    @Override
    public String getContent() {
        return content;
    }

    /**
     * {@inheritDoc}
     *
     * @see OperateLog#setContent(String)
     */
    @Override
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * {@inheritDoc}
     *
     * @see OperateLog#getOperateType()
     */
    @Override
    public OperateType getOperateType() {
        return operateType;
    }

    /**
     * {@inheritDoc}
     *
     * @see OperateLog#setOperateType(OperateType)
     */
    @Override
    public void setOperateType(OperateType operateType) {
        this.operateType = operateType;
    }
}
