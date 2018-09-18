package org.mx.tools.ffee.service;

import org.mx.tools.ffee.dal.entity.BudgetItem;

import java.util.List;

public interface BudgetService {
    List<BudgetItem> getBudgets(String familyId, int year);

    BudgetItem saveBudget(BudgetItem budgetItem);

    BudgetItem deleteBudget(String budgetId);
}
