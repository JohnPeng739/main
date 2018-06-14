package org.mx.dal.service;

import org.junit.Test;
import org.mx.DigestUtils;
import org.mx.dal.BaseTest;
import org.mx.dal.EntityFactory;
import org.mx.dal.entity.User;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class TestDatabase extends BaseTest {
    @SuppressWarnings("unchecked")
    @Test
    public void testUserInterface() {
        GeneralDictAccessor accessor = context.getBean("generalDictAccessor",
                GeneralDictAccessor.class);
        assertNotNull(accessor);
        GeneralTextSearchAccessor searchAccessor = context.getBean("generalDictAccessor",
                GeneralTextSearchAccessor.class);

        try {
            accessor.clear(User.class);

            assertEquals(0, accessor.count(User.class));
            User user = EntityFactory.createEntity(User.class);
            user.setCode("john");
            user.setName("John Peng");
            user.setAddress("some address is here, 中华人民共和国，美利坚合众国。");
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

            check = accessor.getById(user.getId(), User.class);
            assertNotNull(check);
            assertEquals(user.getCode(), check.getCode());
            assertEquals(user.getName(), check.getName());

            check = accessor.getByCode(user.getCode(), User.class);
            assertNotNull(check);
            assertEquals(user.getId(), check.getId());
            assertEquals(user.getCode(), check.getCode());
            assertEquals(user.getName(), check.getName());

            // test text search
            List<User> users = searchAccessor.search("address", true, User.class);
            assertNotNull(user);
            assertEquals(1, users.size());
            assertNotNull(users.get(0));
            assertEquals(check.getId(), users.get(0).getId());
            users = searchAccessor.search(Arrays.asList("address", "here", "some"), true, User.class);
            assertNotNull(user);
            assertEquals(1, users.size());
            assertNotNull(users.get(0));
            assertEquals(check.getId(), users.get(0).getId());
            users = searchAccessor.search("\"Josh Peng\"", true, User.class);
            assertNotNull(user);
            assertEquals(0, users.size());
            users = searchAccessor.search("中华人民共和国", true, User.class);
            assertNotNull(user);
            assertEquals(1, users.size());
            assertNotNull(users.get(0));
            assertEquals(check.getId(), users.get(0).getId());

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

            user = EntityFactory.createEntity(User.class);
            user.setId(DigestUtils.uuid());
            user.setCode("josh");
            user.setName("Josh Peng");
            check = accessor.save(user);
            assertNotNull(check);
            assertEquals(2, accessor.count(User.class));

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

            List<User> list = accessor.find(Arrays.asList(
                    new GeneralAccessor.ConditionTuple("code", "john"),
                    new GeneralAccessor.ConditionTuple("valid", true)
            ), User.class);
            assertEquals(0, list.size());
            list = accessor.find(Arrays.asList(
                    new GeneralAccessor.ConditionTuple("code", "josh"),
                    new GeneralAccessor.ConditionTuple("valid", true)
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
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testParentChildren() {
        GeneralDictAccessor accessor = context.getBean("generalDictAccessor",
                GeneralDictAccessor.class);
        assertNotNull(accessor);

        try {
            assertEquals(0, accessor.count(User.class));
            User root = EntityFactory.createEntity(User.class);
            root.setCode("root");
            root.setName("root");
            accessor.save(root);
            root = accessor.getById(root.getId(), User.class);
            assertNotNull(root);
            assertNotNull(root.getId());
            assertNull(root.getParent());
            assertTrue(root.getChildren() == null || root.getChildren().isEmpty());

            User item01 = EntityFactory.createEntity(User.class);
            item01.setCode("item01");
            item01.setName("item01");
            item01.setParent(accessor.getById(root.getId(), User.class));
            accessor.save(item01);
            item01 = accessor.getById(item01.getId(), User.class);
            assertNotNull(item01);
            assertNotNull(item01.getId());
            assertNotNull(item01.getParent());
            assertTrue(item01.getChildren() == null || item01.getChildren().isEmpty());
            User item02 = EntityFactory.createEntity(User.class);
            item02.setCode("item02");
            item02.setName("item02");
            item02.setParent(accessor.getById(root.getId(), User.class));
            accessor.save(item02);
            item02 = accessor.getById(item02.getId(), User.class);
            assertNotNull(item02);
            assertNotNull(item02.getId());
            assertNotNull(item02.getParent());
            assertTrue(item02.getChildren() == null || item02.getChildren().isEmpty());
            User checkRoot = accessor.getById(root.getId(), User.class);
            assertNotNull(checkRoot);
            assertNull(checkRoot.getParent());
            assertEquals(2, checkRoot.getChildren().size());

            User item0101 = EntityFactory.createEntity(User.class);
            item0101.setCode("item0101");
            item0101.setName("item0101");
            item0101.setParent(item01);
            accessor.save(item0101);
            assertEquals(4, accessor.count(User.class));
            checkRoot = accessor.getById(root.getId(), User.class);
            assertNotNull(checkRoot);
            assertNull(checkRoot.getParent());
            assertEquals(2, checkRoot.getChildren().size());
            User checkItem01 = accessor.getByCode("item01", User.class);
            assertNotNull(checkItem01);
            assertNotNull(checkItem01.getParent());
            assertEquals(root.getCode(), checkItem01.getParent().getCode());
            assertEquals(root.getId(), checkItem01.getParentId());
            assertEquals(1, checkItem01.getChildren().size());

            accessor.remove(item0101, false);
            accessor.remove(item01, false);
            accessor.remove(item02, false);
            accessor.remove(root, false);
            assertEquals(0, accessor.count(User.class));
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }
}
