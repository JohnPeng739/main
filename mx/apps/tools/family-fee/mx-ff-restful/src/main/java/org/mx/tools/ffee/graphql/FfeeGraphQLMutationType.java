package org.mx.tools.ffee.graphql;

import graphql.schema.DataFetchingEnvironment;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.error.UserInterfaceSystemErrorException;
import org.mx.service.rest.graphql.GraphQLFieldSingleResult;
import org.mx.service.rest.graphql.GraphQLTypeExecution;
import org.mx.service.rest.graphql.GraphQLUtils;
import org.mx.tools.ffee.dal.entity.Income;
import org.mx.tools.ffee.dal.entity.MoneyItem;
import org.mx.tools.ffee.dal.entity.Spending;
import org.mx.tools.ffee.service.*;
import org.mx.tools.ffee.service.bean.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("ffeeGraphQLMutationType")
public class FfeeGraphQLMutationType extends GraphQLTypeExecution implements InitializingBean {
    private static final Log logger = LogFactory.getLog(FfeeGraphQLMutationType.class);

    private AccountService accountService;
    private CourseService courseService;
    private FamilyService familyService;
    private BudgetService budgetService;
    private MoneyService moneyService;

    @Autowired
    public FfeeGraphQLMutationType(AccountService accountService,
                                   CourseService courseService,
                                   FamilyService familyService,
                                   BudgetService budgetService,
                                   MoneyService moneyService) {
        super("MutationType");
        this.accountService = accountService;
        this.courseService = courseService;
        this.familyService = familyService;
        this.budgetService = budgetService;
        this.moneyService = moneyService;
    }

    @Override
    public void afterPropertiesSet() {
        super.addExecution(new AccountFieldExecution());
        super.addExecution(new BudgetItemFieldExecution());
        super.addExecution(new CourseFieldExecution());
        super.addExecution(new FamilyFieldExecution());
        super.addExecution(new FamilyMemberFieldExecution());
        super.addExecution(new MoneyFieldExecution("saveIncome", Income.class));
        super.addExecution(new MoneyFieldExecution("saveSpending", Spending.class));
    }

    private enum OperateType {
        SAVE, DELETE, REGISTRY, JOIN_FAMILY
    }

    private class AccountFieldExecution implements GraphQLFieldSingleResult {
        @Override
        public AccountService.AccountSummary executeForSingle(DataFetchingEnvironment environment) {
            OperateType op = OperateType.valueOf(environment.getArgument("op"));
            Map<String, Object> input = environment.getArgument("input");
            AccountInfoBean accountInfoBean = GraphQLUtils.parse(input, AccountInfoBean.class);
            if (op == OperateType.REGISTRY) {
                return accountService.registry(accountInfoBean);
            } else if (op == OperateType.SAVE) {
                return accountService.saveAccount(accountInfoBean);
            } else {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Unsupported operate type: %s.", op));
                }
                throw new UserInterfaceSystemErrorException(
                        UserInterfaceSystemErrorException.SystemErrors.SYSTEM_UNSUPPORTED
                );
            }
        }

        @Override
        public String getFieldName() {
            return "saveAccount";
        }
    }

    private class BudgetItemFieldExecution implements GraphQLFieldSingleResult {
        @Override
        public Object executeForSingle(DataFetchingEnvironment environment) {
            OperateType op = OperateType.valueOf(environment.getArgument("op"));
            Map<String, Object> input = environment.getArgument("input");
            BudgetItemInfoBean budgetItemInfoBean = GraphQLUtils.parse(input, BudgetItemInfoBean.class);
            if (op == OperateType.DELETE) {
                return budgetService.deleteBudget(budgetItemInfoBean);
            } else if (op == OperateType.SAVE) {
                return budgetService.saveBudget(budgetItemInfoBean);
            } else {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Unsupported operate type: %s.", op));
                }
                throw new UserInterfaceSystemErrorException(
                        UserInterfaceSystemErrorException.SystemErrors.SYSTEM_UNSUPPORTED
                );
            }
        }

        @Override
        public String getFieldName() {
            return "saveBudgetItem";
        }
    }

    private class CourseFieldExecution implements GraphQLFieldSingleResult {
        @Override
        public Object executeForSingle(DataFetchingEnvironment environment) {
            OperateType op = OperateType.valueOf(environment.getArgument("op"));
            Map<String, Object> input = environment.getArgument("input");
            CourseInfoBean courseInfoBean = GraphQLUtils.parse(input, CourseInfoBean.class);
            if (op == OperateType.DELETE) {
                return courseService.deleteCourse(courseInfoBean);
            } else if (op == OperateType.SAVE) {
                return courseService.saveCourse(courseInfoBean);
            } else {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Unsupported operate type: %s.", op));
                }
                throw new UserInterfaceSystemErrorException(
                        UserInterfaceSystemErrorException.SystemErrors.SYSTEM_UNSUPPORTED
                );
            }
        }

        @Override
        public String getFieldName() {
            return "saveCourse";
        }
    }

    private class FamilyFieldExecution implements GraphQLFieldSingleResult {
        @Override
        public Object executeForSingle(DataFetchingEnvironment environment) {
            Map<String, Object> input = environment.getArgument("input");
            FamilyInfoBean familyInfoBean = GraphQLUtils.parse(input, FamilyInfoBean.class);
            return familyService.saveFamily(familyInfoBean);
        }

        @Override
        public String getFieldName() {
            return "saveFamily";
        }
    }

    private class FamilyMemberFieldExecution implements GraphQLFieldSingleResult {
        @Override
        public Object executeForSingle(DataFetchingEnvironment environment) {
            Map<String, Object> input = environment.getArgument("input");
            FamilyMemberInfoBean familyMemberInfoBean = GraphQLUtils.parse(input, FamilyMemberInfoBean.class);
            return familyService.saveFamilyMember(familyMemberInfoBean);
        }

        @Override
        public String getFieldName() {
            return "saveFamilyMember";
        }
    }

    private class MoneyFieldExecution implements GraphQLFieldSingleResult {
        private String fieldName;
        private Class<? extends MoneyItem> clazz;

        public MoneyFieldExecution(String fieldName, Class<? extends MoneyItem> clazz) {
            super();
            this.fieldName = fieldName;
            this.clazz = clazz;
        }

        @Override
        public Object executeForSingle(DataFetchingEnvironment environment) {
            OperateType op = OperateType.valueOf(environment.getArgument("op"));
            Map<String, Object> input = environment.getArgument("input");
            MoneyInfoBean moneyInfoBean = GraphQLUtils.parse(input, MoneyInfoBean.class);
            if (op == OperateType.DELETE) {
                return moneyService.deleteMoney(moneyInfoBean, clazz);
            } else if (op == OperateType.SAVE) {
                return moneyService.saveMoney(moneyInfoBean, clazz);
            } else {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Unsupported operate type: %s.", op));
                }
                throw new UserInterfaceSystemErrorException(
                        UserInterfaceSystemErrorException.SystemErrors.SYSTEM_UNSUPPORTED
                );
            }
        }

        @Override
        public String getFieldName() {
            return fieldName;
        }
    }
}
