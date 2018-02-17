package org.mx.tools.ffee.dal.entity;

import org.mx.dal.entity.Base;

/**
 * 描述： 科目类型信息定义
 *
 * @author: John.Peng
 * @date: 2018/2/17 下午4:46
 */
public interface Cource extends Base {
    String getName();

    void setName(String name);

    String getDesc();

    void setDesc(String desc);

    BudgetItem.Type getType();

    void setType(BudgetItem.Type type);
}
