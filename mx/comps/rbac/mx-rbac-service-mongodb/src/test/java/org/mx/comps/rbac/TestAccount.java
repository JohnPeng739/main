package org.mx.comps.rbac;

import org.junit.Test;
import org.mx.DigestUtils;
import org.mx.comps.rbac.dal.entity.Account;
import org.mx.comps.rbac.dal.entity.LoginHistory;
import org.mx.comps.rbac.dal.entity.Role;
import org.mx.comps.rbac.dal.entity.User;
import org.mx.comps.rbac.error.UserInterfaceRbacErrorException;
import org.mx.comps.rbac.service.AccountManageService;
import org.mx.comps.rbac.service.RoleManageService;
import org.mx.comps.rbac.service.UserManageService;
import org.mx.dal.service.GeneralDictAccessor;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;

/**
 * 账户单元测试类
 *
 * @author : john.peng created on date : 2017/11/28
 */
public class TestAccount extends BaseTest {
    public static String account1Id, account2Id, account3Id;

    public static void testInsertAccount(GeneralDictAccessor service, AccountManageService accountService) throws NoSuchAlgorithmException {
        User john = service.getById(TestUser.johnId, User.class);
        assertNotNull(john);
        AccountManageService.AccountInfo accountInfo = AccountManageService.AccountInfo.valueOf("account1",
                "password", "description account.", "", TestUser.johnId, Arrays.asList(), true);
        Account account1 = accountService.saveAccount(accountInfo);
        assertEquals(1, service.count(Account.class));
        assertNotNull(account1);
        assertNotNull(account1.getId());
        account1Id = account1.getId();
        assertEquals("description account.", account1.getDesc());
        assertEquals("account1", account1.getCode());
        assertEquals(john.getFullName(), account1.getName());
        assertEquals(DigestUtils.md5("password"), account1.getPassword());
        assertEquals("admin", account1.getOperator());
        assertTrue(account1.isValid());
        assertTrue(account1.getCreatedTime() > 0);
        assertTrue(account1.getUpdatedTime() > 0);
        account1 = service.getById(account1Id, Account.class);
        assertNotNull(account1);
        assertNotNull(account1.getId());
        assertEquals("description account.", account1.getDesc());
        assertEquals("account1", account1.getCode());
        assertEquals(john.getFullName(), account1.getName());
        assertEquals(DigestUtils.md5("password"), account1.getPassword());
        assertEquals("admin", account1.getOperator());
        account1 = service.getByCode(account1.getCode(), Account.class);
        assertNotNull(account1);
        assertNotNull(account1.getId());
        assertEquals("description account.", account1.getDesc());
        assertEquals("account1", account1.getCode());
        assertEquals(john.getFullName(), account1.getName());
        assertEquals(DigestUtils.md5("password"), account1.getPassword());
        assertEquals("admin", account1.getOperator());
        assertEquals(john, account1.getOwner());
        assertTrue(account1.isValid());

        accountInfo = AccountManageService.AccountInfo.valueOf("account2",
                "", "description account.", "", TestUser.johnId, Arrays.asList(), true);
        Account account2 = accountService.saveAccount(accountInfo);
        assertEquals(2, service.count(Account.class));
        assertNotNull(account2);
        assertNotNull(account2.getId());
        account2Id = account2.getId();
        assertEquals("account2", account2.getCode());
        assertEquals(DigestUtils.md5("ds110119"), account2.getPassword());
        assertEquals(john.getFullName(), account2.getName());
        assertEquals(john, account2.getOwner());
        assertTrue(account2.isValid());
    }

    public static void testEditAccount(GeneralDictAccessor service, AccountManageService accountService) {
        User john = service.getById(TestUser.johnId, User.class);
        assertNotNull(john);
        AccountManageService.AccountInfo accountInfo = AccountManageService.AccountInfo.valueOf("account3",
                "", "description account.", "", TestUser.johnId, Arrays.asList(), true);
        Account account3 = accountService.saveAccount(accountInfo);
        assertEquals(3, service.count(Account.class));
        assertNotNull(account3);
        assertNotNull(account3.getId());
        account3Id = account3.getId();
        assertEquals("description account.", account3.getDesc());
        assertEquals(john.getFullName(), account3.getName());
        assertEquals(john, account3.getOwner());

        accountInfo = AccountManageService.AccountInfo.valueOf(account3.getCode(), "",
                "new desc.", account3.getId(), TestUser.johnId, Arrays.asList(), false);
        account3 = accountService.saveAccount(accountInfo);
        assertEquals(2, service.count(Account.class));
        assertEquals(2, service.count(Account.class, true));
        assertEquals(3, service.count(Account.class, false));
        assertNotNull(account3);
        assertEquals("new desc.", account3.getDesc());
        assertFalse(account3.isValid());
        account3 = service.getByCode("account3", Account.class);
        assertNotNull(account3);
        assertEquals("new desc.", account3.getDesc());
        assertFalse(account3.isValid());

        accountInfo = AccountManageService.AccountInfo.valueOf(account3.getCode(), "",
                account3.getDesc(), account3.getId(), TestUser.johnId, Arrays.asList(), true);
        accountService.saveAccount(accountInfo);
        assertEquals(3, service.count(Account.class));
        assertEquals(3, service.count(Account.class, true));
        assertEquals(3, service.count(Account.class, false));
        account3 = service.getById(account3Id, Account.class);
        assertNotNull(account3);
        assertTrue(account3.isValid());
    }

    @Test
    public void testAccountCrud() {
        GeneralDictAccessor service = context.getBean("generalDictAccessor", GeneralDictAccessor.class);
        assertNotNull(service);
        AccountManageService accountService = context.getBean("accountManageService", AccountManageService.class);
        assertNotNull(service);
        UserManageService userManageService = context.getBean("userManageService", UserManageService.class);
        assertNotNull(userManageService);

        try {
            TestUser.testInsertUser(service, userManageService);
            TestUser.testEditUser(service, userManageService);

            assertEquals(0, service.count(Account.class));
            // insert
            testInsertAccount(service, accountService);
            // edit
            testEditAccount(service, accountService);
            // delete
            testDeleteAccount(service);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }

    @Test
    public void testAccountRoles() {
        GeneralDictAccessor service = context.getBean("generalDictAccessor", GeneralDictAccessor.class);
        assertNotNull(service);
        AccountManageService accountService = context.getBean("accountManageService", AccountManageService.class);
        assertNotNull(service);
        UserManageService userManageService = context.getBean("userManageService", UserManageService.class);
        assertNotNull(userManageService);
        RoleManageService roleManageService = context.getBean("roleManageService", RoleManageService.class);
        assertNotNull(roleManageService);

        try {
            TestUser.testInsertUser(service, userManageService);
            TestUser.testEditUser(service, userManageService);
            assertEquals(3, service.count(User.class));
            TestRole.testInsertRole(service, roleManageService);
            TestRole.testEditRole(service, roleManageService);
            testInsertAccount(service, accountService);
            testEditAccount(service, accountService);
            assertEquals(3, service.count(Role.class));
            assertEquals(3, service.count(Account.class));
            Account account1 = service.getById(account1Id, Account.class);
            assertNotNull(account1);
            assertTrue(account1.getRoles().isEmpty());
            Role role1 = service.getById(TestRole.role1Id, Role.class);
            Role role2 = service.getById(TestRole.role2Id, Role.class);
            Role role3 = service.getById(TestRole.role3Id, Role.class);
            assertNotNull(role1);
            assertNotNull(role2);
            assertNotNull(role3);

            AccountManageService.AccountInfo accountInfo = AccountManageService.AccountInfo.valueOf(account1.getCode(),
                    "", account1.getDesc(), account1.getId(), TestUser.johnId,
                    Arrays.asList(role1.getId(), role2.getId(), role3.getId()), account1.isValid());
            accountService.saveAccount(accountInfo);
            assertEquals(3, service.count(Account.class));
            account1 = service.getById(account1Id, Account.class);
            assertNotNull(account1);
            assertEquals(3, account1.getRoles().size());
            assertEquals(new HashSet<>(Arrays.asList(role1, role2, role3)), account1.getRoles());

            accountInfo = AccountManageService.AccountInfo.valueOf(account1.getCode(),
                    "", account1.getDesc(), account1.getId(), TestUser.johnId,
                    Arrays.asList(role1.getId(), role3.getId()), account1.isValid());
            accountService.saveAccount(accountInfo);
            assertEquals(3, service.count(Account.class));
            account1 = service.getById(account1Id, Account.class);
            assertNotNull(account1);
            assertEquals(2, account1.getRoles().size());
            assertEquals(new HashSet<>(Arrays.asList(role1, role3)), account1.getRoles());

            accountInfo = AccountManageService.AccountInfo.valueOf(account1.getCode(),
                    "", account1.getDesc(), account1.getId(), TestUser.johnId,
                    Arrays.asList(), account1.isValid());
            accountService.saveAccount(accountInfo);
            account1 = service.getById(account1Id, Account.class);
            assertNotNull(account1);
            assertTrue(account1.getRoles().isEmpty());
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }

    @Test
    public void testChangePassword() {
        GeneralDictAccessor service = context.getBean("generalDictAccessor", GeneralDictAccessor.class);
        assertNotNull(service);
        AccountManageService accountService = context.getBean("accountManageService", AccountManageService.class);
        assertNotNull(service);
        UserManageService userManageService = context.getBean("userManageService", UserManageService.class);
        assertNotNull(userManageService);

        try {
            TestUser.testInsertUser(service, userManageService);
            TestUser.testEditUser(service, userManageService);
            assertEquals(3, service.count(User.class));
            testInsertAccount(service, accountService);
            testEditAccount(service, accountService);
            assertEquals(3, service.count(Account.class));

            Account account1 = service.getById(account1Id, Account.class);
            assertNotNull(account1);
            assertEquals(DigestUtils.md5("password"), account1.getPassword());
            accountService.changePassword(account1.getId(), "password", "new password");
            account1 = service.getById(account1Id, Account.class);
            assertNotNull(account1);
            assertEquals(DigestUtils.md5("new password"), account1.getPassword());

            Account account3 = service.getById(account3Id, Account.class);
            assertNotNull(account3);
            assertEquals(DigestUtils.md5("ds110119"), account3.getPassword());
            accountService.changePassword(account3.getId(), "ds110119", "new password");
            account3 = service.getById(account3Id, Account.class);
            assertNotNull(account3);
            assertEquals(DigestUtils.md5("new password"), account3.getPassword());
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }

    @Test
    public void testLoginAndLogout() {
        GeneralDictAccessor service = context.getBean("generalDictAccessor", GeneralDictAccessor.class);
        assertNotNull(service);
        AccountManageService accountService = context.getBean("accountManageService", AccountManageService.class);
        assertNotNull(service);
        UserManageService userManageService = context.getBean("userManageService", UserManageService.class);
        assertNotNull(userManageService);

        try {
            TestUser.testInsertUser(service, userManageService);
            TestUser.testEditUser(service, userManageService);
            assertEquals(3, service.count(User.class));
            testInsertAccount(service, accountService);
            testEditAccount(service, accountService);
            assertEquals(3, service.count(Account.class));

            // 测试正常流程
            LoginHistory login = accountService.login("account1", "password", false);
            assertNotNull(login);
            Account account1 = service.getById(account1Id, Account.class);
            assertNotNull(account1);
            assertEquals(account1, login.getAccount());
            // 测试用户重复登录
            login = accountService.login("account1", "password", true);
            assertNotNull(login);
            assertEquals(account1, login.getAccount());
            try {
                accountService.login("account1", "password", false);
            } catch (UserInterfaceRbacErrorException ex) {
                assertEquals(UserInterfaceRbacErrorException.RbacErrors.ACCOUNT_ALREADY_LOGINED.getErrorCode(), ex.getErrorCode());
            }
            // 测试正常登出
            login = accountService.logout(account1.getId());
            assertNotNull(login);
            assertEquals(account1, login.getAccount());

            // 测试用户不存在
            try {
                accountService.login("abc", "adasd", false);
            } catch (UserInterfaceRbacErrorException ex) {
                assertEquals(UserInterfaceRbacErrorException.RbacErrors.ACCOUNT_NOT_FOUND.getErrorMessage(), ex.getErrorMessage());
            }

            // 测试密码不正确
            try {
                accountService.login("account1", "adfasd", false);
            } catch (UserInterfaceRbacErrorException ex) {
                assertEquals(UserInterfaceRbacErrorException.RbacErrors.ACCOUNT_PASSWORD_NOT_MATCHED.getErrorMessage(), ex.getErrorMessage());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }

    private void testDeleteAccount(GeneralDictAccessor service) {
        List<Account> accounts = service.list(Account.class);
        int accountNum = accounts.size();
        for (Account account : accounts) {
            service.remove(account);
            assertEquals(--accountNum, service.count(Account.class, true));
            assertEquals(accounts.size(), service.count(Account.class, false));
        }
        accountNum = accounts.size();
        for (Account account : accounts) {
            service.remove(account, false);
            assertEquals(0, service.count(Account.class, true));
            assertEquals(--accountNum, service.count(Account.class, false));
        }
    }
}
