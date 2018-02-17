package org.mx.tools.ffee.dal.entity;

import org.mx.dal.entity.Base;

/**
 * 描述： 家庭年度预算明细信息定义
 *
 * @author: John.Peng
 * @date: 2018/2/17 下午4:44
 */
public interface BudgetItem extends Base {
    Type getType();

    void setType(Type type);

    double getMoney();

    void setMoney(double money);

    String getDesc();

    void setDesc(String desc);

    Cource getCource();

    void setCource(Cource cource);

    enum Type {
        /**
         * 收入预算
         */
        INCOME,
        /**
         * 支出预算
         */
        SPENDING
    }
}
