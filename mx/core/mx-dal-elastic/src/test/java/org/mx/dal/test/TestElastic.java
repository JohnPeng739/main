package org.mx.dal.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mx.DigestUtils;
import org.mx.dal.EntityFactory;
import org.mx.dal.entity.Base;
import org.mx.dal.service.ElasticAccessor;
import org.mx.dal.service.GeneralAccessor;
import org.mx.dal.test.entity.AnimalEntityElastic;
import org.mx.dal.test.entity.UserEntityElastic;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;
import java.util.Collections;
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

        try {
            long delay = 1000;
            assertEquals(0, accessor.count(UserEntityElastic.class));

            UserEntityElastic user1 = EntityFactory.createEntity(UserEntityElastic.class);
            user1.setAge(45);
            user1.setCode("john");
            user1.setName("John Peng");
            user1.setDesc("我是一个正高级工程师。");
            assertNull(user1.getId());
            UserEntityElastic u1 = accessor.save(user1);
            Thread.sleep(delay);
            assertNotNull(u1);
            assertNotNull(user1.getId());
            assertEquals(1, accessor.count(UserEntityElastic.class));
            assertEquals(1, accessor.count(UserEntityElastic.class, true));
            u1 = accessor.getById(user1.getId(), UserEntityElastic.class);
            assertNotNull(u1);
            assertEquals(user1.getCode(), u1.getCode());

            UserEntityElastic user2 = EntityFactory.createEntity(UserEntityElastic.class);
            user2.setId(DigestUtils.uuid());
            user2.setAge(16);
            user2.setCode("彭明喜");
            user2.setName("Joy Peng");
            user2.setDesc("我是一个好学生。");
            assertNotNull(user2.getId());
            u1 = accessor.save(user2);
            Thread.sleep(delay);
            assertNotNull(u1);
            assertNotNull(user2.getId());
            assertEquals(2, accessor.count(UserEntityElastic.class));
            assertEquals(2, accessor.count(UserEntityElastic.class, true));
            u1 = accessor.getById(user2.getId(), UserEntityElastic.class);
            assertNotNull(u1);
            assertEquals(user2.getCode(), u1.getCode());

            u1.setAge(17);
            u1.setDesc(u1.getDesc() + "I am a student.");
            accessor.save(u1);
            Thread.sleep(delay);
            assertEquals(2, accessor.count(UserEntityElastic.class));
            assertEquals(2, accessor.count(UserEntityElastic.class, true));
            u1 = accessor.getById(user2.getId(), UserEntityElastic.class);
            assertNotNull(u1);
            assertEquals(user2.getCode(), u1.getCode());

            List<UserEntityElastic> list = accessor.find(Arrays.asList(
                    GeneralAccessor.ConditionTuple.eq("code", "john")), UserEntityElastic.class);
            assertNotNull(list);
            assertEquals(1, list.size());
            list = accessor.find(Arrays.asList(
                    GeneralAccessor.ConditionTuple.contain("name", "Peng")), UserEntityElastic.class);
            assertNotNull(list);
            assertEquals(2, list.size());
            list = accessor.find(Arrays.asList(
                    GeneralAccessor.ConditionTuple.contain("name", "Joy")), UserEntityElastic.class);
            assertNotNull(list);
            assertEquals(1, list.size());
            list = accessor.find(Arrays.asList(
                    GeneralAccessor.ConditionTuple.contain("desc", "高级工程师")), UserEntityElastic.class);
            assertNotNull(list);
            assertEquals(1, list.size());
            list = accessor.find(Arrays.asList(
                    GeneralAccessor.ConditionTuple.contain("desc", "student")), UserEntityElastic.class);
            assertNotNull(list);
            assertEquals(1, list.size());
            assertEquals(1, list.size());
            list = accessor.find(Arrays.asList(
                    GeneralAccessor.ConditionTuple.contain("desc", "学生")), UserEntityElastic.class);
            assertNotNull(list);
            assertEquals(1, list.size());

            list = accessor.find(Arrays.asList(
                    GeneralAccessor.ConditionTuple.lt("age", 100),
                    GeneralAccessor.ConditionTuple.gt("age", 0)),
                    UserEntityElastic.class);
            assertEquals(2, list.size());
            list = accessor.find(Arrays.asList(
                    GeneralAccessor.ConditionTuple.lt("age", 45),
                    GeneralAccessor.ConditionTuple.gt("age", 0)),
                    UserEntityElastic.class);
            assertEquals(1, list.size());
            list = accessor.find(Arrays.asList(
                    GeneralAccessor.ConditionTuple.lte("age", 45),
                    GeneralAccessor.ConditionTuple.gte("age", 17)),
                    UserEntityElastic.class);
            assertEquals(2, list.size());
            list = accessor.find(Arrays.asList(
                    GeneralAccessor.ConditionTuple.lt("age", 45),
                    GeneralAccessor.ConditionTuple.gt("age", 17)),
                    UserEntityElastic.class);
            assertEquals(0, list.size());
            list = accessor.find(Arrays.asList(
                    GeneralAccessor.ConditionTuple.eq("code", "彭明喜"),
                    GeneralAccessor.ConditionTuple.lt("age", 100),
                    GeneralAccessor.ConditionTuple.gt("age", 0),
                    GeneralAccessor.ConditionTuple.contain("name", "Joy")),
                    UserEntityElastic.class);
            assertEquals(1, list.size());
            list = accessor.find(Arrays.asList(
                    GeneralAccessor.ConditionTuple.eq("code", "joy"),
                    GeneralAccessor.ConditionTuple.lt("age", 100),
                    GeneralAccessor.ConditionTuple.gt("age", 0),
                    GeneralAccessor.ConditionTuple.contain("name", "Joy")),
                    UserEntityElastic.class);
            assertEquals(0, list.size());

            accessor.remove(user1);
            Thread.sleep(delay);
            assertEquals(1, accessor.count(UserEntityElastic.class));
            assertEquals(1, accessor.count(UserEntityElastic.class, true));
            assertEquals(2, accessor.count(UserEntityElastic.class, false));

            accessor.remove(user1, false);
            accessor.remove(user2, false);
            Thread.sleep(delay);
            assertEquals(0, accessor.count(UserEntityElastic.class));
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void test2Index() {
        GeneralAccessor accessor = context.getBean("generalAccessorElastic", GeneralAccessor.class);
        assertNotNull(accessor);

        try {
            long delay = 1000;
            assertEquals(0, accessor.count(UserEntityElastic.class));
            assertEquals(0, accessor.count(AnimalEntityElastic.class));

            UserEntityElastic user1 = EntityFactory.createEntity(UserEntityElastic.class);
            user1.setAge(45);
            user1.setCode("john");
            user1.setName("John Peng");
            user1.setDesc("我是一个正高级工程师。");
            assertNull(user1.getId());
            UserEntityElastic u1 = accessor.save(user1);
            Thread.sleep(delay);
            assertNotNull(u1);
            assertNotNull(user1.getId());
            assertEquals(1, accessor.count(UserEntityElastic.class));
            assertEquals(1, accessor.count(UserEntityElastic.class, true));
            u1 = accessor.getById(user1.getId(), UserEntityElastic.class);
            assertNotNull(u1);
            assertEquals(user1.getCode(), u1.getCode());

            List<UserEntityElastic> list = accessor.find(Arrays.asList(
                    new GeneralAccessor.ConditionTuple("code", "john")), UserEntityElastic.class);
            assertNotNull(list);
            assertEquals(1, list.size());
            list = accessor.find(Arrays.asList(
                    new GeneralAccessor.ConditionTuple("name", "John")), UserEntityElastic.class);
            assertNotNull(list);
            assertEquals(1, list.size());
            list = accessor.find(Arrays.asList(
                    new GeneralAccessor.ConditionTuple("name", "Peng")), UserEntityElastic.class);
            assertNotNull(list);
            assertEquals(1, list.size());
            list = accessor.find(Arrays.asList(
                    new GeneralAccessor.ConditionTuple("desc", "高级工程师")), UserEntityElastic.class);
            assertNotNull(list);
            assertEquals(1, list.size());

            AnimalEntityElastic animal1 = EntityFactory.createEntity(AnimalEntityElastic.class);
            animal1.setName("John is the owner of the tom cat.");
            accessor.save(animal1);
            Thread.sleep(delay);
            assertEquals(1, accessor.count(AnimalEntityElastic.class));
            AnimalEntityElastic a1 = accessor.getById(animal1.getId(), AnimalEntityElastic.class);
            assertNotNull(a1);
            List<AnimalEntityElastic> list1 = accessor.find(Collections.singletonList(
                    new GeneralAccessor.ConditionTuple("name", "John")), AnimalEntityElastic.class);
            assertNotNull(list1);
            assertEquals(1, list1.size());
            list1 = accessor.find(Collections.singletonList(
                    new GeneralAccessor.ConditionTuple("name", "cat")), AnimalEntityElastic.class);
            assertNotNull(list1);
            assertEquals(1, list1.size());

            List<GeneralAccessor.ConditionTuple> tuples = Collections.singletonList(
                    new GeneralAccessor.ConditionTuple("name", "John"));
            List<Base> list2 = ((ElasticAccessor) accessor).find(tuples,
                    Arrays.asList(UserEntityElastic.class, AnimalEntityElastic.class));
            assertEquals(2, list2.size());

            accessor.remove(user1, false);
            accessor.remove(animal1, false);
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }
}
