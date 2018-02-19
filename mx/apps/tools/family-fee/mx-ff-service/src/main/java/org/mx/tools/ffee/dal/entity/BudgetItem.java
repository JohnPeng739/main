package org.mx.tools.ffee.dal.entity;

import org.mx.dal.entity.Base;

/**
 * 描述： 家庭年度预算明细信息定义
 *
 * @author John.Peng
 * Date time 2018/2/17 下午4:44
 */
public interface BudgetItem extends Base {
    int getYear();

    void setYear(int year);

    Type getType();

    void setType(Type type);

    double getMoney();

    void setMoney(double money);

    String getDesc();

    void setDesc(String desc);

    Course getCource();

    void setCource(Course course);

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
