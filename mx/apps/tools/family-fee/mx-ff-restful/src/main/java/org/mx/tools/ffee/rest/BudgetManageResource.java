package org.mx.tools.ffee.rest;

import org.mx.dal.session.SessionDataStore;
import org.mx.error.UserInterfaceSystemErrorException;
import org.mx.service.rest.vo.DataVO;
import org.mx.tools.ffee.dal.entity.BudgetItem;
import org.mx.tools.ffee.rest.vo.BudgetInfoVO;
import org.mx.tools.ffee.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("rest/v1")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BudgetManageResource {
    private BudgetService budgetService;
    private SessionDataStore sessionDataStore;

    @Autowired
    public BudgetManageResource(BudgetService budgetService, SessionDataStore sessionDataStore) {
        super();
        this.budgetService = budgetService;
        this.sessionDataStore = sessionDataStore;
    }

    @Path("budgets/families/{familyId}")
    @GET
    public DataVO<List<BudgetItem>> getBudgets(@PathParam("familyId") String familyId,
                                               @QueryParam("year") int year) {
        return new DataVO<>(budgetService.getBudgets(familyId, year));
    }

    @Path("budgets/new")
    @POST
    public DataVO<BudgetItem> newBudget(BudgetInfoVO budgetInfoVO) {
        if (budgetInfoVO == null) {
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        sessionDataStore.setCurrentUserCode(budgetInfoVO.getOpenId());
        BudgetItem budget = budgetInfoVO.get();
        budget.setId(null);
        return new DataVO<>(budgetService.saveBudget(budget));
    }

    @Path("budgets/{budgetId}")
    @PUT
    public DataVO<BudgetItem> modifyBudget(BudgetInfoVO budgetInfoVO) {
        if (budgetInfoVO == null) {
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        sessionDataStore.setCurrentUserCode(budgetInfoVO.getOpenId());
        BudgetItem budget = budgetInfoVO.get();
        return new DataVO<>(budgetService.saveBudget(budget));
    }

    @Path("budgets/{budgetId}")
    @DELETE
    public DataVO<BudgetItem> deleteBudget(@PathParam("budgetId") String budgetId,
                                           @QueryParam("openId") String userCode) {
        sessionDataStore.setCurrentUserCode(userCode);
        return new DataVO<>(budgetService.deleteBudget(budgetId));
    }
}
