package org.mx.tools.ffee.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.dal.EntityFactory;
import org.mx.dal.service.GeneralAccessor;
import org.mx.dal.service.OperateLogService;
import org.mx.tools.ffee.dal.entity.*;
import org.mx.tools.ffee.error.UserInterfaceFfeeErrorException;
import org.mx.tools.ffee.repository.IncomeRepository;
import org.mx.tools.ffee.repository.SpendingRepository;
import org.mx.tools.ffee.service.MoneyManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述： 收入和支出等管理服务实现类，基于Hibernate实现。
 *
 * @author John.Peng
 *         Date time 2018/2/19 上午11:41
 */
@Component
public class MoneyManageServiceImpl implements MoneyManageService {
    private static final Log logger = LogFactory.getLog(MoneyManageServiceImpl.class);

    @Autowired
    @Qualifier("generalAccessor")
    private GeneralAccessor accessor = null;

    @Autowired
    private OperateLogService operateLogService = null;

    @Autowired
    private IncomeRepository incomeRepository = null;

    @Autowired
    private SpendingRepository spendingRepository = null;

    /**
     * 获取所有的科目信息
     *
     * @see MoneyManageService#getCourses()
     */
    @Override
    @Transactional(readOnly = true)
    public List<Course> getCourses() {
        return accessor.list(Course.class);
    }

    private List<Course> getCourses(boolean isPublic) {
        List<GeneralAccessor.ConditionTuple> touples = new ArrayList<>();
        touples.add(new GeneralAccessor.ConditionTuple("isPublic", isPublic));
        return accessor.find(touples, Course.class);
    }

    /**
     * 获取所有的公用科目信息
     *
     * @see MoneyManageService#getPublicCourses()
     */
    @Override
    @Transactional(readOnly = true)
    public List<Course> getPublicCourses() {
        return getCourses(true);
    }

    /**
     * 获取所有的私有科目信息
     *
     * @see MoneyManageService#getPrivateCourses()
     */
    @Override
    @Transactional(readOnly = true)
    public List<Course> getPrivateCourses() {
        return getCourses(false);
    }

    /**
     * 获取所有的预算信息
     *
     * @see MoneyManageService#getBudgets()
     */
    @Override
    @Transactional(readOnly = true)
    public List<BudgetItem> getBudgets() {
        return getBudgets(-1);
    }

    /**
     * 获取指定连读的所有预算信息
     *
     * @see MoneyManageService#getBudgets(int)
     */
    public List<BudgetItem> getBudgets(int year) {
        if (year == -1) {
            return accessor.list(BudgetItem.class);
        } else {
            List<GeneralAccessor.ConditionTuple> touples = new ArrayList<>();
            touples.add(new GeneralAccessor.ConditionTuple("year", year));
            return accessor.find(touples, BudgetItem.class);
        }
    }

    /**
     * 记入一笔收入信息
     *
     * @see MoneyManageService#income(String, String, double, String, long)
     */
    @Override
    @Transactional()
    public Income income(String courseId, String ownerId, double money, String desc, long datetime) {
        Course course = accessor.getById(courseId, Course.class);
        if (course == null) {
            throw new UserInterfaceFfeeErrorException(UserInterfaceFfeeErrorException.FfeeErrors.COURSE_NOT_EXISTED);
        }
        FfeeAccount ffeeAccount = accessor.getById(ownerId, FfeeAccount.class);
        if (ffeeAccount == null) {
            throw new UserInterfaceFfeeErrorException(UserInterfaceFfeeErrorException.FfeeErrors.ACCOUNT_NOT_EXISTED);
        }
        if (datetime <= 0) {
            datetime = System.currentTimeMillis();
        }
        Income income = EntityFactory.createEntity(Income.class);
        income.setCource(course);
        income.setDesc(desc);
        income.setMoney(money);
        income.setOwner(ffeeAccount);
        income.setCreatedTime(datetime);
        income = accessor.save(income, false);
        if (operateLogService != null) {
            operateLogService.writeLog(String.format("记入账户[%s]收入科目[%s]%10.2f。",
                    ffeeAccount.getAccount().getName(), course.getName(), money));
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Record a income item successfully, course: %s, owner: %s, money: %10.2f, datetime: %d.",
                    courseId, ownerId, money, datetime));
        }
        return income;
    }

    /**
     * 获取收入总数
     *
     * @see MoneyManageService#getIncomeTotal()
     */
    @Override
    @Transactional(readOnly = true)
    public double getIncomeTotal() {
        return getIncomeTotal(-1);
    }

    /**
     * 获取指定年度的收入总数
     *
     * @see MoneyManageService#getIncomeTotal(int)
     */
    @Override
    @Transactional(readOnly = true)
    public double getIncomeTotal(int year) {
        return getIncomeTotal(year, -1);
    }

    /**
     * 获取指定年度和月度的收入总数
     *
     * @see MoneyManageService#getIncomeTotal(int, int)
     */
    @Override
    @Transactional(readOnly = true)
    public double getIncomeTotal(int year, int month) {
        DatetimeRange range = new DatetimeRange(year, month);
        return getIncomeTotal(range.start, range.end);
    }

    /**
     * 获取指定时间段的收入总数
     *
     * @see MoneyManageService#getIncomeTotal(long, long)
     */
    @Override
    @Transactional(readOnly = true)
    public double getIncomeTotal(long startDatetime, long endDatetime) {
        double total = incomeRepository.sumIncomeTotalBetween(startDatetime, endDatetime);
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Get income total: %10.2f, year: %d, month: %d.", total, startDatetime,
                    endDatetime));
        }
        return total;
    }

    /**
     * 记入一笔支出信息
     *
     * @see MoneyManageService#spend(String, String, double, String, long)
     */
    @Override
    @Transactional()
    public Spending spend(String courseId, String ownerId, double money, String desc, long datetime) {
        Course course = accessor.getById(courseId, Course.class);
        if (course == null) {
            throw new UserInterfaceFfeeErrorException(UserInterfaceFfeeErrorException.FfeeErrors.COURSE_NOT_EXISTED);
        }
        FfeeAccount ffeeAccount = accessor.getById(ownerId, FfeeAccount.class);
        if (ffeeAccount == null) {
            throw new UserInterfaceFfeeErrorException(UserInterfaceFfeeErrorException.FfeeErrors.ACCOUNT_NOT_EXISTED);
        }
        if (datetime <= 0) {
            datetime = System.currentTimeMillis();
        }
        Spending spending = EntityFactory.createEntity(Spending.class);
        spending.setCource(course);
        spending.setDesc(desc);
        spending.setMoney(money);
        spending.setOwner(ffeeAccount);
        spending.setCreatedTime(datetime);
        spending = accessor.save(spending, false);
        if (operateLogService != null) {
            operateLogService.writeLog(String.format("记入账户[%s]支出科目[%s]%10.2f。",
                    ffeeAccount.getAccount().getName(), course.getName(), money));
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Record a spending item successfully, course: %s, owner: %s, money: %10.2f, datetime: %d.",
                    courseId, ownerId, money, datetime));
        }
        return spending;
    }

    /**
     * 获取所有的支出总数
     *
     * @see MoneyManageService#getSpendingTotal()
     */
    @Override
    @Transactional(readOnly = true)
    public double getSpendingTotal() {
        return getSpendingTotal(-1);
    }

    /**
     * 获取指定年度的支出总数
     *
     * @see MoneyManageService#getSpendingTotal(int)
     */
    @Override
    @Transactional(readOnly = true)
    public double getSpendingTotal(int year) {
        return getSpendingTotal(year, -1);
    }

    /**
     * 获取指定年、月的支出总数
     *
     * @see MoneyManageService#getSpendingTotal(int, int)
     */
    @Override
    @Transactional(readOnly = true)
    public double getSpendingTotal(int year, int month) {
        DatetimeRange range = new DatetimeRange(year, month);
        return getSpendingTotal(range.start, range.end);
    }

    /**
     * 获取指定时间范围的支出总数
     *
     * @see MoneyManageService#getSpendingTotal(long, long)
     */
    @Override
    @Transactional(readOnly = true)
    public double getSpendingTotal(long startDatetime, long endDatetime) {
        double total = spendingRepository.sumSpendingTotalBetween(startDatetime, endDatetime);
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Get spending total: %10.2f, year: %d, month: %d.", total, startDatetime,
                    endDatetime));
        }
        return total;
    }

    private class DatetimeRange {
        long start = -1, end = System.currentTimeMillis();

        DatetimeRange(int year, int month) {
            if (year != -1 && month != -1) {
                LocalDateTime dateTime = LocalDateTime.of(year, month, 1, 0, 0);
                start = dateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
                end = dateTime.with(TemporalAdjusters.lastDayOfMonth()).toInstant(ZoneOffset.of("+8")).toEpochMilli();
            } else if (year != -1) {
                LocalDateTime dateTime = LocalDateTime.of(year, 1, 1, 0, 0);
                start = dateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
                end = dateTime.with(TemporalAdjusters.lastDayOfYear()).toInstant(ZoneOffset.of("+8")).toEpochMilli();
            }
        }
    }
}
