package org.mx.tools.ffee.service;

import org.mx.tools.ffee.dal.entity.BudgetItem;

import java.util.List;

public interface BudgetService {
    double getBudgetTotal(String familyId, Integer year);

    List<MoneyService.YearMoneyItem> getBudgetTotalByFamily(String familyId);

    List<BudgetItem> getBudgets(String familyId, int year);

    BudgetItem saveBudget(BudgetItem budgetItem);

    BudgetItem deleteBudget(String budgetId);
}
