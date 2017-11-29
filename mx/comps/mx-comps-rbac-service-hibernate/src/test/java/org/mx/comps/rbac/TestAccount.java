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
import org.mx.dal.EntityFactory;

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

    public static void testInsertAccount(AccountManageService service) throws NoSuchAlgorithmException {
        Account account1 = EntityFactory.createEntity(Account.class);
        account1.setDesc("description account.");
        account1.setCode("account1");
        account1.setName("account1's name");
        account1.setPassword("password");
        assertNull(account1.getId());
        account1 = service.saveAccount(account1);
        assertEquals(1, service.count(Account.class));
        assertNotNull(account1);
        assertNotNull(account1.getId());
        account1Id = account1.getId();
        assertEquals("description account.", account1.getDesc());
        assertEquals("account1", account1.getCode());
        assertEquals("account1's name", account1.getName());
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
        assertEquals("account1's name", account1.getName());
        assertEquals(DigestUtils.md5("password"), account1.getPassword());
        assertEquals("admin", account1.getOperator());
        account1 = service.getByCode(account1.getCode(), Account.class);
        assertNotNull(account1);
        assertNotNull(account1.getId());
        assertEquals("description account.", account1.getDesc());
        assertEquals("account1", account1.getCode());
        assertEquals("account1's name", account1.getName());
        assertEquals(DigestUtils.md5("password"), account1.getPassword());
        assertEquals("admin", account1.getOperator());
        assertTrue(account1.isValid());

        Account account2 = EntityFactory.createEntity(Account.class);
        account2.setCode("account2");
        account2.setName("account2's name");
        account2 = service.saveAccount(account2);
        assertEquals(2, service.count(Account.class));
        assertNotNull(account2);
        assertNotNull(account2.getId());
        account2Id = account2.getId();
        assertEquals("account2", account2.getCode());
        assertEquals("account2's name", account2.getName());
        assertTrue(account2.isValid());
    }

    public static void testEditAccount(AccountManageService service) {
        Account account3 = EntityFactory.createEntity(Account.class);
        account3.setCode("account3");
        account3.setName("account3's name");
        account3 = service.saveAccount(account3);
        assertEquals(3, service.count(Account.class));
        assertNotNull(account3);
        assertNotNull(account3.getId());
        account3Id = account3.getId();
        assertEquals("account3's name", account3.getName());

        account3.setName("new name.");
        account3.setValid(false);
        account3 = service.saveAccount(account3);
        assertEquals(2, service.count(Account.class));
        assertEquals(2, service.count(Account.class, true));
        assertEquals(3, service.count(Account.class, false));
        assertNotNull(account3);
        assertEquals("new name.", account3.getName());
        assertFalse(account3.isValid());
        account3 = service.getByCode("account3", Account.class);
        assertNotNull(account3);
        assertEquals("new name.", account3.getName());
        assertFalse(account3.isValid());

        account3.setValid(true);
        service.saveAccount(account3);
        assertEquals(3, service.count(Account.class));
        assertEquals(3, service.count(Account.class, true));
        assertEquals(3, service.count(Account.class, false));
        account3 = service.getById(account3Id, Account.class);
        assertNotNull(account3);
        assertTrue(account3.isValid());
    }

    @Test
    public void testAccountCrud() {
        AccountManageService service = context.getBean("accountManageService", AccountManageService.class);
        assertNotNull(service);

        try {
            assertEquals(0, service.count(Account.class));
            // insert
            testInsertAccount(service);
            // edit
            testEditAccount(service);
            // delete
            testDeleteAccount(service);
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testAccountOwner() {
        AccountManageService service = context.getBean("accountManageService", AccountManageService.class);
        assertNotNull(service);
        UserManageService userManageService = context.getBean("userManageService", UserManageService.class);
        assertNotNull(userManageService);

        try {
            TestUser.testInsertUser(userManageService);
            TestUser.testEditUser(userManageService);
            testInsertAccount(service);
            Account account1 = service.getById(account1Id, Account.class);
            assertNotNull(account1);
            User john = userManageService.getById(TestUser.johnId, User.class);
            assertNotNull(john);
            assertNull(account1.getOwner());
            account1.setOwner(john);
            assertNotNull(account1.getOwner());
            service.saveAccount(account1);
            account1 = service.getById(account1Id, Account.class);
            assertNotNull(account1.getOwner());
            assertEquals(account1.getOwner().getId(), john.getId());

            account1.setOwner(null);
            service.saveAccount(account1);
            account1 = service.getById(account1Id, Account.class);
            assertNotNull(account1);
            assertNull(account1.getOwner());
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testAccountRoles() {
        AccountManageService service = context.getBean("accountManageService", AccountManageService.class);
        assertNotNull(service);
        RoleManageService roleManageService = context.getBean("roleManageService", RoleManageService.class);
        assertNotNull(roleManageService);

        try {
            TestRole.testInsertRole(roleManageService);
            TestRole.testEditRole(roleManageService);
            testInsertAccount(service);
            testEditAccount(service);
            assertEquals(3, roleManageService.count(Role.class));
            assertEquals(3, service.count(Account.class));
            Account account1 = service.getById(account1Id, Account.class);
            assertNotNull(account1);
            assertTrue(account1.getRoles().isEmpty());
            Role role1 = roleManageService.getById(TestRole.role1Id, Role.class);
            Role role2 = roleManageService.getById(TestRole.role2Id, Role.class);
            Role role3 = roleManageService.getById(TestRole.role3Id, Role.class);
            assertNotNull(role1);
            assertNotNull(role2);
            assertNotNull(role3);

            account1.getRoles().add(role1);
            account1.getRoles().add(role2);
            account1.getRoles().add(role3);
            assertEquals(3, account1.getRoles().size());
            service.saveAccount(account1);
            assertEquals(3, service.count(Account.class));
            account1 = service.getById(account1Id, Account.class);
            assertNotNull(account1);
            assertEquals(3, account1.getRoles().size());
            assertEquals(new HashSet<>(Arrays.asList(role1, role2, role3)), account1.getRoles());

            account1.getRoles().remove(role2);
            service.saveAccount(account1);
            assertEquals(3, service.count(Account.class));
            account1 = service.getById(account1Id, Account.class);
            assertNotNull(account1);
            assertEquals(2, account1.getRoles().size());
            assertEquals(new HashSet<>(Arrays.asList(role1, role3)), account1.getRoles());

            account1.getRoles().clear();
            service.saveAccount(account1);
            account1 = service.getById(account1Id, Account.class);
            assertNotNull(account1);
            assertTrue(account1.getRoles().isEmpty());
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testChangePassword() {
        AccountManageService service = context.getBean("accountManageService", AccountManageService.class);
        assertNotNull(service);

        try {
            testInsertAccount(service);
            testEditAccount(service);
            assertEquals(3, service.count(Account.class));

            Account account1 = service.getById(account1Id, Account.class);
            assertNotNull(account1);
            assertEquals(DigestUtils.md5("password"), account1.getPassword());
            service.changePassword(account1.getId(), "password", "new password");
            account1 = service.getById(account1Id, Account.class);
            assertNotNull(account1);
            assertEquals(DigestUtils.md5("new password"), account1.getPassword());

            Account account3 = service.getById(account3Id, Account.class);
            assertNotNull(account3);
            assertEquals(DigestUtils.md5("ds110119"), account3.getPassword());
            service.changePassword(account3.getId(), "ds110119", "new password");
            account3 = service.getById(account3Id, Account.class);
            assertNotNull(account3);
            assertEquals(DigestUtils.md5("new password"), account3.getPassword());
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testLoginAndLogout() {
        AccountManageService service = context.getBean("accountManageService", AccountManageService.class);
        assertNotNull(service);

        try {
            testInsertAccount(service);
            testEditAccount(service);
            assertEquals(3, service.count(Account.class));

            // 测试正常流程
            LoginHistory login = service.login("account1", "password", false);
            assertNotNull(login);
            Account account1 = service.getById(account1Id, Account.class);
            assertNotNull(account1);
            assertEquals(account1, login.getAccount());
            // 测试用户重复登录
            login = service.login("account1", "password", true);
            assertNotNull(login);
            assertEquals(account1, login.getAccount());
            try {
                service.login("account1", "password", false);
            } catch (UserInterfaceRbacErrorException ex) {
                assertEquals(UserInterfaceRbacErrorException.RbacErrors.ACCOUNT_ALREADY_LOGINED.getErrorCode(), ex.getErrorCode());
            }
            // 测试正常登出
            login = service.logout(account1.getId());
            assertNotNull(login);
            assertEquals(account1, login.getAccount());

            // 测试用户不存在
            try {
                service.login("abc", "adasd", false);
            } catch (UserInterfaceRbacErrorException ex) {
                assertEquals(UserInterfaceRbacErrorException.RbacErrors.ACCOUNT_NOT_FOUND.getErrorMessage(), ex.getErrorMessage());
            }

            // 测试密码不正确
            try {
                service.login("account1", "adfasd", false);
            } catch (UserInterfaceRbacErrorException ex) {
                assertEquals(UserInterfaceRbacErrorException.RbacErrors.ACCOUNT_PASSWORD_NOT_MATCHED.getErrorMessage(), ex.getErrorMessage());
            }
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }

    private void testDeleteAccount(AccountManageService service) {
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
