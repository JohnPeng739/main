package org.mx.tools.ffee.service;

import org.mx.tools.ffee.dal.entity.BudgetItem;
import org.mx.tools.ffee.dal.entity.Course;
import org.mx.tools.ffee.dal.entity.Income;
import org.mx.tools.ffee.dal.entity.Spending;

import java.util.List;

/**
 * 描述： 收入和支持管理服务接口定义
 *
 * @author John.Peng
 *         Date time 2018/2/19 上午10:32
 */
public interface MoneyManageService {
    List<Course> getCourses();

    List<Course> getPublicCourses();

    List<Course> getPrivateCourses();

    List<BudgetItem> getBudgets();

    List<BudgetItem> getBudgets(int year);

    Income income(String courseId, String ownerId, double money, String desc, long datetime);

    double getIncomeTotal();

    double getIncomeTotal(int year);

    double getIncomeTotal(int year, int month);

    double getIncomeTotal(long startDatetime, long endDatetime);

    List<Income> getIncomes();

    List<Income> getIncomes(int year);

    List<Income> getIncomes(int year, int month);

    List<Income> getIncomes(long startDatetime, long endDatetime);

    Spending spend(String courseId, String ownerId, double money, String desc, long datetime);

    double getSpendingTotal();

    double getSpendingTotal(int year);

    double getSpendingTotal(int year, int month);

    double getSpendingTotal(long startDatetime, long endDatetime);

    List<Spending> getSpendings();

    List<Spending> getSpendings(int year);

    List<Spending> getSpendings(int year, int month);

    List<Spending> getSpendings(long startDatetime, long endDatetime);
}
