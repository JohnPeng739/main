package org.mx.tools.ffee.dal.entity;

import java.util.Set;

/**
 * 描述： 家庭年度预算信息定义
 *
 * @author: John.Peng
 * @date: 2018/2/17 下午4:42
 */
public interface Budget {
    int getYear();

    void setYear(int year);

    Set<BudgetItem> getItems();

    void setItems(Set<BudgetItem> items);
}
