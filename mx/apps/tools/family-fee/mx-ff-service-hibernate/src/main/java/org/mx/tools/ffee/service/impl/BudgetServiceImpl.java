package org.mx.tools.ffee.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.dal.EntityFactory;
import org.mx.dal.service.GeneralAccessor;
import org.mx.error.UserInterfaceSystemErrorException;
import org.mx.tools.ffee.dal.entity.BudgetItem;
import org.mx.tools.ffee.dal.entity.Course;
import org.mx.tools.ffee.dal.entity.Family;
import org.mx.tools.ffee.error.UserInterfaceFfeeErrorException;
import org.mx.tools.ffee.repository.BudgetRepository;
import org.mx.tools.ffee.service.AccountService;
import org.mx.tools.ffee.service.BudgetService;
import org.mx.tools.ffee.service.MoneyService;
import org.mx.tools.ffee.service.bean.BudgetItemInfoBean;
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
    private AccountService accountService;

    @Autowired
    public BudgetServiceImpl(@Qualifier("generalAccessor") GeneralAccessor generalAccessor,
                             BudgetRepository budgetRepository,
                             AccountService accountService) {
        super();
        this.generalAccessor = generalAccessor;
        this.budgetRepository = budgetRepository;
        this.accountService = accountService;
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
        Family family = generalAccessor.getById(familyId, Family.class);
        if (family == null) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("The family[%s] not found.", familyId));
            }
            throw new UserInterfaceFfeeErrorException(
                    UserInterfaceFfeeErrorException.FfeeErrors.FAMILY_NOT_EXISTED
            );
        }
        accountService.writeAccessLog(String.format("获取%s家庭的预算数据。", family.getName()));
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
        Family family = generalAccessor.getById(familyId, Family.class);
        if (family == null) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("The family[%s] not found.", familyId));
            }
            throw new UserInterfaceFfeeErrorException(
                    UserInterfaceFfeeErrorException.FfeeErrors.FAMILY_NOT_EXISTED
            );
        }
        accountService.writeAccessLog(String.format("获取%s家庭的预算数据。", family.getName()));
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
                logger.error(String.format("The family[%s] not found.", familyId));
            }
            throw new UserInterfaceFfeeErrorException(
                    UserInterfaceFfeeErrorException.FfeeErrors.FAMILY_NOT_EXISTED
            );
        }
        accountService.writeAccessLog(String.format("获取%s家庭的预算数据。", family.getName()));
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
    public BudgetItem saveBudget(BudgetItemInfoBean budgetItemInfoBean) {
        if (budgetItemInfoBean == null) {
            if (logger.isErrorEnabled()) {
                logger.error("The budget item is null.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        String familyId = budgetItemInfoBean.getFamilyId();
        if (StringUtils.isBlank(familyId)) {
            if (logger.isErrorEnabled()) {
                logger.error("The family is null or the family's id is blank.");
            }
            throw new UserInterfaceFfeeErrorException(
                    UserInterfaceFfeeErrorException.FfeeErrors.FAMILY_NOT_EXISTED
            );
        }
        String courseId = budgetItemInfoBean.getCourseId();
        if (StringUtils.isBlank(courseId)) {
            if (logger.isErrorEnabled()) {
                logger.error("The course is null or the course's id is blank.");
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
        Course course = generalAccessor.getById(courseId, Course.class);
        if (course == null) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("The course[%s] not found.", courseId));
            }
            throw new UserInterfaceFfeeErrorException(
                    UserInterfaceFfeeErrorException.FfeeErrors.COURSE_NOT_EXISTED
            );
        }
        String id = budgetItemInfoBean.getId();
        BudgetItem saved = null;
        if (!StringUtils.isBlank(id)) {
            BudgetItem checked = generalAccessor.getById(id, BudgetItem.class);
            if (checked != null) {
                // 存在
                saved = checked;
            }
        }
        if (saved == null) {
            saved = EntityFactory.createEntity(BudgetItem.class);
        }
        saved.setMoney(budgetItemInfoBean.getMoney());
        saved.setDesc(budgetItemInfoBean.getDesc());
        saved.setFamily(family);
        saved.setCourse(course);
        saved = generalAccessor.save(saved);
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Save the budget successfully, course: %s, year: %d, money: %10.2f.",
                    course.getCode(), saved.getYear(), saved.getMoney()));
        }
        accountService.writeAccessLog(String.format("修改%s家庭的预算数据。", family.getName()));
        return saved;
    }

    @Transactional
    @Override
    public BudgetItem deleteBudget(BudgetItemInfoBean budgetItemInfoBean) {
        if (budgetItemInfoBean == null) {
            if (logger.isErrorEnabled()) {
                logger.error("The budget item info is null.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        String budgetId = budgetItemInfoBean.getId();
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
        accountService.writeAccessLog(String.format("删除%s家庭的预算数据。", budgetItem.getFamily().getName()));
        return budgetItem;
    }
}
