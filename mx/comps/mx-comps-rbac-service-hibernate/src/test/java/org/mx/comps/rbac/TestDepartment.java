package org.mx.comps.rbac;

import org.junit.Test;
import org.mx.comps.rbac.dal.entity.Department;
import org.mx.comps.rbac.dal.entity.User;
import org.mx.comps.rbac.service.DepartmentManageService;
import org.mx.comps.rbac.service.UserManageService;
import org.mx.dal.EntityFactory;

import java.util.List;

import static org.junit.Assert.*;

/**
 * 部门单元测试类
 *
 * @author : john.peng created on date : 2017/11/28
 */
public class TestDepartment extends BaseTest {
    public static String depart1Id, depart2Id, depart3Id;

    public static void testInsertDepartment(DepartmentManageService service) {
        Department depart1 = EntityFactory.createEntity(Department.class);
        depart1.setCode("depart1");
        depart1.setName("depart1");
        depart1.setDesc("description");
        assertNull(depart1.getId());
        depart1 = service.saveDepartment(depart1);
        assertNotNull(depart1);
        assertNotNull(depart1.getId());
        depart1Id = depart1.getId();
        assertEquals(1, service.count(Department.class));
        depart1 = service.getById(depart1Id, Department.class);
        assertNotNull(depart1);
        assertEquals("depart1", depart1.getCode());
        assertEquals("depart1", depart1.getName());
        assertEquals("description", depart1.getDesc());
        depart1 = service.getByCode("depart1", Department.class);
        assertNotNull(depart1);
        assertEquals("depart1", depart1.getCode());
        assertEquals("depart1", depart1.getName());
        assertEquals("description", depart1.getDesc());

        Department depart2 = EntityFactory.createEntity(Department.class);
        depart2.setCode("depart2");
        depart2.setName("depart2");
        depart2 = service.saveDepartment(depart2);
        assertEquals(2, service.count(Department.class));
        assertNotNull(depart2);
        depart2Id = depart2.getId();
        assertNotNull(depart2.getId());
    }

    public static void testEditDepartment(DepartmentManageService service) {
        Department depart3 = EntityFactory.createEntity(Department.class);
        depart3.setCode("depart3");
        depart3.setName("depart3");
        depart3 = service.saveDepartment(depart3);
        assertEquals(3, service.count(Department.class));
        assertNotNull(depart3);
        assertNotNull(depart3.getId());
        depart3Id = depart3.getId();

        depart3 = service.getById(depart3Id, Department.class);
        depart3.setName("new name");
        depart3.setValid(false);
        depart3 = service.saveDepartment(depart3);
        assertEquals(2, service.count(Department.class));
        assertEquals(3, service.count(Department.class, false));
        assertNotNull(depart3);
        depart3 = service.getById(depart3Id, Department.class);
        assertNotNull(depart3);
        assertEquals("new name", depart3.getName());
        assertFalse(depart3.isValid());

        depart3.setValid(true);
        depart3.setName("depart3");
        service.saveDepartment(depart3);
        assertEquals(3, service.count(Department.class));
        assertEquals(3, service.count(Department.class, false));
    }

    @Test
    public void testDepartmentCrud() {
        DepartmentManageService service = context.getBean("departmentManageService", DepartmentManageService.class);
        assertNotNull(service);

        try {
            assertEquals(service.count(User.class), 0);
            // insert
            testInsertDepartment(service);
            // edit
            testEditDepartment(service);
            // delete
            testDeleteDepartment(service);
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testDepartmentManager() {
        DepartmentManageService service = context.getBean("departmentManageService", DepartmentManageService.class);
        assertNotNull(service);
        UserManageService userManageService = context.getBean("userManageService", UserManageService.class);
        assertNotNull(userManageService);

        try {
            TestUser.testInsertUser(userManageService);
            TestUser.testEditUser(userManageService);
            testInsertDepartment(service);
            testEditDepartment(service);
            assertEquals(3, userManageService.count(User.class));
            assertEquals(3, service.count(Department.class));

            Department depart1 = service.getById(depart1Id, Department.class);
            assertNotNull(depart1);
            assertTrue(depart1.isValid());
            assertNull(depart1.getManager());
            User john = userManageService.getById(TestUser.johnId, User.class);
            assertNotNull(john);
            assertTrue(john.isValid());
            depart1.setManager(john);
            assertNotNull(depart1.getManager());
            depart1 = service.saveDepartment(depart1);
            assertEquals(3, service.count(Department.class));
            assertNotNull(depart1);
            depart1 = service.getById(depart1Id, Department.class);
            assertNotNull(depart1);
            assertNotNull(depart1.getManager());
            assertEquals(john, depart1.getManager());

            depart1.setManager(null);
            assertNull(depart1.getManager());
            service.saveDepartment(depart1);
            depart1 = service.saveDepartment(depart1);
            assertEquals(3, service.count(Department.class));
            assertNull(depart1.getManager());
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }

    private void testDeleteDepartment(DepartmentManageService service) {
        List<Department> departs = service.list(Department.class);
        int departNum = departs.size();
        for (Department depart : departs) {
            service.remove(depart);
            assertEquals(--departNum, service.count(Department.class));
            assertEquals(departs.size(), service.count(Department.class, false));
        }

        departNum = departs.size();
        for (Department depart : departs) {
            service.remove(depart, false);
            assertEquals(--departNum, service.count(Department.class, false));
        }
    }
}
