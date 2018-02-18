package org.mx.tools.ffee.dal.entity;

import org.mx.dal.entity.Base;

/**
 * 描述： 收入或支出的明细信息定义
 *
 * @author: John.Peng
 * @date: 2018/2/17 下午5:26
 */
public interface MoneyItem  extends Base {
    Course getCource();

    void setCource(Course course);

    double getMoney();

    void setMoney(double money);

    String getDesc();

    void setDesc(String desc);

    FfeeAccount getOwner();

    void setOwner(FfeeAccount account);
}
