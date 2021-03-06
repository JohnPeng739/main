package org.mx.test;

import org.junit.Test;
import org.mx.DigestUtils;
import org.mx.dal.EntityFactory;
import org.mx.dal.service.GeneralAccessor;
import org.mx.dal.service.GeneralDictAccessor;
import org.mx.dal.service.JdbcBatchAccessor;
import org.mx.error.UserInterfaceException;
import org.mx.test.entity.User;
import org.mx.test.entity.UserEntity;
import org.mx.test.repository.UserRepository;
import org.mx.test.service.TransactionService;
import org.mx.test.service.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class TestDatabase extends BaseTest {

    @Test
    public void testTransaction() {
        GeneralDictAccessor accessor = context.getBean("generalDictAccessor",
                GeneralDictAccessor.class);
        assertNotNull(accessor);
        accessor.clear(User.class);
        assertEquals(0, accessor.count(User.class));
        TransactionService transactionService = context.getBean(TransactionService.class);
        assertNotNull(transactionService);

        transactionService.commit();
        assertTrue(transactionService.commitSuccess());

        try {
            transactionService.rollback();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        assertTrue(transactionService.rollbackSuccess());

        accessor.clear(User.class);
        assertEquals(0, accessor.count(User.class));
    }

    @Test
    public void testJdbcDataType() {
        // TODO 测试JDBC操作时的数据类型兼容性
    }

    @Test
    public void testPerformance() {
        GeneralDictAccessor accessor = context.getBean("generalDictAccessor",
                GeneralDictAccessor.class);
        assertNotNull(accessor);
        JdbcBatchAccessor jdbcBatchAccessor = context.getBean("jdbcBatchAccessor", JdbcBatchAccessor.class);
        assertNotNull(jdbcBatchAccessor);
        accessor.clear(User.class);

        // initialize
        List<User> users = new ArrayList<>(1000);
        for (int index = 0; index < 1000; index++) {
            User user = EntityFactory.createEntity(User.class);
            user.setId(null);
            user.setCode("john " + index);
            user.setName("John Peng");
            user.setAddress("address");
            user.setEmail("email");
            user.setPostCode("zip");
            user.setDesc("description");
            users.add(user);
        }

        // normal insert
        long t0 = System.currentTimeMillis();
        for (User user : users) {
            accessor.save(user);
        }
        long t1_1 = System.currentTimeMillis() - t0;
        assertEquals(1000, accessor.count(User.class));
        // normal update
        t0 = System.currentTimeMillis();
        for (User user : users) {
            user.setName(user.getName() + " test");
            accessor.save(user);
        }
        long t1_2 = System.currentTimeMillis() - t0;
        assertEquals(1000, accessor.count(User.class));
        // normal delete
        t0 = System.currentTimeMillis();
        for (User user : users) {
            accessor.remove(user, false);
        }
        long t1_3 = System.currentTimeMillis() - t0;
        assertEquals(0, accessor.count(User.class));

        users.clear();
        users = new ArrayList<>(1000);
        for (int index = 0; index < 1000; index++) {
            User user = EntityFactory.createEntity(User.class);
            user.setId(null);
            user.setCode("josh " + index);
            user.setName("josh Peng");
            user.setAddress("address");
            user.setEmail("email");
            user.setPostCode("zip");
            user.setDesc("description");
            users.add(user);
        }
        // batch insert
        t0 = System.currentTimeMillis();
        accessor.save(users);
        long t2_1 = System.currentTimeMillis() - t0;
        assertEquals(1000, accessor.count(User.class));
        // batch update
        t0 = System.currentTimeMillis();
        for (User user : users) {
            user.setName(user.getName() + " test");
        }
        accessor.save(users);
        long t2_2 = System.currentTimeMillis() - t0;
        assertEquals(1000, accessor.count(User.class));
        // batch delete
        t0 = System.currentTimeMillis();
        accessor.remove(users, false);
        long t2_3 = System.currentTimeMillis() - t0;
        assertEquals(0, accessor.count(User.class));

        users.clear();
        users = new ArrayList<>(1000);
        for (int index = 0; index < 1000; index++) {
            User user = EntityFactory.createEntity(User.class);
            user.setId(DigestUtils.uuid());
            user.setOperator("system");
            user.setCode("josh " + index);
            user.setName("josh Peng");
            user.setAddress("address");
            user.setEmail("email");
            user.setPostCode("zip");
            user.setDesc("description");
            users.add(user);
        }
        // batch insert
        t0 = System.currentTimeMillis();
        jdbcBatchAccessor.batchInsert(Arrays.asList("id", "code", "name", "address", "email", "postCode", "desc", "createdTime", "updatedTime", "operator", "valid"), users);
        long t3_1 = System.currentTimeMillis() - t0;
        assertEquals(1000, accessor.count(User.class));
        // batch update
        t0 = System.currentTimeMillis();
        for (User user : users) {
            user.setName(user.getName() + " test");
        }
        jdbcBatchAccessor.batchUpdate(Arrays.asList("id"), Arrays.asList("name"), users);
        long t3_2 = System.currentTimeMillis() - t0;
        assertEquals(1000, accessor.count(User.class));
        // batch delete
        t0 = System.currentTimeMillis();
        jdbcBatchAccessor.batchDelete(Arrays.asList("id"), users);
        long t3_3 = System.currentTimeMillis() - t0;
        assertEquals(0, accessor.count(User.class));
        System.out.println("Operate type\tinsert\t\tupdate\t\tdelete");
        System.out.println(String.format("Normal\t\t%6dms\t%6dms\t%6dms", t1_1, t1_2, t1_3));
        System.out.println(String.format("Batch1\t\t%6dms\t%6dms\t%6dms", t2_1, t2_2, t2_3));
        System.out.println(String.format("Batch2\t\t%6dms\t%6dms\t%6dms", t3_1, t3_2, t3_3));
        assertTrue(t2_1 <= t1_1);
    }

    @Test
    public void testUserInterface() {
        GeneralDictAccessor accessor = context.getBean("generalDictAccessor",
                GeneralDictAccessor.class);
        assertNotNull(accessor);

        try {
            accessor.clear(User.class);

            assertEquals(0, accessor.count(User.class));
            User user = EntityFactory.createEntity(User.class);
            user.setCode("john");
            user.setName("John Peng");
            user.setAddress("address");
            user.setEmail("email");
            user.setPostCode("zip");
            user.setDesc("description");
            User check = accessor.save(user);
            assertNotNull(check);
            assertNotNull(check.getId());
            assertEquals(user.getCode(), check.getCode());
            assertEquals(user.getName(), check.getName());
            assertEquals(user.getAddress(), check.getAddress());
            assertTrue(user.getCreatedTime() > 0);
            assertEquals(1, accessor.count(User.class));

            User user1 = EntityFactory.createEntity(User.class);
            user1.setId(null);
            user1.setCode("john");
            user1.setName("John Peng");
            user1.setAddress("address");
            user1.setEmail("email");
            user1.setPostCode("zip");
            user1.setDesc("description");
            User check1 = accessor.save(user1);
            assertNotNull(check1);
            assertEquals(check1.getId(), check.getId());
            assertEquals(1, accessor.count(User.class));

            check = accessor.getById(user.getId(), User.class);
            assertNotNull(check);
            assertEquals(user.getCode(), check.getCode());
            assertEquals(user.getName(), check.getName());

            check = accessor.getByCode(user.getCode(), User.class);
            assertNotNull(check);
            assertEquals(user.getId(), check.getId());
            assertEquals(user.getCode(), check.getCode());
            assertEquals(user.getName(), check.getName());

            user = accessor.getByCode("john", User.class);
            user.setCode("john-1");
            user.setName(user.getName() + "(editable)");
            user.setDesc("I'm John Peng.");
            check = accessor.save(user);
            assertNotNull(check);
            assertEquals(1, accessor.count(User.class));
            assertEquals(user.getId(), check.getId());
            assertEquals(user.getCode(), check.getCode());
            assertEquals(user.getName(), check.getName());
            assertEquals(user.getDesc(), check.getDesc());

            check = accessor.getByCode("john", User.class);
            assertNotNull(check);
            check = accessor.getById(user.getId(), User.class);
            assertNotNull(check);
            assertEquals(user.getCode(), check.getCode());

            check = accessor.getByCode("john-1", User.class);
            // 证明修改代码无效
            assertNull(check);

            String id = "id";
            user = EntityFactory.createEntity(User.class);
            user.setId(id);
            user.setCode("josh");
            user.setName("Josh Peng");
            check = accessor.save(user);
            assertNotNull(check);
            assertEquals(2, accessor.count(User.class));

            // 证明给定的ID是有效的
            check = accessor.getById(id, User.class);
            assertNotNull(check);
            assertEquals("josh", check.getCode());

            List<User> list = accessor.find(GeneralAccessor.ConditionGroup.and(
                    GeneralAccessor.ConditionGroup.or(
                            GeneralAccessor.ConditionTuple.eq("code", "josh"),
                            GeneralAccessor.ConditionTuple.eq("code", "john-1")
                    ),
                    GeneralAccessor.ConditionTuple.eq("valid", true)
            ), User.class);
            assertEquals(1, list.size());

            list = accessor.find(GeneralAccessor.ConditionGroup.and(
                    GeneralAccessor.ConditionGroup.or(
                            GeneralAccessor.ConditionTuple.eq("code", "josh"),
                            GeneralAccessor.ConditionTuple.eq("code", "john")
                    ),
                    GeneralAccessor.ConditionTuple.eq("valid", true)
            ), User.class);
            assertEquals(2, list.size());

            list = accessor.find(GeneralAccessor.ConditionGroup.and(
                    GeneralAccessor.ConditionTuple.contain("code", "jo"),
                    GeneralAccessor.ConditionTuple.eq("valid", true)
            ), User.class);
            assertEquals(2, list.size());

            list = accessor.find(GeneralAccessor.ConditionGroup.and(
                    GeneralAccessor.ConditionTuple.prefix("code", "jo"),
                    GeneralAccessor.ConditionTuple.eq("valid", true)
            ), User.class);
            assertEquals(2, list.size());

            list = accessor.find(GeneralAccessor.ConditionGroup.and(
                    GeneralAccessor.ConditionTuple.contain("code", "o"),
                    GeneralAccessor.ConditionTuple.eq("valid", true)
            ), User.class);
            assertEquals(2, list.size());

            list = accessor.find(GeneralAccessor.ConditionGroup.and(
                    GeneralAccessor.ConditionTuple.prefix("code", "o"),
                    GeneralAccessor.ConditionTuple.eq("valid", true)
            ), User.class);
            assertEquals(0, list.size());

            list = accessor.find(GeneralAccessor.ConditionTuple.isNull("code"), User.class);
            assertEquals(0, list.size());

            list = accessor.find(GeneralAccessor.ConditionTuple.isNotNull("code"), User.class);
            assertEquals(2, list.size());

            check = accessor.getByCode("john", User.class);
            assertNotNull(check);
            assertTrue(check.isValid());
            check = accessor.getByCode("josh", User.class);
            assertNotNull(check);
            assertTrue(check.isValid());

            user = accessor.getByCode("john", User.class);
            assertNotNull(user);
            check = accessor.remove(user);
            assertNotNull(check);
            assertEquals(1, accessor.count(User.class));
            assertEquals(2, accessor.count(User.class, false));
            assertFalse(check.isValid());

            list = accessor.find(GeneralAccessor.ConditionGroup.and(
                    GeneralAccessor.ConditionTuple.eq("code", "john"),
                    GeneralAccessor.ConditionTuple.eq("valid", true)
            ), User.class);
            assertEquals(0, list.size());
            list = accessor.find(GeneralAccessor.ConditionGroup.and(
                    GeneralAccessor.ConditionTuple.eq("code", "josh"),
                    GeneralAccessor.ConditionTuple.eq("valid", true)
            ), User.class);
            assertEquals(1, list.size());

            user = accessor.getByCode("john", User.class);
            check = accessor.remove(user, false);
            assertNotNull(check);
            assertEquals(1, accessor.count(User.class));
            check = accessor.getById(user.getId(), User.class);
            assertNull(check);
            check = accessor.getByCode(user.getCode(), User.class);
            assertNull(check);
            check = accessor.getByCode("josh", User.class);
            assertNotNull(check);

            accessor.clear(User.class);
            assertEquals(0, accessor.count(User.class));
        } catch (UserInterfaceException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testUserRepository() {
        GeneralDictAccessor accessor = context.getBean("generalDictAccessor",
                GeneralDictAccessor.class);
        assertNotNull(accessor);
        accessor.clear(User.class);
        UserRepository repository = context.getBean(UserRepository.class);
        assertNotNull(repository);

        assertEquals(0, accessor.count(User.class));
        User user = EntityFactory.createEntity(User.class);
        user.setCode("john");
        user.setName("John Peng");
        user.setAddress("address");
        user.setEmail("john@hotmail.com");
        user.setPostCode("zip");
        user.setDesc("description");
        User check = accessor.save(user);
        assertNotNull(check);
        assertNotNull(check.getId());
        assertEquals(user.getCode(), check.getCode());
        assertEquals(user.getName(), check.getName());
        assertEquals(user.getAddress(), check.getAddress());
        assertTrue(user.getCreatedTime() > 0);
        assertEquals(1, accessor.count(User.class));

        user = EntityFactory.createEntity(User.class);
        user.setId(DigestUtils.uuid());
        user.setCode("josh");
        user.setName("Josh Peng");
        user.setEmail("joy@hotmail.com");
        check = accessor.save(user);
        assertNotNull(check);
        assertEquals(2, accessor.count(User.class));

        List<UserEntity> list = repository.getByCode("john");
        assertEquals(1, list.size());

        list = repository.getLikeEmail("john");
        assertEquals(1, list.size());

        list = repository.getLikeEmail("hotmail");
        assertEquals(2, list.size());

        List<User> removes = accessor.list(User.class);
        for (User remove : removes) {
            accessor.remove(remove, false);
        }
        assertEquals(0, accessor.count(User.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testParentChildren() {
        GeneralDictAccessor accessor = context.getBean("generalDictAccessor",
                GeneralDictAccessor.class);
        assertNotNull(accessor);
        accessor.clear(User.class);
        UserService userService = context.getBean(UserService.class);
        assertNotNull(userService);

        try {
            User root = EntityFactory.createEntity(User.class);
            root.setCode("root");
            root.setName("root");
            accessor.save(root);
            root = userService.getUserById(root.getId());
            assertNotNull(root);
            assertNotNull(root.getId());
            assertNull(root.getParent());
            assertTrue(root.getChildren() == null || root.getChildren().isEmpty());

            User item01 = EntityFactory.createEntity(User.class);
            item01.setCode("item01");
            item01.setName("item01");
            item01.setParent(accessor.getById(root.getId(), User.class));
            accessor.save(item01);
            item01 = userService.getUserById(item01.getId());
            assertNotNull(item01);
            assertNotNull(item01.getId());
            assertNotNull(item01.getParent());
            assertTrue(item01.getChildren() == null || item01.getChildren().isEmpty());
            User item02 = EntityFactory.createEntity(User.class);
            item02.setCode("item02");
            item02.setName("item02");
            item02.setParent(accessor.getById(root.getId(), User.class));
            accessor.save(item02);
            item02 = userService.getUserById(item02.getId());
            assertNotNull(item02);
            assertNotNull(item02.getId());
            assertNotNull(item02.getParent());
            assertTrue(item02.getChildren() == null || item02.getChildren().isEmpty());
            User checkRoot = userService.getUserById(root.getId());
            assertNotNull(checkRoot);
            assertNull(checkRoot.getParent());
            assertEquals(2, checkRoot.getChildren().size());

            User item0101 = EntityFactory.createEntity(User.class);
            item0101.setCode("item0101");
            item0101.setName("item0101");
            item0101.setParent(item01);
            accessor.save(item0101);
            assertEquals(4, accessor.count(User.class));
            checkRoot = userService.getUserById(root.getId());
            assertNotNull(checkRoot);
            assertNull(checkRoot.getParent());
            assertEquals(2, checkRoot.getChildren().size());
            User checkItem01 = userService.getUserByCode("item01");
            assertNotNull(checkItem01);
            assertNotNull(checkItem01.getParent());
            assertEquals(root.getCode(), checkItem01.getParent().getCode());
            assertEquals(root.getId(), checkItem01.getParentId());
            assertEquals(1, checkItem01.getChildren().size());

            accessor.remove(item0101, false);
            accessor.remove(item02, false);
            assertEquals(0, accessor.count(User.class));
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }
}
