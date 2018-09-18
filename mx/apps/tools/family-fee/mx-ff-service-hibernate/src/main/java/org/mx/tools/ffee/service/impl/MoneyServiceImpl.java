package org.mx.tools.ffee.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.DateUtils;
import org.mx.StringUtils;
import org.mx.dal.service.GeneralAccessor;
import org.mx.error.UserInterfaceSystemErrorException;
import org.mx.tools.ffee.dal.entity.*;
import org.mx.tools.ffee.error.UserInterfaceFfeeErrorException;
import org.mx.tools.ffee.service.MoneyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component("moneyService")
public class MoneyServiceImpl implements MoneyService {
    private static final Log logger = LogFactory.getLog(MoneyServiceImpl.class);

    private GeneralAccessor generalAccessor;

    @Autowired
    public MoneyServiceImpl(@Qualifier("generalAccessor") GeneralAccessor generalAccessor) {
        super();
        this.generalAccessor = generalAccessor;
    }

    @Transactional(readOnly = true)
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
    public Income saveIncome(Income income) {
        return saveMoney(income, Income.class);
    }

    private <T extends MoneyItem> T saveMoney(T moneyItem, Class<T> clazz) {
        // 检查家庭，家庭必须要有
        if (moneyItem.getFamily() == null || StringUtils.isBlank(moneyItem.getFamily().getId())) {
            if (logger.isErrorEnabled()) {
                logger.error("The family is null or the family's id is blank.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        Family family = generalAccessor.getById(moneyItem.getFamily().getId(), Family.class);
        if (family == null) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("The family[%s] not found.", moneyItem.getFamily().getId()));
            }
            throw new UserInterfaceFfeeErrorException(
                    UserInterfaceFfeeErrorException.FfeeErrors.FAMILY_NOT_EXISTED
            );
        }
        // 检查科目，科目必须要有
        if (moneyItem.getCourse() == null || StringUtils.isBlank(moneyItem.getCourse().getId())) {
            if (logger.isErrorEnabled()) {
                logger.error("The course is null or the course's id is blank.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        Course course = generalAccessor.getById(moneyItem.getCourse().getId(), Course.class);
        if (course == null) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("The course[%s] not found.", moneyItem.getCourse().getId()));
            }
            throw new UserInterfaceFfeeErrorException(
                    UserInterfaceFfeeErrorException.FfeeErrors.COURSE_NOT_EXISTED
            );
        }
        // 检查收入所有者，可以为空，表示公共收入
        FfeeAccount owner = null;
        if (moneyItem.getOwner() != null && !StringUtils.isBlank(moneyItem.getOwner().getId())) {
            owner = generalAccessor.getById(moneyItem.getOwner().getId(), FfeeAccount.class);
            if (owner == null) {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("The owner account[%s] not found.", moneyItem.getOwner().getId()));
                }
                throw new UserInterfaceFfeeErrorException(
                        UserInterfaceFfeeErrorException.FfeeErrors.ACCOUNT_NOT_EXISTED
                );
            }
        }
        if (!StringUtils.isBlank(moneyItem.getId())) {
            T checked = generalAccessor.getById(moneyItem.getId(), clazz);
            if (checked != null) {
                checked.setDesc(moneyItem.getDesc());
                checked.setMoney(moneyItem.getMoney());
                checked.setOccurTime(moneyItem.getOccurTime());
                moneyItem = checked;
            } else {
                moneyItem.setId(null);
            }
        }
        moneyItem.setFamily(family);
        moneyItem.setCourse(course);
        moneyItem.setOwner(owner);
        moneyItem = generalAccessor.save(moneyItem);
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Save the %s successfully, family: %s, course: %s, money: %10.2f.",
                    moneyItem instanceof Income ? "income" : "spending", family.getName(), course.getName(),
                    moneyItem.getMoney()));
        }
        return moneyItem;
    }

    @Transactional
    @Override
    public Income deleteIncome(String incomeId) {
        Income income = generalAccessor.remove(incomeId, Income.class);
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Logical remove the income[%s] successfully.", incomeId));
        }
        return income;
    }

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
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
        return generalAccessor.find(GeneralAccessor.ConditionGroup.and(
                GeneralAccessor.ConditionTuple.eq("family", family),
                GeneralAccessor.ConditionTuple.gte("occurTime", range.getLowerLimit()),
                GeneralAccessor.ConditionTuple.lte("occurTime", range.getUpperLimit())
        ), Spending.class);
    }

    @Transactional
    @Override
    public Spending saveSpending(Spending spending) {
        return saveMoney(spending, Spending.class);
    }

    @Transactional
    @Override
    public Spending deleteSpending(String spendingId) {
        Spending spending = generalAccessor.remove(spendingId, Spending.class);
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Logical remove the spending[%s] successfully.", spendingId));
        }
        return spending;
    }
}
