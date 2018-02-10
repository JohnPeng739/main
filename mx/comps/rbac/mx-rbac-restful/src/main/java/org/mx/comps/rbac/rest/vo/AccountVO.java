package org.mx.comps.rbac.rest.vo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.comps.rbac.dal.entity.Account;
import org.mx.comps.rbac.dal.entity.Role;
import org.mx.comps.rbac.dal.entity.User;
import org.mx.dal.EntityFactory;
import org.mx.service.rest.vo.BaseDictVO;

import java.util.*;

public class AccountVO extends BaseDictVO {
    private static final Log logger = LogFactory.getLog(AccountVO.class);

    private UserVO owner;
    private List<RoleVO> roles;

    public static AccountVO transform(Account account, boolean iterate) {
        if (account == null) {
            return null;
        }
        AccountVO accountVO = new AccountVO();
        BaseDictVO.transform(account, accountVO);
        if (account.getOwner() != null) {
            accountVO.owner = UserVO.transform(account.getOwner());
        }
        Set<Role> roles = account.getRoles();
        if (roles != null && !roles.isEmpty() && iterate) {
            accountVO.roles = RoleVO.transform(roles);
        }
        return accountVO;
    }

    public static List<AccountVO> transform(Collection<Account> accounts) {
        List<AccountVO> accountVOS = new ArrayList<>();
        if (accounts == null || accounts.isEmpty()) {
            return accountVOS;
        }
        accounts.forEach(account -> {
            AccountVO accountVO = AccountVO.transform(account, true);
            if (accountVO != null) {
                accountVOS.add(accountVO);
            }
        });
        return accountVOS;
    }

    public UserVO getOwner() {
        return owner;
    }

    public void setOwner(UserVO owner) {
        this.owner = owner;
    }

    public List<RoleVO> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleVO> roles) {
        this.roles = roles;
    }
}
