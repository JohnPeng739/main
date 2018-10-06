package org.mx.tools.ffee.service;

import org.mx.tools.ffee.dal.entity.BudgetItem;
import org.mx.tools.ffee.service.bean.BudgetItemInfoBean;

import java.util.List;

public interface BudgetService {
    double getBudgetTotal(String familyId, Integer year);

    List<MoneyService.YearMoneyItem> getBudgetTotalByFamily(String familyId);

    List<BudgetItem> getBudgets(String familyId, int year);

    BudgetItem saveBudget(BudgetItemInfoBean budgetItemInfoBean);

    BudgetItem deleteBudget(BudgetItemInfoBean budgetItemInfoBean);
}
