package org.mx.tools.ffee.dal.entity;

import org.mx.dal.entity.BaseDictTree;

/**
 * 描述： 科目类型信息定义，科目为树状字典。
 *
 * @author John.Peng
 *         Date time 2018/2/17 下午4:46
 */
public interface Course extends BaseDictTree {
    BudgetItem.Type getType();

    void setType(BudgetItem.Type type);

    boolean isPublic();

    void setPublic(boolean isPublic);
}
