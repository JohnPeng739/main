package org.mx.comps.rbac;

import org.junit.Test;
import org.mx.comps.rbac.dal.entity.Account;
import org.mx.comps.rbac.dal.entity.Privilege;
import org.mx.comps.rbac.dal.entity.Role;
import org.mx.comps.rbac.service.AccountManageService;
import org.mx.comps.rbac.service.RoleManageService;
import org.mx.dal.EntityFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;

/**
 * 角色单元测试类
 *
 * @author : john.peng created on date : 2017/11/28
 */
public class TestRole extends BaseTest {
    public static String role1Id, role2Id, role3Id;

    public static void testInsertRole(RoleManageService service) {
        Role role1 = EntityFactory.createEntity(Role.class);
        role1.setCode("role1");
        role1.setName("role1");
        role1.setDesc("desc");
        role1 = service.saveRole(role1);
        assertEquals(1, service.count(Role.class));
        role1 = service.getById(role1.getId(), Role.class);
        assertNotNull(role1);
        assertNotNull(role1.getId());
        role1Id = role1.getId();
        assertEquals("role1", role1.getCode());
        assertEquals("role1", role1.getName());
        assertEquals("desc", role1.getDesc());
        assertTrue(role1.isValid());

        Role role2 = EntityFactory.createEntity(Role.class);
        role2.setCode("role2");
        role2.setName("role2");
        role2.setDesc("desc");
        service.saveRole(role2);
        assertEquals(2, service.count(Role.class));
        role2 = service.getById(role2.getId(), Role.class);
        assertNotNull(role2);
        role2Id = role2.getId();
        assertEquals("role2", role2.getCode());
        assertEquals("role2", role2.getName());
        assertTrue(role2.isValid());
    }

    public static void testEditRole(RoleManageService service) {
        Role role3 = EntityFactory.createEntity(Role.class);
        role3.setCode("role3");
        role3.setName("role3");
        role3.setDesc("description");
        service.saveRole(role3);
        assertEquals(3, service.count(Role.class));
        role3 = service.getById(role3.getId(), Role.class);
        assertNotNull(role3);
        assertNotNull(role3.getId());
        role3Id = role3.getId();
        assertEquals("role3", role3.getName());
        assertEquals("description", role3.getDesc());
        assertTrue(role3.isValid());

        role3.setName("new name");
        role3.setDesc("new desc");
        role3.setValid(false);
        service.saveRole(role3);
        assertEquals(2, service.count(Role.class));
        assertEquals(3, service.count(Role.class, false));
        role3 = service.getById(role3.getId(), Role.class);
        assertNotNull(role3);
        assertNotNull(role3.getId());
        role3Id = role3.getId();
        assertEquals("new name", role3.getName());
        assertEquals("new desc", role3.getDesc());
        assertFalse(role3.isValid());

        role3.setName("role3");
        role3.setValid(true);
        service.saveRole(role3);
        assertEquals(3, service.count(Role.class));
    }

    @Test
    public void testRoleCrud() {
        RoleManageService service = context.getBean("roleManageService", RoleManageService.class);
        assertNotNull(service);

        try {
            assertEquals(0, service.count(Role.class));
            // insert
            testInsertRole(service);
            // edit
            testEditRole(service);
            // delete
            testDeleteRole(service);
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testRoleAccounts() {
        RoleManageService service = context.getBean("roleManageService", RoleManageService.class);
        assertNotNull(service);
        AccountManageService accountManageService = context.getBean("accountManageService", AccountManageService.class);
        assertNotNull(accountManageService);

        try {
            TestAccount.testInsertAccount(accountManageService);
            TestAccount.testEditAccount(accountManageService);
            testInsertRole(service);
            testEditRole(service);
            assertEquals(3, accountManageService.count(Account.class));
            assertEquals(3, service.count(Role.class));

            Role role1 = service.getById(role1Id, Role.class);
            assertNotNull(role1);
            assertEquals(0, role1.getAccounts().size());
            Account account1 = accountManageService.getById(TestAccount.account1Id, Account.class);
            Account account2 = accountManageService.getById(TestAccount.account2Id, Account.class);
            Account account3 = accountManageService.getById(TestAccount.account3Id, Account.class);
            assertNotNull(account1);
            assertNotNull(account2);
            assertNotNull(account3);

            role1.getAccounts().add(account1);
            role1.getAccounts().add(account2);
            role1.getAccounts().add(account3);
            assertEquals(3, role1.getAccounts().size());
            service.saveRole(role1);
            assertEquals(3, service.count(Role.class));
            role1 = service.getById(role1Id, Role.class);
            assertNotNull(role1);
            assertNotNull(role1.getAccounts());
            assertEquals(3, role1.getAccounts().size());
            assertEquals(new HashSet<>(Arrays.asList(account1, account2, account3)), role1.getAccounts());

            role1.getAccounts().remove(account2);
            service.saveRole(role1);
            assertEquals(3, service.count(Role.class));
            role1 = service.getById(role1Id, Role.class);
            assertNotNull(role1);
            assertNotNull(role1.getAccounts());
            assertEquals(2, role1.getAccounts().size());
            assertEquals(new HashSet<>(Arrays.asList(account1, account3)), role1.getAccounts());

            role1.getAccounts().clear();
            service.saveRole(role1);
            role1 = service.getById(role1Id, Role.class);
            assertNotNull(role1);
            assertEquals(0, role1.getAccounts().size());
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testRolePrivileges() {
        RoleManageService service = context.getBean("roleManageService", RoleManageService.class);
        assertNotNull(service);
        try {
            TestPrivilege.testInsertPrivilege(service);
            TestPrivilege.testEditPrivilege(service);
            testInsertRole(service);
            testEditRole(service);
            assertEquals(3, service.count(Role.class));
            assertEquals(3, service.count(Privilege.class));

            Role role1 = service.getById(role1Id, Role.class);
            assertNotNull(role1);
            assertTrue(role1.getPrivileges().isEmpty());
            Privilege p1 = service.getById(TestPrivilege.p1Id, Privilege.class);
            assertNotNull(p1);
            assertTrue(p1.getRoles().isEmpty());
            Privilege p2 = service.getById(TestPrivilege.p2Id, Privilege.class);
            assertNotNull(p2);
            assertTrue(p2.getRoles().isEmpty());
            Privilege p3 = service.getById(TestPrivilege.p3Id, Privilege.class);
            assertNotNull(p3);
            assertTrue(p3.getRoles().isEmpty());

            role1.getPrivileges().add(p1);
            role1.getPrivileges().add(p2);
            role1.getPrivileges().add(p3);
            service.saveRole(role1);
            assertEquals(3, service.count(Role.class));
            role1 = service.getById(role1Id, Role.class);
            assertNotNull(role1);
            assertEquals(3, role1.getPrivileges().size());
            assertEquals(new HashSet<>(Arrays.asList(p1, p2, p3)), role1.getPrivileges());
            p1 = service.getById(TestPrivilege.p1Id, Privilege.class);
            assertNotNull(p1);
            assertEquals(1, p1.getRoles().size());
            assertEquals(new HashSet<>(Arrays.asList(role1)), p1.getRoles());
            p2 = service.getById(TestPrivilege.p2Id, Privilege.class);
            assertNotNull(p2);
            assertEquals(1, p2.getRoles().size());
            assertEquals(new HashSet<>(Arrays.asList(role1)), p2.getRoles());
            p3 = service.getById(TestPrivilege.p3Id, Privilege.class);
            assertNotNull(p3);
            assertEquals(1, p3.getRoles().size());
            assertEquals(new HashSet<>(Arrays.asList(role1)), p3.getRoles());

            role1.getPrivileges().remove(p2);
            service.saveRole(role1);
            assertEquals(3, service.count(Role.class));
            role1 = service.getById(role1Id, Role.class);
            assertNotNull(role1);
            assertEquals(2, role1.getPrivileges().size());
            assertEquals(new HashSet<>(Arrays.asList(p1, p3)), role1.getPrivileges());
            p1 = service.getById(TestPrivilege.p1Id, Privilege.class);
            assertNotNull(p1);
            assertEquals(1, p1.getRoles().size());
            assertEquals(new HashSet<>(Arrays.asList(role1)), p1.getRoles());
            p2 = service.getById(TestPrivilege.p2Id, Privilege.class);
            assertNotNull(p2);
            assertEquals(0, p2.getRoles().size());
            p3 = service.getById(TestPrivilege.p3Id, Privilege.class);
            assertNotNull(p3);
            assertEquals(1, p3.getRoles().size());
            assertEquals(new HashSet<>(Arrays.asList(role1)), p3.getRoles());

            role1.getPrivileges().clear();
            service.saveRole(role1);
            assertEquals(3, service.count(Role.class));
            role1 = service.getById(role1Id, Role.class);
            assertNotNull(role1);
            assertEquals(0, role1.getPrivileges().size());
            p1 = service.getById(TestPrivilege.p1Id, Privilege.class);
            assertNotNull(p1);
            assertEquals(0, p1.getRoles().size());
            p2 = service.getById(TestPrivilege.p2Id, Privilege.class);
            assertNotNull(p2);
            assertEquals(0, p2.getRoles().size());
            p3 = service.getById(TestPrivilege.p3Id, Privilege.class);
            assertNotNull(p3);
            assertEquals(0, p3.getRoles().size());
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }

    private void testDeleteRole(RoleManageService service) {
        List<Role> roles = service.list(Role.class);
        int roleNum = roles.size();
        for (Role role : roles) {
            service.remove(role);
            assertEquals(--roleNum, service.count(Role.class));
            assertEquals(roles.size(), service.count(Role.class, false));
        }

        roleNum = roles.size();
        for (Role role : roles) {
            service.remove(role, false);
            assertEquals(--roleNum, service.count(Role.class, false));
        }
    }
}
