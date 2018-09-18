package org.mx.tools.ffee.dal.entity;

import org.mx.dal.entity.Base;

/**
 * 描述： 家庭年度预算明细信息定义
 *
 * @author John.Peng
 * Date time 2018/2/17 下午4:44
 */
public interface BudgetItem extends Base {
    Family getFamily();

    void setFamily(Family family);

    int getYear();

    void setYear(int year);

    double getMoney();

    void setMoney(double money);

    String getDesc();

    void setDesc(String desc);

    Course getCourse();

    void setCourse(Course course);
}
