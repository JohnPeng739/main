package org.mx.comps.rbac;

import org.junit.Test;
import org.mx.comps.rbac.dal.entity.Account;
import org.mx.comps.rbac.dal.entity.Accredit;
import org.mx.comps.rbac.dal.entity.Role;
import org.mx.comps.rbac.dal.entity.User;
import org.mx.comps.rbac.error.UserInterfaceRbacErrorException;
import org.mx.comps.rbac.service.AccountManageService;
import org.mx.comps.rbac.service.AccreditManageService;
import org.mx.comps.rbac.service.RoleManageService;
import org.mx.comps.rbac.service.UserManageService;
import org.mx.dal.service.GeneralDictAccessor;
import org.mx.error.UserInterfaceSystemErrorException;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import static org.junit.Assert.*;

/**
 * 授权管理单元测试类
 *
 * @author : john.peng created on date : 2017/12/01
 */
public class TestAccredit extends BaseTest {
    @Test
    public void testAccredit() {
        GeneralDictAccessor service = context.getBean("generalDictAccessor", GeneralDictAccessor.class);
        assertNotNull(service);
        AccreditManageService accreditService = context.getBean(AccreditManageService.class);
        assertNotNull(service);
        UserManageService userManageService = context.getBean(UserManageService.class);
        assertNotNull(userManageService);
        AccountManageService accountManageService = context.getBean(AccountManageService.class);
        assertNotNull(accountManageService);
        RoleManageService roleManageService = context.getBean(RoleManageService.class);
        assertNotNull(roleManageService);

        try {
            TestUser.testInsertUser(service, userManageService);
            TestUser.testEditUser(service, userManageService);
            assertEquals(3, service.count(User.class));
            TestAccount.testInsertAccount(service, accountManageService);
            TestAccount.testEditAccount(service, accountManageService);
            TestRole.testInsertRole(service, roleManageService);
            TestRole.testEditRole(service, roleManageService);
            assertEquals(3, service.count(Account.class));
            assertEquals(3, service.count(Role.class));
            assertEquals(0, service.count(Accredit.class));

            Account account1 = service.getById(TestAccount.account1Id, Account.class);
            assertNotNull(account1);
            Account account2 = service.getById(TestAccount.account2Id, Account.class);
            assertNotNull(account2);
            Role role1 = service.getById(TestRole.role1Id, Role.class);
            assertNotNull(role1);
            Role role2 = service.getById(TestRole.role2Id, Role.class);
            assertNotNull(role2);
            Role role3 = service.getById(TestRole.role3Id, Role.class);
            assertNotNull(role3);

            long startTime = new Date().getTime();
            AccreditManageService.AccreditInfo accreditInfo;
            // 测试没有设置源
            try {
                accreditInfo = AccreditManageService.AccreditInfo.valueOf(
                        "", TestAccount.account2Id,
                        Arrays.asList(TestRole.role1Id, TestRole.role2Id, TestRole.role3Id), startTime, -1, "desc");
                accreditService.accredit(accreditInfo);
                fail("Here need a exception");
            } catch (UserInterfaceSystemErrorException ex) {
                assertEquals(UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM.getErrorCode(), ex.getErrorCode());
            }
            try {
                accreditInfo = AccreditManageService.AccreditInfo.valueOf(
                        TestAccount.account1Id, "",
                        Arrays.asList(TestRole.role1Id, TestRole.role2Id, TestRole.role3Id), startTime, -1, "desc");
                accreditService.accredit(accreditInfo);
                fail("Here need a exception");
            } catch (UserInterfaceSystemErrorException ex) {
                assertEquals(UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM.getErrorCode(), ex.getErrorCode());
            }
            try {
                accreditInfo = AccreditManageService.AccreditInfo.valueOf(
                        TestAccount.account1Id, TestAccount.account2Id,
                        null, startTime, -1, "desc");
                accreditService.accredit(accreditInfo);
                fail("Here need a exception");
            } catch (UserInterfaceSystemErrorException ex) {
                assertEquals(UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM.getErrorCode(), ex.getErrorCode());
            }
            try {
                accreditInfo = AccreditManageService.AccreditInfo.valueOf(
                        TestAccount.account1Id, TestAccount.account2Id,
                        Arrays.asList(), startTime, -1, "desc");
                accreditService.accredit(accreditInfo);
                fail("Here need a exception");
            } catch (UserInterfaceSystemErrorException ex) {
                assertEquals(UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM.getErrorCode(), ex.getErrorCode());
            }
            try {
                accreditInfo = AccreditManageService.AccreditInfo.valueOf(
                        "abcde", TestAccount.account2Id,
                        Arrays.asList(TestRole.role1Id, TestRole.role2Id, TestRole.role3Id), startTime, -1, "desc");
                accreditService.accredit(accreditInfo);
                fail("Here need a exception");
            } catch (UserInterfaceRbacErrorException ex) {
                assertEquals(UserInterfaceRbacErrorException.RbacErrors.ACCOUNT_NOT_FOUND.getErrorCode(), ex.getErrorCode());
            }
            try {
                accreditInfo = AccreditManageService.AccreditInfo.valueOf(
                        TestAccount.account1Id, "abcde",
                        Arrays.asList(TestRole.role1Id, TestRole.role2Id, TestRole.role3Id), startTime, -1, "desc");
                accreditService.accredit(accreditInfo);
                fail("Here need a exception");
            } catch (UserInterfaceRbacErrorException ex) {
                assertEquals(UserInterfaceRbacErrorException.RbacErrors.ACCOUNT_NOT_FOUND.getErrorCode(), ex.getErrorCode());
            }
            try {
                accreditInfo = AccreditManageService.AccreditInfo.valueOf(
                        TestAccount.account1Id, TestAccount.account2Id,
                        Arrays.asList(TestRole.role1Id, "abcdef", TestRole.role3Id), startTime, -1, "desc");
                accreditService.accredit(accreditInfo);
                fail("Here need a exception");
            } catch (UserInterfaceRbacErrorException ex) {
                assertEquals(UserInterfaceRbacErrorException.RbacErrors.ROLE_NOT_FOUND.getErrorCode(), ex.getErrorCode());
            }
            // 测试正常授权
            long endTime = new Date().getTime() + 500;
            accreditInfo = AccreditManageService.AccreditInfo.valueOf(
                    TestAccount.account1Id, TestAccount.account2Id,
                    Arrays.asList(TestRole.role1Id, TestRole.role2Id, TestRole.role3Id), startTime, endTime, "desc");
            Accredit accredit = accreditService.accredit(accreditInfo);
            assertEquals(1, service.count(Accredit.class));
            assertNotNull(accredit);
            assertEquals(account1, accredit.getSrc());
            assertEquals(account2, accredit.getTar());
            assertEquals(new HashSet<>(Arrays.asList(role1, role2, role3)), accredit.getRoles());
            assertEquals(startTime, accredit.getStartTime().getTime());
            assertEquals(endTime, accredit.getEndTime().getTime());
            assertTrue(accredit.isValid());
            assertEquals("desc", accredit.getDesc());
            accredit = service.getById(accredit.getId(), Accredit.class);
            assertNotNull(accredit);
            assertEquals(account1, accredit.getSrc());
            assertEquals(account2, accredit.getTar());
            assertEquals(new HashSet<>(Arrays.asList(role1, role2, role3)), accredit.getRoles());
            assertEquals(startTime, accredit.getStartTime().getTime());
            assertEquals(endTime, accredit.getEndTime().getTime());
            assertTrue(accredit.isValid());
            assertEquals("desc", accredit.getDesc());
            // 测试重复授权
            try {
                accreditInfo = AccreditManageService.AccreditInfo.valueOf(
                        TestAccount.account1Id, TestAccount.account2Id,
                        Arrays.asList(TestRole.role1Id, TestRole.role3Id), startTime, -1, "desc");
                accreditService.accredit(accreditInfo);
                fail("Here need a exception");
            } catch (UserInterfaceRbacErrorException ex) {
                assertEquals(UserInterfaceRbacErrorException.RbacErrors.ACCREDIT_SAME_FOUND.getErrorCode(), ex.getErrorCode());
            }
            // 测试自动时间到达后关闭
            Thread.sleep(600);
            accreditInfo = AccreditManageService.AccreditInfo.valueOf(
                    TestAccount.account1Id, TestAccount.account2Id,
                    Arrays.asList(TestRole.role1Id, TestRole.role3Id), startTime, -1, "desc");
            accredit = accreditService.accredit(accreditInfo);
            assertEquals(2, service.count(Accredit.class));
            assertEquals(2, service.count(Accredit.class, false));
            accredit = service.getById(accredit.getId(), Accredit.class);
            assertNotNull(accredit);
            assertEquals(account1, accredit.getSrc());
            assertEquals(account2, accredit.getTar());
            assertEquals(new HashSet<>(Arrays.asList(role1, role3)), accredit.getRoles());
            assertEquals(startTime, accredit.getStartTime().getTime());
            assertNull(accredit.getEndTime());
            assertTrue(accredit.isValid());
            assertEquals("desc", accredit.getDesc());

            // 测试关闭
            accreditService.closeAccredit(accredit.getId());
            assertEquals(1, service.count(Accredit.class));
            assertEquals(2, service.count(Accredit.class, false));
            // 再次授权
            accreditInfo = AccreditManageService.AccreditInfo.valueOf(
                    TestAccount.account1Id, TestAccount.account2Id,
                    Arrays.asList(TestRole.role1Id, TestRole.role3Id), startTime, -1, "desc");
            accredit = accreditService.accredit(accreditInfo);
            assertEquals(2, service.count(Accredit.class));
            assertEquals(3, service.count(Accredit.class, false));
            assertNotNull(accredit);
            accredit = service.getById(accredit.getId(), Accredit.class);
            assertNotNull(accredit);
            assertEquals(account1, accredit.getSrc());
            assertEquals(account2, accredit.getTar());
            assertEquals(new HashSet<>(Arrays.asList(role1, role3)), accredit.getRoles());
            assertEquals(startTime, accredit.getStartTime().getTime());
            assertNull(accredit.getEndTime());
            assertTrue(accredit.isValid());
            assertEquals("desc", accredit.getDesc());
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }
}
