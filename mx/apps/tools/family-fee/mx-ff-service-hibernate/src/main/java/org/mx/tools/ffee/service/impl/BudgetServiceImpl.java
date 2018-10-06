package org.mx.tools.ffee.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.dal.service.GeneralAccessor;
import org.mx.error.UserInterfaceSystemErrorException;
import org.mx.tools.ffee.dal.entity.BudgetItem;
import org.mx.tools.ffee.dal.entity.Course;
import org.mx.tools.ffee.dal.entity.Family;
import org.mx.tools.ffee.error.UserInterfaceFfeeErrorException;
import org.mx.tools.ffee.repository.BudgetRepository;
import org.mx.tools.ffee.service.BudgetService;
import org.mx.tools.ffee.service.MoneyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Component("budgetService")
public class BudgetServiceImpl implements BudgetService {
    private static final Log logger = LogFactory.getLog(BudgetServiceImpl.class);

    private GeneralAccessor generalAccessor;
    private BudgetRepository budgetRepository;

    @Autowired
    public BudgetServiceImpl(@Qualifier("generalAccessor") GeneralAccessor generalAccessor,
                             BudgetRepository budgetRepository) {
        super();
        this.generalAccessor = generalAccessor;
        this.budgetRepository = budgetRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public double getBudgetTotal(String familyId, Integer year) {
        if (StringUtils.isBlank(familyId)) {
            if (logger.isErrorEnabled()) {
                logger.error("The family's id is blank.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        if (year == null || year <= 0) {
            year = Calendar.getInstance().get(Calendar.YEAR);
        }
        return budgetRepository.findBudgetTotalByFamily(familyId, year);
    }

    @Transactional(readOnly = true)
    @Override
    public List<MoneyService.YearMoneyItem> getBudgetTotalByFamily(String familyId) {
        if (StringUtils.isBlank(familyId)) {
            if (logger.isErrorEnabled()) {
                logger.error("The family's id is blank.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        List<Object[]> list = budgetRepository.findBudgetTotalGroupByYearByFamily(familyId);
        List<MoneyService.YearMoneyItem> result = new ArrayList<>();
        if (list != null && !list.isEmpty()) {
            list.forEach(row -> {
                int year = (int) row[0];
                double money = (double) row[1];
                result.add(new MoneyService.YearMoneyItem(year, 1, money, 0, 0, 0));
            });
        }
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public List<BudgetItem> getBudgets(String familyId, int year) {
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
                logger.error(String.format("The family[%s] not found.", family));
            }
            throw new UserInterfaceFfeeErrorException(
                    UserInterfaceFfeeErrorException.FfeeErrors.FAMILY_NOT_EXISTED
            );
        }
        if (year <= 0) {
            return generalAccessor.find(GeneralAccessor.ConditionTuple.eq("family", family), BudgetItem.class);
        } else {
            return generalAccessor.find(GeneralAccessor.ConditionGroup.and(
                    GeneralAccessor.ConditionTuple.eq("family", family),
                    GeneralAccessor.ConditionTuple.eq("year", year)
            ), BudgetItem.class);
        }
    }

    @Transactional
    @Override
    public BudgetItem saveBudget(BudgetItem budgetItem) {
        if (budgetItem == null) {
            if (logger.isErrorEnabled()) {
                logger.error("The budget item is null.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        if (budgetItem.getFamily() == null || StringUtils.isBlank(budgetItem.getFamily().getId())) {
            if (logger.isErrorEnabled()) {
                logger.error("The family is null or the family's id is blank.");
            }
            throw new UserInterfaceFfeeErrorException(
                    UserInterfaceFfeeErrorException.FfeeErrors.FAMILY_NOT_EXISTED
            );
        }
        if (budgetItem.getCourse() == null || StringUtils.isBlank(budgetItem.getCourse().getId())) {
            if (logger.isErrorEnabled()) {
                logger.error("The course is null or the course's id is blank.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        Family family = generalAccessor.getById(budgetItem.getFamily().getId(), Family.class);
        if (family == null) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("The family[%s] not found.", budgetItem.getFamily().getId()));
            }
        }
        Course course = generalAccessor.getById(budgetItem.getCourse().getId(), Course.class);
        if (course == null) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("The course[%s] not found.", budgetItem.getCourse().getId()));
            }
            throw new UserInterfaceFfeeErrorException(
                    UserInterfaceFfeeErrorException.FfeeErrors.COURSE_NOT_EXISTED
            );
        }
        if (!StringUtils.isBlank(budgetItem.getId())) {
            BudgetItem checked = generalAccessor.getById(budgetItem.getId(), BudgetItem.class);
            if (checked != null) {
                // 存在
                checked.setMoney(budgetItem.getMoney());
                checked.setDesc(budgetItem.getDesc());
                budgetItem = checked;
            } else {
                // 不存在
                budgetItem.setId(null);
            }
        }
        budgetItem.setFamily(family);
        budgetItem.setCourse(course);
        budgetItem = generalAccessor.save(budgetItem);
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Save the budget successfully, course: %s, year: %d, money: %10.2f.",
                    course.getCode(), budgetItem.getYear(), budgetItem.getMoney()));
        }
        return budgetItem;
    }

    @Transactional
    @Override
    public BudgetItem deleteBudget(String budgetId) {
        if (StringUtils.isBlank(budgetId)) {
            if (logger.isErrorEnabled()) {
                logger.error("The budget item's id is blank.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        BudgetItem budgetItem = generalAccessor.remove(budgetId, BudgetItem.class);
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Logical delete budget item[%s] successfully.", budgetId));
        }
        return budgetItem;
    }
}
