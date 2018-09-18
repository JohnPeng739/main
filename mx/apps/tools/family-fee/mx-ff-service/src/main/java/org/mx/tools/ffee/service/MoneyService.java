package org.mx.tools.ffee.service;

import org.mx.tools.ffee.dal.entity.Income;
import org.mx.tools.ffee.dal.entity.Spending;

import java.util.List;

public interface MoneyService {
    List<Income> getIncomes(String familyId, int year);

    Income saveIncome(Income income);

    Income deleteIncome(String incomeId);

    List<Spending> getSpendings(String familyId, int year, int month);

    List<Spending> getSpendingsLastWeek(String familyId);

    Spending saveSpending(Spending spending);

    Spending deleteSpending(String spendingId);
}
