package org.mx.tools.ffee.rest;

import org.mx.StringUtils;
import org.mx.dal.session.SessionDataStore;
import org.mx.error.UserInterfaceSystemErrorException;
import org.mx.service.rest.vo.DataVO;
import org.mx.tools.ffee.dal.entity.Income;
import org.mx.tools.ffee.dal.entity.Spending;
import org.mx.tools.ffee.rest.vo.MoneyItemInfoVO;
import org.mx.tools.ffee.service.MoneyService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("rest/v1")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MoneyManageResource {
    private MoneyService moneyService;
    private SessionDataStore sessionDataStore;

    @Autowired
    public MoneyManageResource(MoneyService moneyService, SessionDataStore sessionDataStore) {
        super();
        this.moneyService = moneyService;
        this.sessionDataStore = sessionDataStore;
    }

    @Path("incomes/families/{familyId}")
    @GET
    public DataVO<List<Income>> getAllIncomes(@PathParam("familyId") String familyId, @QueryParam("userCode") String userCode,
                                              @QueryParam("year") int year) {
        return new DataVO<>(moneyService.getIncomes(familyId, year));
    }

    @Path("incomes/new")
    @POST
    public DataVO<Income> newIncome(MoneyItemInfoVO moneyItemInfoVO) {
        if (moneyItemInfoVO == null) {
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        sessionDataStore.setCurrentUserCode(moneyItemInfoVO.getOpenId());
        Income income = moneyItemInfoVO.getIncome();
        income.setId(null);
        return new DataVO<>(moneyService.saveIncome(income));
    }

    @Path("incomes/{incomeId}")
    @PUT
    public DataVO<Income> modifyIncome(MoneyItemInfoVO moneyItemInfoVO,
                                       @PathParam("incomeId") String incomeId) {
        if (moneyItemInfoVO == null) {
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        sessionDataStore.setCurrentUserCode(moneyItemInfoVO.getOpenId());
        Income income = moneyItemInfoVO.getIncome();
        income.setId(incomeId);
        return new DataVO<>(moneyService.saveIncome(income));
    }

    @Path("incomes/{incomeId}")
    @DELETE
    public DataVO<Income> deleteIncome(@PathParam("incomeId") String incomeId,
                                       @QueryParam("userCode") String userCode) {
        sessionDataStore.setCurrentUserCode(userCode);
        return new DataVO<>(moneyService.deleteIncome(incomeId));
    }

    @Path("spendings/families/{familyId}")
    @GET
    public DataVO<List<Spending>> getAllSpendings(@PathParam("familyId") String familyId,
                                                  @QueryParam("userCode") String userCode,
                                                  @QueryParam("year") int year,
                                                  @QueryParam("month") int month,
                                                  @QueryParam("week") String week) {
        if (!StringUtils.isBlank(week)) {
            return new DataVO<>(moneyService.getSpendingsLastWeek(familyId));
        } else {
            return new DataVO<>(moneyService.getSpendings(familyId, year, month));
        }
    }

    @Path("spendings/new")
    @POST
    public DataVO<Spending> newSpending(MoneyItemInfoVO moneyItemInfoVO) {
        if (moneyItemInfoVO == null) {
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        sessionDataStore.setCurrentUserCode(moneyItemInfoVO.getOpenId());
        Spending spending = moneyItemInfoVO.getSpending();
        spending.setId(null);
        return new DataVO<>(moneyService.saveSpending(spending));
    }

    @Path("spendings/{spendingId}")
    @PUT
    public DataVO<Spending> modifySpending(MoneyItemInfoVO moneyItemInfoVO,
                                           @PathParam("spendingId") String spendingId) {
        if (moneyItemInfoVO == null) {
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        sessionDataStore.setCurrentUserCode(moneyItemInfoVO.getOpenId());
        Spending spending = moneyItemInfoVO.getSpending();
        spending.setId(spendingId);
        return new DataVO<>(moneyService.saveSpending(spending));
    }

    @Path("spendings/{spendingId}")
    @DELETE
    public DataVO<Spending> deleteSpending(@PathParam("spendingId") String spendingId,
                                           @QueryParam("userCode") String userCode) {
        sessionDataStore.setCurrentUserCode(userCode);
        return new DataVO<>(moneyService.deleteSpending(spendingId));
    }
}
