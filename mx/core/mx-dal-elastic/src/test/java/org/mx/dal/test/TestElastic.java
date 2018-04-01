package org.mx.dal.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mx.dal.EntityFactory;
import org.mx.dal.service.GeneralAccessor;
import org.mx.dal.test.entity.UserEntity;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class TestElastic {
    private AnnotationConfigApplicationContext context = null;

    @Before
    public void before() {
        context = new AnnotationConfigApplicationContext(TestConfig.class);
    }

    @After
    public void after() {
        context.close();
    }

    @Test
    public void test() {
        GeneralAccessor accessor = context.getBean("generalAccessorElastic", GeneralAccessor.class);
        assertNotNull(accessor);

        assertEquals(0, accessor.count(UserEntity.class));

        UserEntity user1 = EntityFactory.createEntity(UserEntity.class);
        user1.setAge(45);
        user1.setCode("john");
        user1.setName("John Peng");
        user1.setDesc("我是一个正高级工程师。");
        assertNull(user1.getId());
        UserEntity u1 = accessor.save(user1);
        assertNotNull(u1);
        assertNotNull(user1.getId());
        assertEquals(1, accessor.count(UserEntity.class));
        assertEquals(1, accessor.count(UserEntity.class, true));
        u1 = accessor.getById(user1.getId(), UserEntity.class);
        assertNotNull(u1);
        assertEquals(user1.getCode(), u1.getCode());

        UserEntity user2 = EntityFactory.createEntity(UserEntity.class);
        user2.setAge(16);
        user2.setCode("joy");
        user2.setName("Joy Peng");
        user2.setDesc("我是一个好学生。");
        assertNull(user2.getId());
        u1 = accessor.save(user2);
        assertNotNull(u1);
        assertNotNull(user2.getId());
        assertEquals(2, accessor.count(UserEntity.class));
        assertEquals(2, accessor.count(UserEntity.class, true));
        u1 = accessor.getById(user2.getId(), UserEntity.class);
        assertNotNull(u1);
        assertEquals(user2.getCode(), u1.getCode());

        u1.setAge(17);
        u1.setDesc(u1.getDesc() + "I am a student.");
        accessor.save(u1);
        assertEquals(2, accessor.count(UserEntity.class));
        assertEquals(2, accessor.count(UserEntity.class, true));
        u1 = accessor.getById(user2.getId(), UserEntity.class);
        assertNotNull(u1);
        assertEquals(user2.getCode(), u1.getCode());

        List<UserEntity> list = accessor.find(Arrays.asList(
                new GeneralAccessor.ConditionTuple("code", "john")), UserEntity.class);
        assertNotNull(list);
        assertEquals(1, list.size());
        list = accessor.find(Arrays.asList(
                new GeneralAccessor.ConditionTuple("name", "peng")), UserEntity.class);
        assertNotNull(list);
        assertEquals(2, list.size());
        list = accessor.find(Arrays.asList(
                new GeneralAccessor.ConditionTuple("name", "joy")), UserEntity.class);
        assertNotNull(list);
        assertEquals(1, list.size());
        list = accessor.find(Arrays.asList(
                new GeneralAccessor.ConditionTuple("desc", "高")), UserEntity.class);
        assertNotNull(list);
        assertEquals(1, list.size());list = accessor.find(Arrays.asList(
                new GeneralAccessor.ConditionTuple("desc", "student")), UserEntity.class);
        assertNotNull(list);
        assertEquals(1, list.size());

        accessor.remove(user1);
        assertEquals(1, accessor.count(UserEntity.class));
        assertEquals(1, accessor.count(UserEntity.class, true));
        assertEquals(2, accessor.count(UserEntity.class, false));

        accessor.remove(user1, false);
        accessor.remove(user2, false);
    }
}
