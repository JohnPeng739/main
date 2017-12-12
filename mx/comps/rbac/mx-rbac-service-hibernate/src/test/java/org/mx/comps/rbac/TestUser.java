package org.mx.comps.rbac;

import org.junit.Test;
import org.mx.DigestUtils;
import org.mx.comps.rbac.dal.entity.Account;
import org.mx.comps.rbac.dal.entity.Department;
import org.mx.comps.rbac.dal.entity.User;
import org.mx.comps.rbac.error.UserInterfaceRbacErrorException;
import org.mx.comps.rbac.service.AccountManageService;
import org.mx.comps.rbac.service.DepartmentManageService;
import org.mx.comps.rbac.service.UserManageService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;

/**
 * 用户实体单元测试类
 *
 * @author : john.peng created on date : 2017/11/28
 */
public class TestUser extends BaseTest {
    public static String johnId, joyId, joshId;

    public static void testInsertUser(UserManageService service) throws ParseException {
        long birthday = new SimpleDateFormat("yyyy-MM-dd").parse("1973-09-18").getTime();
        UserManageService.UserInfo userInfo = UserManageService.UserInfo.valueOf("喜", "明",
                "彭", User.Sex.MALE, "", birthday, "", "manager", true,
                "This is John.Peng.");
        User john = service.saveUser(userInfo);
        assertEquals(1, service.count(User.class));
        johnId = john.getId();
        assertNotNull(john);
        assertNotNull(john.getId());
        assertEquals(john.getFullName(), "彭 明喜");
        assertEquals(john.getLastName(), "彭");
        assertEquals(john.getStation(), "manager");
        assertEquals(john.getSex(), User.Sex.MALE);
        assertEquals(john.getDesc(), "This is John.Peng.");
        assertEquals(birthday, john.getBirthday().getTime());
        assertTrue(john.getCreatedTime() > 0);
        assertTrue(john.getUpdatedTime() > 0);
        assertEquals(john.getOperator(), "admin");
        assertTrue(john.isValid());
        assertEquals(service.count(User.class), 1);
        john = service.getById(john.getId(), User.class);
        assertNotNull(john);
        assertNotNull(john.getId());
        assertEquals(john.getFullName(), "彭 明喜");
        assertEquals(john.getLastName(), "彭");
        assertEquals(john.getStation(), "manager");
        assertEquals(john.getSex(), User.Sex.MALE);
        assertEquals(john.getDesc(), "This is John.Peng.");
        assertEquals(birthday, john.getBirthday().getTime());
        assertTrue(john.getCreatedTime() > 0);
        assertTrue(john.getUpdatedTime() > 0);
        assertEquals(john.getOperator(), "admin");
        assertTrue(john.isValid());
        assertEquals(service.count(User.class), 1);
        assertEquals(service.count(User.class, true), 1);
        assertEquals(service.count(User.class, false), 1);

        userInfo = UserManageService.UserInfo.valueOf("joy", "",
                "peng", User.Sex.FEMALE);
        User joy = service.saveUser(userInfo);
        joyId = joy.getId();
        assertEquals(2, service.count(User.class));
        assertNotNull(joy);
        assertNotNull(joy.getId());
        assertEquals(joy.getFullName(), "peng joy");
        joy = service.getById(joy.getId(), User.class);
        assertNotNull(joy);
        assertNotNull(joy.getId());
        assertEquals(joy.getFullName(), "peng joy");
    }

    public static void testEditUser(UserManageService service) {
        UserManageService.UserInfo userInfo = UserManageService.UserInfo.valueOf("josh", "",
                "peng", User.Sex.MALE, "", -1, "", "", true,
                "original desc.");
        User josh = service.saveUser(userInfo);
        joshId = josh.getId();
        assertNotNull(josh);
        assertNotNull(josh.getId());
        assertEquals(service.count(User.class), 3);
        userInfo = UserManageService.UserInfo.valueOf(josh.getFirstName(), josh.getMiddleName(),
                josh.getLastName(), josh.getSex(), josh.getId(), -1, "", "", false,
                "new desc.");
        josh = service.saveUser(userInfo);
        assertNotNull(josh);
        assertNotNull(josh.getId());
        assertEquals(service.count(User.class), 2);
        assertEquals(josh.getDesc(), "new desc.");
        assertEquals(service.count(User.class, true), 2);
        assertEquals(service.count(User.class, false), 3);

        userInfo = UserManageService.UserInfo.valueOf(josh.getFirstName(), josh.getMiddleName(),
                josh.getLastName(), josh.getSex(), josh.getId(), -1, "", "", true,
                josh.getDesc());
        service.saveUser(userInfo);
        assertEquals(service.count(User.class), 3);
        assertEquals(service.count(User.class, true), 3);
        assertEquals(service.count(User.class, false), 3);
    }

    @Test
    public void testUserCrud() {
        UserManageService service = context.getBean("userManageService", UserManageService.class);
        assertNotNull(service);

        try {
            assertEquals(service.count(User.class), 0);
            // insert
            testInsertUser(service);
            // edit
            testEditUser(service);
            // delete
            testDeleteUser(service);
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testUserDepartment() {
        UserManageService service = context.getBean("userManageService", UserManageService.class);
        assertNotNull(service);
        DepartmentManageService departManageService = context.getBean("departmentManageService", DepartmentManageService.class);
        assertNotNull(departManageService);

        try {
            TestDepartment.testInsertDepartment(departManageService);
            TestDepartment.testEditDepartment(departManageService);
            testInsertUser(service);
            testEditUser(service);
            assertEquals(3, departManageService.count(Department.class));
            assertEquals(3, service.count(User.class));

            User joy = service.getById(joyId, User.class);
            assertNotNull(joy);
            assertNull(joy.getDepartment());
            User josh = service.getById(joshId, User.class);
            assertNotNull(josh);
            assertNull(josh.getDepartment());
            Department depart1 = departManageService.getById(TestDepartment.depart1Id, Department.class);
            assertNotNull(depart1);
            joy.setDepartment(depart1);
            service.save(joy);
            joy = service.getById(joyId, User.class);
            assertNotNull(joy);
            assertNotNull(joy.getDepartment());
            assertEquals(depart1, joy.getDepartment());
            depart1 = departManageService.getById(TestDepartment.depart1Id, Department.class);
            assertNotNull(depart1);
            assertEquals(1, depart1.getEmployees().size());
            assertEquals(new HashSet<>(Arrays.asList(joy)), depart1.getEmployees());

            josh.setDepartment(depart1);
            service.save(josh);
            josh = service.getById(joshId, User.class);
            assertNotNull(josh);
            assertNotNull(josh.getDepartment());
            assertEquals(depart1, josh.getDepartment());
            depart1 = departManageService.getById(TestDepartment.depart1Id, Department.class);
            assertNotNull(depart1);
            assertEquals(2, depart1.getEmployees().size());
            assertEquals(new HashSet<>(Arrays.asList(joy, josh)), depart1.getEmployees());

            joy.setDepartment(null);
            service.save(joy);
            joy = service.getById(joyId, User.class);
            assertNotNull(joy);
            assertNull(joy.getDepartment());
            depart1 = departManageService.getById(TestDepartment.depart1Id, Department.class);
            assertNotNull(depart1);
            assertEquals(1, depart1.getEmployees().size());
            assertEquals(new HashSet<>(Arrays.asList(josh)), depart1.getEmployees());
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testAllocateAccount() {
        UserManageService service = context.getBean("userManageService", UserManageService.class);
        assertNotNull(service);
        AccountManageService accountManageService = context.getBean("accountManageService", AccountManageService.class);
        assertNotNull(accountManageService);
        try {
            testInsertUser(service);
            testEditUser(service);
            assertEquals(3, service.count(User.class));

            User john = service.getById(joshId, User.class);
            assertNotNull(john);
            assertEquals(0, accountManageService.count(Account.class));

            // 用户不存在
            AccountManageService.AccountInfo accountInfo = AccountManageService.AccountInfo.valueOf("john---",
                    "password", "desc", "", "asdfasd", Arrays.asList(), true);
            try {
                service.allocateAccount(accountInfo);
                fail("here need a exception");
            } catch (UserInterfaceRbacErrorException ex) {
                assertEquals(UserInterfaceRbacErrorException.RbacErrors.USER_NOT_FOUND.getErrorCode(), ex.getErrorCode());
            }

            // 正常创建
            accountInfo = AccountManageService.AccountInfo.valueOf("John.Peng",
                    "edmund!@#123", "desc", "", john.getId(), Arrays.asList(), true);
            Account account = service.allocateAccount(accountInfo);
            assertNotNull(account);
            assertEquals(3, service.count(User.class));
            assertEquals(1, accountManageService.count(Account.class));
            account = accountManageService.getByCode("John.Peng", Account.class);
            assertNotNull(account);
            assertNotNull(account.getOwner());
            assertEquals(john, account.getOwner());
            assertEquals(DigestUtils.md5("edmund!@#123"), account.getPassword());
            assertEquals(john.getFullName(), account.getName());
            assertEquals("desc", account.getDesc());
            assertEquals(0, account.getRoles().size());

            // 账户已存在
            try {
                service.allocateAccount(accountInfo);
                fail("here need a exception");
            } catch (UserInterfaceRbacErrorException ex) {
                assertEquals(UserInterfaceRbacErrorException.RbacErrors.ACCOUNT_HAS_EXIST.getErrorCode(), ex.getErrorCode());
            }
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }

    private void testDeleteUser(UserManageService service) {
        List<User> users = service.list(User.class);
        int userNum = users.size();
        for (User user : users) {
            service.remove(user);
            assertEquals(service.count(User.class), --userNum);
            assertEquals(service.count(User.class, false), users.size());
        }
        userNum = users.size();
        for (User user : users) {
            service.remove(user, false);
            assertEquals(service.count(User.class, false), --userNum);
        }
    }
}
