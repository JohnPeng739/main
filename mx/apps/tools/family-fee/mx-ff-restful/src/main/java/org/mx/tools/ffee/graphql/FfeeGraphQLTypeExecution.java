package org.mx.tools.ffee.graphql;

import graphql.schema.DataFetchingEnvironment;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.dal.entity.Base;
import org.mx.dal.service.GeneralDictAccessor;
import org.mx.error.UserInterfaceSystemErrorException;
import org.mx.service.rest.graphql.GraphQLFieldListResult;
import org.mx.service.rest.graphql.GraphQLFieldSingleResult;
import org.mx.service.rest.graphql.GraphQLTypeExecution;
import org.mx.tools.ffee.dal.entity.Family;
import org.mx.tools.ffee.dal.entity.Income;
import org.mx.tools.ffee.dal.entity.Spending;
import org.mx.tools.ffee.service.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * 描述： 基于GraphQL实现的类型执行器类定义
 *
 * @author john peng
 * Date time 2018/10/5 下午3:45
 */
@Component("ffeeGraphQLTypeExecution")
public class FfeeGraphQLTypeExecution extends GraphQLTypeExecution implements InitializingBean {
    private static final Log logger = LogFactory.getLog(FfeeGraphQLTypeExecution.class);

    private GeneralDictAccessor dictAccessor;
    private AccountService accountService;
    private CourseService courseService;
    private FamilyService familyService;
    private BudgetService budgetService;
    private MoneyService moneyService;

    @Autowired
    public FfeeGraphQLTypeExecution(@Qualifier("generalDictAccessor") GeneralDictAccessor dictAccessor,
                                    AccountService accountService,
                                    CourseService courseService,
                                    FamilyService familyService,
                                    BudgetService budgetService,
                                    MoneyService moneyService) {
        super("QueryType");
        this.dictAccessor = dictAccessor;
        this.accountService = accountService;
        this.courseService = courseService;
        this.familyService = familyService;
        this.budgetService = budgetService;
        this.moneyService = moneyService;
    }

    @Override
    public void afterPropertiesSet() {
        super.addExecution(new AccountFieldExecution());
        super.addExecution(new CourseFieldExecution());
        super.addExecution(new AccessLogFieldExecution());
        super.addExecution(new BudgetItemFieldExecution());
        super.addExecution(new DefaultFieldExecution<>("family", Family.class));
        super.addExecution(new AccountIncomeFieldExecution());
        super.addExecution(new FamilyIncomeFieldExecution());
        super.addExecution(new AccountSpendingFieldExecution());
        super.addExecution(new FamilySpendingFieldExecution());
        super.addExecution(new AccountMoneySummaryFiledExecution());
        super.addExecution(new FamilyMoneySummaryFiledExecution());
        super.addExecution(new DefaultFieldExecution<>("income", Income.class));
        super.addExecution(new DefaultFieldExecution<>("spending", Spending.class));
    }

    private class AccountMoneySummaryFiledExecution implements GraphQLFieldSingleResult {
        @Override
        public MoneyService.AccountMoneySummary executeForSingle(DataFetchingEnvironment environment) {
            String accountId = environment.getArgument("accountId");
            if (StringUtils.isBlank(accountId)) {
                if (logger.isErrorEnabled()) {
                    logger.error("The account's id is blank.");
                }
                throw new UserInterfaceSystemErrorException(
                        UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
                );
            }
            return moneyService.getAccountMoneySummary(accountId);
        }

        @Override
        public String getFieldName() {
            return "accountMoneySummary";
        }
    }

    private class FamilyMoneySummaryFiledExecution implements GraphQLFieldSingleResult {
        @Override
        public MoneyService.FamilyMoneySummary executeForSingle(DataFetchingEnvironment environment) {
            String familyId = environment.getArgument("familyId");
            if (StringUtils.isBlank(familyId)) {
                if (logger.isErrorEnabled()) {
                    logger.error("The family's id is blank.");
                }
                throw new UserInterfaceSystemErrorException(
                        UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
                );
            }
            return moneyService.getFamilyMoneySummary(familyId);
        }

        @Override
        public String getFieldName() {
            return "accountMoneySummary";
        }
    }

    private class AccountIncomeFieldExecution implements GraphQLFieldListResult {
        @Override
        public List<? extends Income> executeForList(DataFetchingEnvironment environment) {
            Map<String, Object> input = environment.getArgument("input");
            String accountId = (String) input.get("accountId");
            if (StringUtils.isBlank(accountId)) {
                if (logger.isErrorEnabled()) {
                    logger.error("The account's id is blank.");
                }
                throw new UserInterfaceSystemErrorException(
                        UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
                );
            }
            Integer year = (Integer) input.get("year");
            Integer month = (Integer) input.get("month");
            Integer week = (Integer) input.get("week");
            return moneyService.getAccountIncomes(accountId, year, month, week);
        }

        @Override
        public String getFieldName() {
            return "accountIncome";
        }
    }

    private class FamilyIncomeFieldExecution implements GraphQLFieldListResult {
        @Override
        public List<? extends Income> executeForList(DataFetchingEnvironment environment) {
            Map<String, Object> input = environment.getArgument("input");
            String familyId = (String) input.get("familyId");
            if (StringUtils.isBlank(familyId)) {
                if (logger.isErrorEnabled()) {
                    logger.error("The family's id is blank.");
                }
                throw new UserInterfaceSystemErrorException(
                        UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
                );
            }
            Integer year = (Integer) input.get("year");
            Integer month = (Integer) input.get("month");
            Integer week = (Integer) input.get("week");
            return moneyService.getFamilyIncomes(familyId, year, month, week);
        }

        @Override
        public String getFieldName() {
            return "accountIncome";
        }
    }

    private class AccountSpendingFieldExecution implements GraphQLFieldListResult {
        @Override
        public List<? extends Spending> executeForList(DataFetchingEnvironment environment) {
            Map<String, Object> input = environment.getArgument("input");
            String accountId = (String) input.get("accountId");
            if (StringUtils.isBlank(accountId)) {
                if (logger.isErrorEnabled()) {
                    logger.error("The account's id is blank.");
                }
                throw new UserInterfaceSystemErrorException(
                        UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
                );
            }
            Integer year = (Integer) input.get("year");
            Integer month = (Integer) input.get("month");
            Integer week = (Integer) input.get("week");
            return moneyService.getAccountSpendings(accountId, year, month, week);
        }

        @Override
        public String getFieldName() {
            return "accountSpending";
        }
    }

    private class FamilySpendingFieldExecution implements GraphQLFieldListResult {
        @Override
        public List<? extends Spending> executeForList(DataFetchingEnvironment environment) {
            Map<String, Object> input = environment.getArgument("input");
            String familyId = (String) input.get("familyId");
            if (StringUtils.isBlank(familyId)) {
                if (logger.isErrorEnabled()) {
                    logger.error("The family's id is blank.");
                }
                throw new UserInterfaceSystemErrorException(
                        UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
                );
            }
            Integer year = (Integer) input.get("year");
            Integer month = (Integer) input.get("month");
            Integer week = (Integer) input.get("week");
            return moneyService.getFamilySpendings(familyId, year, month, week);
        }

        @Override
        public String getFieldName() {
            return "accountSpending";
        }
    }

    private class AccountFieldExecution implements GraphQLFieldSingleResult {
        @Override
        public Object executeForSingle(DataFetchingEnvironment environment) {
            Map<String, Object> input = environment.getArgument("input");
            if (input.containsKey("id")) {
                return accountService.getAccountSummaryById((String) input.get("id"));
            } else if (input.containsKey("openId")) {
                return accountService.getAccountSummaryByOpenId((String) input.get("openId"));
            }
            if (logger.isErrorEnabled()) {
                logger.error("The account'id or openId is blank.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }

        @Override
        public String getFieldName() {
            return "account";
        }
    }

    private class CourseFieldExecution implements GraphQLFieldSingleResult, GraphQLFieldListResult {
        @Override
        public List executeForList(DataFetchingEnvironment environment) {
            String familyId = environment.getArgument("familyId");
            // 如果familyId为blank，则返回所有公共科目
            return courseService.getAllCoursesByFamily(familyId);
        }

        @Override
        public Object executeForSingle(DataFetchingEnvironment environment) {
            String id = environment.getArgument("id");
            if (StringUtils.isBlank(id)) {
                if (logger.isErrorEnabled()) {
                    logger.error("The course's id is blank.");
                }
                throw new UserInterfaceSystemErrorException(
                        UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
                );
            }
            return courseService.getCourse(id);
        }

        @Override
        public String getFieldName() {
            return "course";
        }
    }

    private class AccessLogFieldExecution implements GraphQLFieldListResult {
        @Override
        public List executeForList(DataFetchingEnvironment environment) {
            Map<String, Object> input = environment.getArgument("input");
            String accountId = (String) input.get("accountId"), familyId = (String) input.get("familyId");
            if (!StringUtils.isBlank(accountId)) {
                return accountService.getLogsByAccount(accountId);
            } else if (!StringUtils.isBlank(familyId)) {
                return familyService.getAccessLogsByFamilyId(familyId);
            }
            if (logger.isErrorEnabled()) {
                logger.error("The account'id or family's id is blank.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }

        @Override
        public String getFieldName() {
            return "accessLog";
        }
    }

    private class BudgetItemFieldExecution implements GraphQLFieldSingleResult, GraphQLFieldListResult {
        @Override
        public List executeForList(DataFetchingEnvironment environment) {
            Map<String, Object> input = environment.getArgument("input");
            String familyId = (String) input.get("familyId");
            Integer year = (Integer) input.get("year");
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
            return budgetService.getBudgets(familyId, year);
        }

        @Override
        public Object executeForSingle(DataFetchingEnvironment environment) {
            String id = environment.getArgument("id");
            return dictAccessor.getById(id, org.mx.tools.ffee.dal.entity.BudgetItem.class);
        }

        @Override
        public String getFieldName() {
            return "budgetItem";
        }
    }

    private class DefaultFieldExecution<T extends Base> implements GraphQLFieldSingleResult<T>, GraphQLFieldListResult<T> {
        private String fileName;
        private Class<T> clazz;

        private DefaultFieldExecution(String fieldName, Class<T> clazz) {
            super();
            this.fileName = fieldName;
            this.clazz = clazz;
        }

        @Override
        public List<T> executeForList(DataFetchingEnvironment environment) {
            return dictAccessor.list(clazz);
        }

        @Override
        public T executeForSingle(DataFetchingEnvironment environment) {
            String id = environment.getArgument("id");
            if (StringUtils.isBlank(id)) {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("The %s's id is blank.", clazz.getName()));
                }
                throw new UserInterfaceSystemErrorException(
                        UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
                );
            }
            return dictAccessor.getById(id, clazz);
        }

        @Override
        public String getFieldName() {
            return fileName;
        }
    }
}
