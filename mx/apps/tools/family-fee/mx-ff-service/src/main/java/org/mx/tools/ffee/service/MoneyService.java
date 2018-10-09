package org.mx.tools.ffee.service;

import org.mx.tools.ffee.dal.entity.Family;
import org.mx.tools.ffee.dal.entity.Income;
import org.mx.tools.ffee.dal.entity.MoneyItem;
import org.mx.tools.ffee.dal.entity.Spending;
import org.mx.tools.ffee.service.bean.MoneyInfoBean;

import java.util.List;

public interface MoneyService {
    List<Income> getAccountIncomes(String accountId, Integer year, Integer month, Integer week);

    List<Income> getFamilyIncomes(String familyId, Integer year, Integer month, Integer week);

    List<Spending> getAccountSpendings(String accountId, Integer year, Integer month, Integer week);

    List<Spending> getFamilySpendings(String familyId, Integer year, Integer month, Integer week);

    AccountMoneySummary getAccountMoneySummary(String accountId);

    FamilyMoneySummary getFamilyMoneySummary(String familyId);

    List<Income> getIncomes(String familyId, int year);

    List<Spending> getSpendings(String familyId, int year, int month);

    List<Spending> getSpendingsLastWeek(String familyId);

    MoneyItem saveMoney(MoneyInfoBean moneyInfoBean, Class<? extends MoneyItem> clazz);

    MoneyItem deleteMoney(MoneyInfoBean moneyInfoBean, Class<? extends MoneyItem> clazz);

    class AccountMoneySummary {
        private String id, openId, nickname, avatarUrl;
        private Family family;
        private RecentMoneySummary recent;
        private CurrentMoneySummary current;

        public AccountMoneySummary(String id, String openId, String nickname, String avatarUrl, Family family,
                                   RecentMoneySummary recent, CurrentMoneySummary current) {
            super();
            this.id = id;
            this.openId = openId;
            this.nickname = nickname;
            this.avatarUrl = avatarUrl;
            this.family = family;
            this.recent = recent;
            this.current = current;
        }

        public String getId() {
            return id;
        }

        public String getOpenId() {
            return openId;
        }

        public String getNickname() {
            return nickname;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public Family getFamily() {
            return family;
        }

        public RecentMoneySummary getRecent() {
            return recent;
        }

        public CurrentMoneySummary getCurrent() {
            return current;
        }
    }

    class FamilyMoneySummary {
        private String id, name, avatarUrl;
        private RecentMoneySummary recent;
        private CurrentMoneySummary current;

        public FamilyMoneySummary(String id, String name, String avatarUrl, RecentMoneySummary recent,
                                  CurrentMoneySummary current) {
            super();
            this.id = id;
            this.name = name;
            this.avatarUrl = avatarUrl;
            this.recent = recent;
            this.current = current;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public RecentMoneySummary getRecent() {
            return recent;
        }

        public CurrentMoneySummary getCurrent() {
            return current;
        }
    }

    class RecentMoneySummary {
        private List<YearMoneyItem> budgets, incomes, spendings;

        public RecentMoneySummary(List<YearMoneyItem> budgets, List<YearMoneyItem> income,
                                  List<YearMoneyItem> spendings) {
            super();
            this.budgets = budgets;
            this.incomes = income;
            this.spendings = spendings;
        }

        public List<YearMoneyItem> getBudgets() {
            return budgets;
        }

        public List<YearMoneyItem> getIncomes() {
            return incomes;
        }

        public List<YearMoneyItem> getSpendings() {
            return spendings;
        }
    }

    class CurrentMoneySummary {
        private double budget;
        private MoneySummary income, spending;

        public CurrentMoneySummary(double budget, MoneySummary income, MoneySummary spending) {
            super();
            this.budget = budget;
            this.income = income;
            this.spending = spending;
        }

        public double getBudget() {
            return budget;
        }

        public MoneySummary getIncome() {
            return income;
        }

        public MoneySummary getSpending() {
            return spending;
        }
    }

    class MoneySummary {
        private double total;
        private List<YearMonthMoneyItem> byMonth;
        private List<MemberMoneyItem> byMember;

        public MoneySummary(double total, List<YearMonthMoneyItem> byMonth, List<MemberMoneyItem> byMember) {
            super();
            this.total = total;
            this.byMonth = byMonth;
            this.byMember = byMember;
        }

        public double getTotal() {
            return total;
        }

        public List<YearMonthMoneyItem> getByMonth() {
            return byMonth;
        }

        public List<MemberMoneyItem> getByMember() {
            return byMember;
        }
    }

    class YearMoneyItem {
        private int year;
        private long count;
        private double total, min, max, avg;

        public YearMoneyItem(int year, long count, double total, double min, double max, double avg) {
            super();
            this.year = year;
            this.count = count;
            this.total = total;
            this.min = min;
            this.max = max;
            this.avg = avg;
        }

        public int getYear() {
            return year;
        }

        public long getCount() {
            return count;
        }

        public double getTotal() {
            return total;
        }

        public double getMin() {
            return min;
        }

        public double getMax() {
            return max;
        }

        public double getAvg() {
            return avg;
        }
    }

    class YearMonthMoneyItem {
        private int year, month;
        private long count;
        private double total, min, max, avg;

        public YearMonthMoneyItem(int year, int month, long count, double total, double min, double max, double avg) {
            super();
            this.year = year;
            this.month = month;
            this.count = count;
            this.total = total;
            this.min = min;
            this.max = max;
            this.avg = avg;
        }

        public int getYear() {
            return year;
        }

        public int getMonth() {
            return month;
        }

        public long getCount() {
            return count;
        }

        public double getTotal() {
            return total;
        }

        public double getMin() {
            return min;
        }

        public double getMax() {
            return max;
        }

        public double getAvg() {
            return avg;
        }
    }

    class MemberMoneyItem {
        private String nickname;
        private long count;
        private double total, min, max, avg;

        public MemberMoneyItem(String nickname, long count, double total, double min, double max, double avg) {
            super();
            this.nickname = nickname;
            this.count = count;
            this.total = total;
            this.min = min;
            this.max = max;
            this.avg = avg;
        }

        public String getNickname() {
            return nickname;
        }

        public long getCount() {
            return count;
        }

        public double getTotal() {
            return total;
        }

        public double getMin() {
            return min;
        }

        public double getMax() {
            return max;
        }

        public double getAvg() {
            return avg;
        }
    }
}
