package org.mx.comps.rbac;

import org.junit.Test;
import org.mx.comps.rbac.dal.entity.Department;
import org.mx.comps.rbac.dal.entity.User;
import org.mx.comps.rbac.service.DepartmentManageService;
import org.mx.comps.rbac.service.UserManageService;
import org.mx.dal.service.GeneralDictAccessor;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * 部门单元测试类
 *
 * @author : john.peng created on date : 2017/11/28
 */
public class TestDepartment extends BaseTest {
    public static String depart1Id, depart2Id, depart3Id;

    public static void testInsertDepartment(GeneralDictAccessor service, DepartmentManageService departService) {
        DepartmentManageService.DepartInfo departInfo = DepartmentManageService.DepartInfo.valueOf("depart1",
                "depart1", "description");
        Department depart1 = departService.saveDepartment(departInfo);
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

        departInfo = DepartmentManageService.DepartInfo.valueOf("depart2",
                "depart2", "description");
        Department depart2 = departService.saveDepartment(departInfo);
        assertEquals(2, service.count(Department.class));
        assertNotNull(depart2);
        depart2Id = depart2.getId();
        assertNotNull(depart2.getId());
    }

    public static void testEditDepartment(GeneralDictAccessor service, DepartmentManageService departService) {
        DepartmentManageService.DepartInfo departInfo = DepartmentManageService.DepartInfo.valueOf("depart3",
                "depart3", "description");
        Department depart3 = departService.saveDepartment(departInfo);
        assertEquals(3, service.count(Department.class));
        assertNotNull(depart3);
        assertNotNull(depart3.getId());
        depart3Id = depart3.getId();

        departInfo = DepartmentManageService.DepartInfo.valueOf("depart3",
                "new name", "description", depart3.getId(), "", Arrays.asList(), false);
        depart3 = departService.saveDepartment(departInfo);
        assertEquals(2, service.count(Department.class));
        assertEquals(3, service.count(Department.class, false));
        assertNotNull(depart3);
        depart3 = service.getById(depart3Id, Department.class);
        assertNotNull(depart3);
        assertEquals("new name", depart3.getName());
        assertFalse(depart3.isValid());

        departInfo = DepartmentManageService.DepartInfo.valueOf("depart3",
                "depart3", "description", depart3.getId(), "", Arrays.asList(), true);
        departService.saveDepartment(departInfo);
        assertEquals(3, service.count(Department.class));
        assertEquals(3, service.count(Department.class, false));
    }

    @Test
    public void testDepartmentCrud() {
        GeneralDictAccessor service = context.getBean("generalDictAccessor", GeneralDictAccessor.class);
        assertNotNull(service);
        DepartmentManageService departService = context.getBean(DepartmentManageService.class);
        assertNotNull(service);

        try {
            assertEquals(service.count(User.class), 0);
            // insert
            testInsertDepartment(service, departService);
            // edit
            testEditDepartment(service, departService);
            // delete
            testDeleteDepartment(service);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }

    @Test
    public void testDepartmentManager() {
        GeneralDictAccessor service = context.getBean("generalDictAccessor", GeneralDictAccessor.class);
        assertNotNull(service);
        DepartmentManageService departService = context.getBean(DepartmentManageService.class);
        assertNotNull(service);
        UserManageService userManageService = context.getBean(UserManageService.class);
        assertNotNull(userManageService);

        try {
            TestUser.testInsertUser(service, userManageService);
            TestUser.testEditUser(service, userManageService);
            testInsertDepartment(service, departService);
            testEditDepartment(service, departService);
            assertEquals(3, service.count(User.class));
            assertEquals(3, service.count(Department.class));

            Department depart1 = service.getById(depart1Id, Department.class);
            assertNotNull(depart1);
            assertTrue(depart1.isValid());
            assertNull(depart1.getManager());
            User john = service.getById(TestUser.johnId, User.class);
            assertNotNull(john);
            assertTrue(john.isValid());
            DepartmentManageService.DepartInfo departInfo = DepartmentManageService.DepartInfo.valueOf(depart1.getCode(),
                    depart1.getName(), depart1.getDesc(), depart1.getId(), john.getId(), Arrays.asList(), true);
            depart1 = departService.saveDepartment(departInfo);
            assertEquals(3, service.count(Department.class));
            assertNotNull(depart1);
            depart1 = service.getById(depart1Id, Department.class);
            assertNotNull(depart1);
            assertNotNull(depart1.getManager());
            assertEquals(john, depart1.getManager());

            departInfo = DepartmentManageService.DepartInfo.valueOf(depart1.getCode(),
                    depart1.getName(), depart1.getDesc(), depart1.getId(), null, Arrays.asList(), true);
            departService.saveDepartment(departInfo);
            depart1 = service.getById(depart1Id, Department.class);
            assertEquals(3, service.count(Department.class));
            assertNull(depart1.getManager());
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }

    private void testDeleteDepartment(GeneralDictAccessor service) {
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
