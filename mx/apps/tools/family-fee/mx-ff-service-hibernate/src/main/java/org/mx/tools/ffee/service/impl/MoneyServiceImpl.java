package org.mx.tools.ffee.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.DateUtils;
import org.mx.StringUtils;
import org.mx.dal.EntityFactory;
import org.mx.dal.service.GeneralAccessor;
import org.mx.error.UserInterfaceSystemErrorException;
import org.mx.tools.ffee.dal.entity.*;
import org.mx.tools.ffee.error.UserInterfaceFfeeErrorException;
import org.mx.tools.ffee.repository.MoneyRepository;
import org.mx.tools.ffee.service.AccountService;
import org.mx.tools.ffee.service.BudgetService;
import org.mx.tools.ffee.service.FamilyService;
import org.mx.tools.ffee.service.MoneyService;
import org.mx.tools.ffee.service.bean.MoneyInfoBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Component("moneyService")
public class MoneyServiceImpl implements MoneyService {
    private static final Log logger = LogFactory.getLog(MoneyServiceImpl.class);

    private GeneralAccessor generalAccessor;
    private AccountService accountService;
    private FamilyService familyService;
    private BudgetService budgetService;
    private MoneyRepository moneyRepository;

    @Autowired
    public MoneyServiceImpl(@Qualifier("generalAccessor") GeneralAccessor generalAccessor,
                            AccountService accountService,
                            FamilyService familyService,
                            BudgetService budgetService,
                            MoneyRepository moneyRepository) {
        super();
        this.generalAccessor = generalAccessor;
        this.accountService = accountService;
        this.familyService = familyService;
        this.budgetService = budgetService;
        this.moneyRepository = moneyRepository;
    }

    private DateUtils.DatetimeRange range(Integer year, Integer month, Integer week) {
        DateUtils.DatetimeRange range = null;
        if (week != null) {
            // 取周收入
            if (week == 0) {
                // 取本周
                range = DateUtils.DatetimeRange.currentWeek();
            } else if (week == -1) {
                // 取上周
                range = DateUtils.DatetimeRange.lastWeek();
            }
        } else if (year != null && month != null) {
            // 取月收入
            range = DateUtils.DatetimeRange.range(year, month);
        } else {
            // 取年收入
            if (year == null) {
                // 取当年
                year = Calendar.getInstance().get(Calendar.YEAR);
            }
            range = DateUtils.DatetimeRange.range(year);
        }
        return range;
    }

    @Transactional
    @Override
    public List<Income> getAccountIncomes(String accountId, Integer year, Integer month, Integer week) {
        if (StringUtils.isBlank(accountId)) {
            if (logger.isErrorEnabled()) {
                logger.error("The account's id is blank.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        DateUtils.DatetimeRange range = range(year, month, week);
        FfeeAccount account = generalAccessor.getById(accountId, FfeeAccount.class);
        if (account == null) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("The account[%s] not found.", accountId));
            }
            throw new UserInterfaceFfeeErrorException(
                    UserInterfaceFfeeErrorException.FfeeErrors.ACCOUNT_NOT_EXISTED
            );
        }
        accountService.writeAccessLog(String.format("获取%s账户的收入数据。", account.getNickname()));
        return moneyRepository.findIncomesByAccountId(accountId, range.getLowerLimit(), range.getUpperLimit());
    }

    @Transactional
    @Override
    public List<Income> getFamilyIncomes(String familyId, Integer year, Integer month, Integer week) {
        if (StringUtils.isBlank(familyId)) {
            if (logger.isErrorEnabled()) {
                logger.error("The family's id is blank.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        DateUtils.DatetimeRange range = range(year, month, week);
        Family family = generalAccessor.getById(familyId, Family.class);
        if (family == null) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("The family[%s] not found.", familyId));
            }
            throw new UserInterfaceFfeeErrorException(
                    UserInterfaceFfeeErrorException.FfeeErrors.FAMILY_NOT_EXISTED
            );
        }
        accountService.writeAccessLog(String.format("获取%s家庭的收入数据。", family.getName()));
        return moneyRepository.findIncomesByFamilyId(familyId, range.getLowerLimit(), range.getUpperLimit());
    }

    @Transactional
    @Override
    public List<Spending> getAccountSpendings(String accountId, Integer year, Integer month, Integer week) {
        if (StringUtils.isBlank(accountId)) {
            if (logger.isErrorEnabled()) {
                logger.error("The account's id is blank.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        DateUtils.DatetimeRange range = range(year, month, week);
        FfeeAccount account = generalAccessor.getById(accountId, FfeeAccount.class);
        if (account == null) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("The account[%s] not found.", accountId));
            }
            throw new UserInterfaceFfeeErrorException(
                    UserInterfaceFfeeErrorException.FfeeErrors.ACCOUNT_NOT_EXISTED
            );
        }
        accountService.writeAccessLog(String.format("获取%s账户的支出数据。", account.getNickname()));
        return moneyRepository.findSpendingsByAccountId(accountId, range.getLowerLimit(), range.getUpperLimit());
    }

    @Transactional
    @Override
    public List<Spending> getFamilySpendings(String familyId, Integer year, Integer month, Integer week) {
        if (StringUtils.isBlank(familyId)) {
            if (logger.isErrorEnabled()) {
                logger.error("The family's id is blank.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        DateUtils.DatetimeRange range = range(year, month, week);
        Family family = generalAccessor.getById(familyId, Family.class);
        if (family == null) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("The family[%s] not found.", familyId));
            }
            throw new UserInterfaceFfeeErrorException(
                    UserInterfaceFfeeErrorException.FfeeErrors.FAMILY_NOT_EXISTED
            );
        }
        accountService.writeAccessLog(String.format("获取%s家庭的支出数据。", family.getName()));
        return moneyRepository.findSpendingsByFamilyId(familyId, range.getLowerLimit(), range.getUpperLimit());
    }

    private List<YearMoneyItem> getYearMoneyTotalByAccount(String accountId, Class<? extends MoneyItem> clazz) {
        if (StringUtils.isBlank(accountId)) {
            if (logger.isErrorEnabled()) {
                logger.error("The account's id is blank.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        FfeeAccount account = generalAccessor.getById(accountId, FfeeAccount.class);
        if (account == null) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("The account[%s] not found.", accountId));
            }
            throw new UserInterfaceFfeeErrorException(
                    UserInterfaceFfeeErrorException.FfeeErrors.ACCOUNT_NOT_EXISTED
            );
        }
        List<? extends MoneyItem> items = generalAccessor.find(
                GeneralAccessor.ConditionTuple.eq("owner", account), clazz
        );
        return computeYearMoney(items);
    }

    private List<YearMoneyItem> computeYearMoney(List<? extends MoneyItem> moneyItems) {
        Map<Integer, DoubleSummaryStatistics> map = moneyItems.stream().map(item -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(item.getOccurTime());
            int year = calendar.get(Calendar.YEAR);
            return new IntKeyDoubleValue(year, item.getMoney());
        }).collect(Collectors.groupingBy(IntKeyDoubleValue::getK, Collectors.summarizingDouble(IntKeyDoubleValue::getV)));
        List<YearMoneyItem> result = new ArrayList<>();
        if (map != null && !map.isEmpty()) {
            map.forEach((k, v) -> result.add(new YearMoneyItem(k, v.getCount(), v.getSum(), v.getMin(),
                    v.getMax(), v.getAverage())));
        }
        return result;
    }

    private List<YearMonthMoneyItem> computeYearMonthMoney(List<? extends MoneyItem> moneyItems) {
        Map<Integer, DoubleSummaryStatistics> map = moneyItems.stream().map(item -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(item.getOccurTime());
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            return new IntKeyDoubleValue(year * 100 + month, item.getMoney());
        }).collect(Collectors.groupingBy(IntKeyDoubleValue::getK, Collectors.summarizingDouble(IntKeyDoubleValue::getV)));
        List<YearMonthMoneyItem> result = new ArrayList<>();
        if (map != null && !map.isEmpty()) {
            map.forEach((k, v) -> result.add(new YearMonthMoneyItem(k / 100, k % 100, v.getCount(),
                    v.getSum(), v.getMin(), v.getMax(), v.getAverage())));
        }
        return result;
    }

    private List<MemberMoneyItem> computeMemberMoney(List<? extends MoneyItem> moneyItems) {
        Map<String, DoubleSummaryStatistics> map = moneyItems.stream().map(item -> {
            FfeeAccount account = item.getOwner();
            String nickname = account != null ? account.getNickname() : "NA";
            return new StringKeyDoubleValue(nickname, item.getMoney());
        }).collect(Collectors.groupingBy(StringKeyDoubleValue::getK, Collectors.summarizingDouble(StringKeyDoubleValue::getV)));
        List<MemberMoneyItem> result = new ArrayList<>();
        if (map != null && !map.isEmpty()) {
            map.forEach((k, v) -> result.add(new MemberMoneyItem(k, v.getCount(), v.getSum(), v.getMin(),
                    v.getMax(), v.getAverage())));
        }
        return result;
    }

    @Transactional
    @Override
    public AccountMoneySummary getAccountMoneySummary(String accountId) {
        if (StringUtils.isBlank(accountId)) {
            if (logger.isErrorEnabled()) {
                logger.error("The account's id is blank.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        AccountService.AccountSummary accountSummary = accountService.getAccountSummaryById(accountId);
        FfeeAccount account = accountSummary.getAccount();
        Family family = accountSummary.getFamily();
        if (account == null) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("The account[%s] not found.", accountId));
            }
            throw new UserInterfaceFfeeErrorException(
                    UserInterfaceFfeeErrorException.FfeeErrors.ACCOUNT_NOT_EXISTED
            );
        }
        // 历年统计的收入和支出
        List<YearMoneyItem> recentIncomes = getYearMoneyTotalByAccount(accountId, Income.class);
        List<YearMoneyItem> recentSpendings = getYearMoneyTotalByAccount(accountId, Spending.class);
        RecentMoneySummary recent = new RecentMoneySummary(null, recentIncomes, recentSpendings);
        // 本年度按月统计的收入和支出
        List<? extends MoneyItem> incomes = getAccountIncomes(accountId, Calendar.getInstance().get(Calendar.YEAR), null, null);
        double totalIncome = incomes.stream().mapToDouble(MoneyItem::getMoney).sum();
        List<YearMonthMoneyItem> byMonthIncome = computeYearMonthMoney(incomes);
        List<MemberMoneyItem> byMemberIncome = computeMemberMoney(incomes);
        MoneySummary currentIncomes = new MoneySummary(totalIncome, byMonthIncome, byMemberIncome);
        List<? extends MoneyItem> spendings = getAccountSpendings(accountId, Calendar.getInstance().get(Calendar.YEAR), null, null);
        double totalSpending = spendings.stream().mapToDouble(MoneyItem::getMoney).sum();
        List<YearMonthMoneyItem> byMonthSpending = computeYearMonthMoney(spendings);
        List<MemberMoneyItem> byMemberSpending = computeMemberMoney(spendings);
        MoneySummary currentSpendings = new MoneySummary(totalSpending, byMonthSpending, byMemberSpending);
        CurrentMoneySummary current = new CurrentMoneySummary(0.0, currentIncomes, currentSpendings);

        accountService.writeAccessLog(String.format("获取%s账户的收入总览数据。", account.getNickname()));
        return new AccountMoneySummary(account.getId(), account.getOpenId(), account.getNickname(),
                account.getAvatarUrl(), family, recent, current);
    }

    private List<YearMoneyItem> getYearMoneyTotalByFamily(String familyId, Class<? extends MoneyItem> clazz) {
        if (StringUtils.isBlank(familyId)) {
            if (logger.isErrorEnabled()) {
                logger.error("The family's id is blank.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        Family family = generalAccessor.getById(familyId, Family.class);
        if (family == null) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("The family[%s] not found.", familyId));
            }
            throw new UserInterfaceFfeeErrorException(
                    UserInterfaceFfeeErrorException.FfeeErrors.FAMILY_NOT_EXISTED
            );
        }
        List<? extends MoneyItem> items = generalAccessor.find(
                GeneralAccessor.ConditionTuple.eq("family", family), clazz
        );
        return computeYearMoney(items);
    }

    @Transactional
    @Override
    public FamilyMoneySummary getFamilyMoneySummary(String familyId) {
        if (StringUtils.isBlank(familyId)) {
            if (logger.isErrorEnabled()) {
                logger.error("The family's id is blank.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        Family family = familyService.getFamily(familyId);
        if (family == null) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("The family[%s] not found.", familyId));
            }
            throw new UserInterfaceFfeeErrorException(
                    UserInterfaceFfeeErrorException.FfeeErrors.FAMILY_NOT_EXISTED
            );
        }
        // 历年统计的预算、收入和支出
        List<YearMoneyItem> recentBudgets = budgetService.getBudgetTotalByFamily(familyId);
        List<YearMoneyItem> recentIncomes = computeYearMoney(getIncomes(familyId, -1));
        List<YearMoneyItem> recentSpendings = computeYearMoney(getSpendings(familyId, -1, -1));
        RecentMoneySummary recent = new RecentMoneySummary(recentBudgets, recentIncomes, recentSpendings);
        // 取本年度预算总数
        double budget = budgetService.getBudgetTotal(familyId, null);
        // 本年收入总数、按月收入总数和按成员收入总数
        List<? extends MoneyItem> incomes = getIncomes(familyId, Calendar.getInstance().get(Calendar.YEAR));
        double totalIncome = incomes.stream().mapToDouble(MoneyItem::getMoney).sum();
        List<YearMonthMoneyItem> byMonthIncome = computeYearMonthMoney(incomes);
        List<MemberMoneyItem> byMemberIncome = computeMemberMoney(incomes);
        MoneySummary currentIncomes = new MoneySummary(totalIncome, byMonthIncome, byMemberIncome);
        // 本年支出总数、按月支出总数和按成员支出总数
        List<? extends MoneyItem> spendings = getSpendings(familyId, Calendar.getInstance().get(Calendar.YEAR), -1);
        double totalSpending = spendings.stream().mapToDouble(MoneyItem::getMoney).sum();
        List<YearMonthMoneyItem> byMonthSpending = computeYearMonthMoney(spendings);
        List<MemberMoneyItem> byMemberSpending = computeMemberMoney(spendings);
        MoneySummary currentSpendings = new MoneySummary(totalSpending, byMonthSpending, byMemberSpending);
        CurrentMoneySummary current = new CurrentMoneySummary(budget, currentIncomes, currentSpendings);
        accountService.writeAccessLog(String.format("获取%s家庭的收入总览数据。", family.getName()));
        return new FamilyMoneySummary(family.getId(), family.getName(), family.getAvatarUrl(), recent, current);
    }

    @Transactional
    @Override
    public List<Income> getIncomes(String familyId, int year) {
        if (StringUtils.isBlank(familyId)) {
            if (logger.isErrorEnabled()) {
                logger.error("The family's id is blank.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        Family family = generalAccessor.getById(familyId, Family.class);
        if (family == null) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("The family[%s] not found.", familyId));
            }
            throw new UserInterfaceFfeeErrorException(
                    UserInterfaceFfeeErrorException.FfeeErrors.FAMILY_NOT_EXISTED
            );
        }
        accountService.writeAccessLog(String.format("获取%s家庭的收入数据。", family.getName()));
        GeneralAccessor.ConditionTuple familyCond = GeneralAccessor.ConditionTuple.eq("family", family);
        if (year > 0) {
            DateUtils.DatetimeRange range = DateUtils.DatetimeRange.range(year);
            return generalAccessor.find(GeneralAccessor.ConditionGroup.and(
                    familyCond,
                    GeneralAccessor.ConditionTuple.gte("occurTime", range.getLowerLimit()),
                    GeneralAccessor.ConditionTuple.lte("occurTime", range.getUpperLimit())
            ), Income.class);
        } else {
            return generalAccessor.find(familyCond, Income.class);
        }
    }

    @Transactional
    @Override
    public MoneyItem deleteMoney(MoneyInfoBean moneyInfoBean, Class<? extends MoneyItem> clazz) {
        if (moneyInfoBean == null) {
            if (logger.isErrorEnabled()) {
                logger.error("The money item object is null.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        String id = moneyInfoBean.getId();
        if (StringUtils.isBlank(id)) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("The money item[%s]'s id is blank. ", clazz.getName()));
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_UNSUPPORTED
            );
        }
        accountService.writeAccessLog("删除一笔收入/支出数据。");
        return generalAccessor.remove(id, clazz);
    }

    @Transactional
    @Override
    public MoneyItem saveMoney(MoneyInfoBean moneyInfoBean, Class<? extends MoneyItem> clazz) {
        if (moneyInfoBean == null) {
            if (logger.isErrorEnabled()) {
                logger.error("The money item object is null.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        // 检查家庭，家庭必须要有
        String familyId = moneyInfoBean.getFamilyId();
        if (StringUtils.isBlank(familyId)) {
            if (logger.isErrorEnabled()) {
                logger.error("The family's id is blank.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        Family family = generalAccessor.getById(familyId, Family.class);
        if (family == null) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("The family[%s] not found.", familyId));
            }
            throw new UserInterfaceFfeeErrorException(
                    UserInterfaceFfeeErrorException.FfeeErrors.FAMILY_NOT_EXISTED
            );
        }
        // 检查科目，科目必须要有
        String courseId = moneyInfoBean.getCourseId();
        if (StringUtils.isBlank(courseId)) {
            if (logger.isErrorEnabled()) {
                logger.error("The course's id is blank.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        Course course = generalAccessor.getById(courseId, Course.class);
        if (course == null) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("The course[%s] not found.", courseId));
            }
            throw new UserInterfaceFfeeErrorException(
                    UserInterfaceFfeeErrorException.FfeeErrors.COURSE_NOT_EXISTED
            );
        }
        // 检查收入所有者，可以为空，表示公共收入
        String ownerId = moneyInfoBean.getOwnerId();
        FfeeAccount owner = null;
        if (!StringUtils.isBlank(ownerId)) {
            owner = generalAccessor.getById(ownerId, FfeeAccount.class);
            if (owner == null) {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("The owner account[%s] not found.", ownerId));
                }
                throw new UserInterfaceFfeeErrorException(
                        UserInterfaceFfeeErrorException.FfeeErrors.ACCOUNT_NOT_EXISTED
                );
            }
        }
        String id = moneyInfoBean.getId();
        MoneyItem saved = null;
        if (!StringUtils.isBlank(id)) {
            MoneyItem checked = generalAccessor.getById(id, clazz);
            if (checked != null) {
                saved = checked;
            }
        }
        if (saved == null) {
            saved = EntityFactory.createEntity(clazz);
        }
        saved.setDesc(moneyInfoBean.getDesc());
        saved.setMoney(moneyInfoBean.getMoney());
        saved.setOccurTime(moneyInfoBean.getOccurTime());
        saved.setFamily(family);
        saved.setCourse(course);
        saved.setOwner(owner);
        saved = generalAccessor.save(saved);
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Save the %s successfully, family: %s, course: %s, money: %10.2f.",
                    saved instanceof Income ? "income" : "spending", family.getName(), course.getName(),
                    saved.getMoney()));
        }
        accountService.writeAccessLog("保存一笔收入/支出数据。");
        return saved;
    }

    @Transactional
    @Override
    public List<Spending> getSpendings(String familyId, int year, int month) {
        if (StringUtils.isBlank(familyId)) {
            if (logger.isErrorEnabled()) {
                logger.error("The family's id is blank.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        Family family = generalAccessor.getById(familyId, Family.class);
        if (family == null) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("The family[%s] not found.", familyId));
            }
            throw new UserInterfaceFfeeErrorException(
                    UserInterfaceFfeeErrorException.FfeeErrors.FAMILY_NOT_EXISTED
            );
        }
        GeneralAccessor.ConditionTuple familyCond = GeneralAccessor.ConditionTuple.eq("family", family);
        accountService.writeAccessLog(String.format("获取%s家庭的支出数据。", family.getName()));
        if (year > 0) {
            DateUtils.DatetimeRange range = month > 0 ?
                    DateUtils.DatetimeRange.range(year, month) : DateUtils.DatetimeRange.range(year);
            return generalAccessor.find(GeneralAccessor.ConditionGroup.and(
                    familyCond,
                    GeneralAccessor.ConditionTuple.gte("occurTime", range.getLowerLimit()),
                    GeneralAccessor.ConditionTuple.lte("occurTime", range.getUpperLimit())
            ), Spending.class);
        } else {
            DateUtils.DatetimeRange range = DateUtils.DatetimeRange.range(year);
            return generalAccessor.find(familyCond, Spending.class);
        }
    }

    @Transactional
    @Override
    public List<Spending> getSpendingsLastWeek(String familyId) {
        if (StringUtils.isBlank(familyId)) {
            if (logger.isErrorEnabled()) {
                logger.error("The family's id is blank.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        Family family = generalAccessor.getById(familyId, Family.class);
        if (family == null) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("The family[%s] not found.", familyId));
            }
            throw new UserInterfaceFfeeErrorException(
                    UserInterfaceFfeeErrorException.FfeeErrors.FAMILY_NOT_EXISTED
            );
        }
        DateUtils.DatetimeRange range = DateUtils.DatetimeRange.lastWeek();
        accountService.writeAccessLog(String.format("获取%s家庭的支出数据。", family.getName()));
        return generalAccessor.find(GeneralAccessor.ConditionGroup.and(
                GeneralAccessor.ConditionTuple.eq("family", family),
                GeneralAccessor.ConditionTuple.gte("occurTime", range.getLowerLimit()),
                GeneralAccessor.ConditionTuple.lte("occurTime", range.getUpperLimit())
        ), Spending.class);
    }

    private class IntKeyDoubleValue {
        private int k;
        private double v;

        public IntKeyDoubleValue(int k, double v) {
            super();
            this.k = k;
            this.v = v;
        }

        public int getK() {
            return k;
        }

        public double getV() {
            return v;
        }
    }

    private class StringKeyDoubleValue {
        private String k;
        private double v;

        public StringKeyDoubleValue(String k, double v) {
            super();
            this.k = k;
            this.v = v;
        }

        public String getK() {
            return k;
        }

        public double getV() {
            return v;
        }
    }
}
