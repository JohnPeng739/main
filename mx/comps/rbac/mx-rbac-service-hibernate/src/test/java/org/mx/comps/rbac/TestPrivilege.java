package org.mx.comps.rbac;

import org.junit.Test;
import org.mx.comps.rbac.dal.entity.Privilege;
import org.mx.comps.rbac.dal.entity.Role;
import org.mx.comps.rbac.service.RoleManageService;
import org.mx.comps.rbac.service.hibernate.impl.LazyLoadServiceImpl;
import org.mx.dal.EntityFactory;
import org.mx.dal.service.GeneralDictAccessor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;

/**
 * 特权实体单元测试类
 *
 * @author : john.peng created on date : 2017/11/28
 */
public class TestPrivilege extends BaseTest {
    public static String p1Id, p2Id, p3Id;

    public static void testInsertPrivilege(GeneralDictAccessor service) {
        Privilege p1 = EntityFactory.createEntity(Privilege.class);
        p1.setCode("privilege1");
        p1.setName("privilege1");
        p1.setDesc("desc");
        p1 = service.save(p1);
        assertEquals(1, service.count(Privilege.class));
        assertNotNull(p1);
        assertNotNull(p1.getId());
        p1Id = p1.getId();
        p1 = service.getById(p1Id, Privilege.class);
        assertNotNull(p1);
        assertEquals("privilege1", p1.getCode());
        assertEquals("privilege1", p1.getName());
        assertEquals("desc", p1.getDesc());
        assertTrue(p1.isValid());

        Privilege p2 = EntityFactory.createEntity(Privilege.class);
        p2.setCode("privilege2");
        p2.setName("privilege2");
        p2.setDesc("desc");
        service.save(p2);
        assertEquals(2, service.count(Privilege.class));
        p2 = service.getById(p2.getId(), Privilege.class);
        assertNotNull(p2);
        p2Id = p2.getId();
        assertEquals("privilege2", p2.getCode());
        assertEquals("privilege2", p2.getName());
        assertEquals("desc", p2.getDesc());
        assertTrue(p2.isValid());
    }

    public static void testEditPrivilege(GeneralDictAccessor service) {
        Privilege p3 = EntityFactory.createEntity(Privilege.class);
        p3.setCode("privilege3");
        p3.setName("privilege3");
        service.save(p3);
        assertEquals(3, service.count(Privilege.class));
        p3 = service.getById(p3.getId(), Privilege.class);
        assertNotNull(p3);
        p3Id = p3.getId();
        assertEquals("privilege3", p3.getCode());
        assertEquals("privilege3", p3.getName());
        p3.setName("new name");
        p3.setValid(false);
        service.save(p3);
        assertEquals(2, service.count(Privilege.class));
        assertEquals(3, service.count(Privilege.class, false));

        p3.setName("privilege3");
        p3.setValid(true);
        service.save(p3);
        assertEquals(3, service.count(Privilege.class));
    }

    @Test
    public void testPrivilegeCrud() {
        GeneralDictAccessor service = context.getBean("generalDictAccessor", GeneralDictAccessor.class);
        assertNotNull(service);

        try {
            assertEquals(0, service.count(Privilege.class));
            // insert
            testInsertPrivilege(service);
            // edit
            testEditPrivilege(service);
            // delete
            testDeletePrivilege(service);
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testPrivilegeRoles() {
        GeneralDictAccessor service = context.getBean("generalDictAccessor", GeneralDictAccessor.class);
        assertNotNull(service);
        RoleManageService roleManageService = context.getBean( RoleManageService.class);
        assertNotNull(roleManageService);
        LazyLoadServiceImpl lazyLoad = context.getBean(LazyLoadServiceImpl.class);
        assertNotNull(lazyLoad);

        try {
            TestRole.testInsertRole(service, roleManageService);
            TestRole.testEditRole(service, roleManageService);
            testInsertPrivilege(service);
            testEditPrivilege(service);
            assertEquals(3, service.count(Role.class));
            assertEquals(3, service.count(Privilege.class));

            Privilege p1 = lazyLoad.getPrivilegeById(p1Id);
            assertNotNull(p1);
            assertTrue(p1.getRoles().isEmpty());
            Role role1 = lazyLoad.getRoleById(TestRole.role1Id);
            Role role2 = lazyLoad.getRoleById(TestRole.role2Id);
            Role role3 = lazyLoad.getRoleById(TestRole.role3Id);
            assertNotNull(role1);
            assertTrue(role1.getPrivileges().isEmpty());
            assertNotNull(role2);
            assertTrue(role2.getPrivileges().isEmpty());
            assertNotNull(role3);
            assertTrue(role3.getPrivileges().isEmpty());

            p1.getRoles().add(role1);
            p1.getRoles().add(role2);
            p1.getRoles().add(role3);
            service.save(p1);
            p1 = lazyLoad.getPrivilegeById(p1Id);
            assertNotNull(p1);
            assertEquals(3, p1.getRoles().size());
            assertEquals(new HashSet<>(Arrays.asList(role1, role2, role3)), p1.getRoles());
            role1 = lazyLoad.getRoleById(TestRole.role1Id);
            assertNotNull(role1);
            assertEquals(1, role1.getPrivileges().size());
            assertEquals(new HashSet<>(Arrays.asList(p1)), role1.getPrivileges());
            role2 = lazyLoad.getRoleById(TestRole.role2Id);
            assertNotNull(role2);
            assertEquals(1, role2.getPrivileges().size());
            assertEquals(new HashSet<>(Arrays.asList(p1)), role2.getPrivileges());
            role3 = lazyLoad.getRoleById(TestRole.role3Id);
            assertNotNull(role3);
            assertEquals(1, role3.getPrivileges().size());
            assertEquals(new HashSet<>(Arrays.asList(p1)), role3.getPrivileges());

            p1.getRoles().remove(role2);
            service.save(p1);
            p1 = lazyLoad.getPrivilegeById(p1Id);
            assertNotNull(p1);
            assertEquals(2, p1.getRoles().size());
            assertEquals(new HashSet<>(Arrays.asList(role1, role3)), p1.getRoles());
            role1 = lazyLoad.getRoleById(TestRole.role1Id);
            assertNotNull(role1);
            assertEquals(1, role1.getPrivileges().size());
            assertEquals(new HashSet<>(Arrays.asList(p1)), role1.getPrivileges());
            role2 = lazyLoad.getRoleById(TestRole.role2Id);
            assertNotNull(role2);
            assertEquals(0, role2.getPrivileges().size());
            role3 = lazyLoad.getRoleById(TestRole.role3Id);
            assertNotNull(role3);
            assertEquals(1, role3.getPrivileges().size());
            assertEquals(new HashSet<>(Arrays.asList(p1)), role3.getPrivileges());

            p1.getRoles().clear();
            service.save(p1);
            p1 = lazyLoad.getPrivilegeById(p1Id);
            assertNotNull(p1);
            assertEquals(0, p1.getRoles().size());
            role1 = lazyLoad.getRoleById(TestRole.role1Id);
            assertNotNull(role1);
            assertEquals(0, role1.getPrivileges().size());
            role2 = lazyLoad.getRoleById(TestRole.role2Id);
            assertNotNull(role2);
            assertEquals(0, role2.getPrivileges().size());
            role3 = lazyLoad.getRoleById(TestRole.role3Id);
            assertNotNull(role3);
            assertEquals(0, role3.getPrivileges().size());
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }

    private void testDeletePrivilege(GeneralDictAccessor service) {
        List<Privilege> ps = service.list(Privilege.class);
        int pnum = ps.size();
        for (Privilege p : ps) {
            service.remove(p);
            assertEquals(--pnum, service.count(Privilege.class));
            assertEquals(ps.size(), service.count(Privilege.class, false));
        }
        pnum = ps.size();
        for (Privilege p : ps) {
            service.remove(p, false);
            assertEquals(--pnum, service.count(Privilege.class, false));
        }
    }
}
