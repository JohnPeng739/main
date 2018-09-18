package org.mx.tools.ffee.dal.entity;

import org.mx.dal.entity.Base;

/**
 * 描述： 收入或支出的明细信息定义
 *
 * @author John.Peng
 *         Date time 2018/2/17 下午5:26
 */
public interface MoneyItem extends Base {
    Family getFamily();

    void setFamily(Family family);

    Course getCourse();

    void setCourse(Course course);

    double getMoney();

    void setMoney(double money);

    long getOccurTime();

    void setOccurTime(long time);

    String getDesc();

    void setDesc(String desc);

    FfeeAccount getOwner();

    void setOwner(FfeeAccount account);
}
