package org.mx.dal.entity;

import org.springframework.data.mongodb.core.index.Indexed;

/**
 * 基于Mongodb实现的操作日志实体
 *
 * @author : john.peng date : 2017/11/19
 * @see BaseEntity
 * @see OperateLog
 */
public class OperateLogEntity extends BaseEntity implements OperateLog {
    @Indexed(unique = true)
    private String content;

    /**
     * {@inheritDoc}
     *
     * @see BaseEntity#toString()
     */
    @Override
    public String toString() {
        return super.toString() +
                ", content='" + content + '\'';
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
