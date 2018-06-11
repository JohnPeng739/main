package org.mx.dal.entity;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 基于Mongodb实现的操作日志实体
 *
 * @author : john.peng date : 2017/11/19
 * @see MongoBaseEntity
 * @see OperateLog
 */
@Document(collection = "operateLog")
public class MongoOperateLogEntity extends MongoBaseEntity implements OperateLog {
    @Indexed
    private String system, module;
    @Indexed
    private OperateType operateType = OperateType.QUERY;
    @TextIndexed
    private String content;

    /**
     * {@inheritDoc}
     *
     * @see MongoBaseEntity#toString()
     */
    @Override
    public String toString() {
        return super.toString() +
                ", system='" + system + '\'' +
                ", module='" + module + '\'' +
                ", operateType='" + operateType.name() + '\'' +
                ", content='" + content + '\'';
    }

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
}
