package org.mx.dal.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mx.DigestUtils;
import org.mx.dal.EntityFactory;
import org.mx.dal.entity.Base;
import org.mx.dal.service.ElasticAccessor;
import org.mx.dal.service.GeneralAccessor;
import org.mx.dal.test.entity.AnimalEntity;
import org.mx.dal.test.entity.UserEntity;
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
            assertEquals(0, accessor.count(UserEntity.class));

            UserEntity user1 = EntityFactory.createEntity(UserEntity.class);
            user1.setAge(45);
            user1.setCode("john");
            user1.setName("John Peng");
            user1.setDesc("我是一个正高级工程师。");
            assertNull(user1.getId());
            UserEntity u1 = accessor.save(user1);
            Thread.sleep(delay);
            assertNotNull(u1);
            assertNotNull(user1.getId());
            assertEquals(1, accessor.count(UserEntity.class));
            assertEquals(1, accessor.count(UserEntity.class, true));
            u1 = accessor.getById(user1.getId(), UserEntity.class);
            assertNotNull(u1);
            assertEquals(user1.getCode(), u1.getCode());

            UserEntity user2 = EntityFactory.createEntity(UserEntity.class);
            user2.setId(DigestUtils.uuid());
            user2.setAge(16);
            user2.setCode("joy");
            user2.setName("Joy Peng");
            user2.setDesc("我是一个好学生。");
            assertNotNull(user2.getId());
            u1 = accessor.save(user2);
            Thread.sleep(delay);
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
            Thread.sleep(delay);
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
                    new GeneralAccessor.ConditionTuple("name", "Peng")), UserEntity.class);
            assertNotNull(list);
            assertEquals(2, list.size());
            list = accessor.find(Arrays.asList(
                    new GeneralAccessor.ConditionTuple("name", "Joy")), UserEntity.class);
            assertNotNull(list);
            assertEquals(1, list.size());
            list = accessor.find(Arrays.asList(
                    new GeneralAccessor.ConditionTuple("desc", "高级工程师")), UserEntity.class);
            assertNotNull(list);
            assertEquals(1, list.size());
            list = accessor.find(Arrays.asList(
                    new GeneralAccessor.ConditionTuple("desc", "student")), UserEntity.class);
            assertNotNull(list);
            assertEquals(1, list.size());
            assertEquals(1, list.size());
            list = accessor.find(Arrays.asList(
                    new GeneralAccessor.ConditionTuple("desc", "学生")), UserEntity.class);
            assertNotNull(list);
            assertEquals(1, list.size());

            list = accessor.find(Arrays.asList(
                    new GeneralAccessor.ConditionTuple("age",
                            GeneralAccessor.ConditionTuple.ConditionOperate.LT, 100),
                    new GeneralAccessor.ConditionTuple("age",
                            GeneralAccessor.ConditionTuple.ConditionOperate.GT, 0)),
                    UserEntity.class);
            assertEquals(2, list.size());
            list = accessor.find(Arrays.asList(
                    new GeneralAccessor.ConditionTuple("age",
                            GeneralAccessor.ConditionTuple.ConditionOperate.LT, 45),
                    new GeneralAccessor.ConditionTuple("age",
                            GeneralAccessor.ConditionTuple.ConditionOperate.GT, 0)),
                    UserEntity.class);
            assertEquals(1, list.size());
            list = accessor.find(Arrays.asList(
                    new GeneralAccessor.ConditionTuple("age",
                            GeneralAccessor.ConditionTuple.ConditionOperate.LTE, 45),
                    new GeneralAccessor.ConditionTuple("age",
                            GeneralAccessor.ConditionTuple.ConditionOperate.GTE, 17)),
                    UserEntity.class);
            assertEquals(2, list.size());
            list = accessor.find(Arrays.asList(
                    new GeneralAccessor.ConditionTuple("age",
                            GeneralAccessor.ConditionTuple.ConditionOperate.LT, 45),
                    new GeneralAccessor.ConditionTuple("age",
                            GeneralAccessor.ConditionTuple.ConditionOperate.GT, 17)),
                    UserEntity.class);
            assertEquals(0, list.size());list = accessor.find(Arrays.asList(
                    new GeneralAccessor.ConditionTuple("age",
                            GeneralAccessor.ConditionTuple.ConditionOperate.LT, 100),
                    new GeneralAccessor.ConditionTuple("age",
                            GeneralAccessor.ConditionTuple.ConditionOperate.GT, 0),
                    new GeneralAccessor.ConditionTuple("name",
                            GeneralAccessor.ConditionTuple.ConditionOperate.EQ, "Joy")),
                    UserEntity.class);
            assertEquals(1, list.size());

            accessor.remove(user1);
            Thread.sleep(delay);
            assertEquals(1, accessor.count(UserEntity.class));
            assertEquals(1, accessor.count(UserEntity.class, true));
            assertEquals(2, accessor.count(UserEntity.class, false));

            accessor.remove(user1, false);
            accessor.remove(user2, false);
            Thread.sleep(delay);
            assertEquals(0, accessor.count(UserEntity.class));
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
            assertEquals(0, accessor.count(UserEntity.class));
            assertEquals(0, accessor.count(AnimalEntity.class));

            UserEntity user1 = EntityFactory.createEntity(UserEntity.class);
            user1.setAge(45);
            user1.setCode("john");
            user1.setName("John Peng");
            user1.setDesc("我是一个正高级工程师。");
            assertNull(user1.getId());
            UserEntity u1 = accessor.save(user1);
            Thread.sleep(delay);
            assertNotNull(u1);
            assertNotNull(user1.getId());
            assertEquals(1, accessor.count(UserEntity.class));
            assertEquals(1, accessor.count(UserEntity.class, true));
            u1 = accessor.getById(user1.getId(), UserEntity.class);
            assertNotNull(u1);
            assertEquals(user1.getCode(), u1.getCode());

            List<UserEntity> list = accessor.find(Arrays.asList(
                    new GeneralAccessor.ConditionTuple("code", "john")), UserEntity.class);
            assertNotNull(list);
            assertEquals(1, list.size());
            list = accessor.find(Arrays.asList(
                    new GeneralAccessor.ConditionTuple("name", "John")), UserEntity.class);
            assertNotNull(list);
            assertEquals(1, list.size());
            list = accessor.find(Arrays.asList(
                    new GeneralAccessor.ConditionTuple("name", "Peng")), UserEntity.class);
            assertNotNull(list);
            assertEquals(1, list.size());
            list = accessor.find(Arrays.asList(
                    new GeneralAccessor.ConditionTuple("desc", "高级工程师")), UserEntity.class);
            assertNotNull(list);
            assertEquals(1, list.size());

            AnimalEntity animal1 = EntityFactory.createEntity(AnimalEntity.class);
            animal1.setName("John is the owner of the tom cat.");
            accessor.save(animal1);
            Thread.sleep(delay);
            assertEquals(1, accessor.count(AnimalEntity.class));
            AnimalEntity a1 = accessor.getById(animal1.getId(), AnimalEntity.class);
            assertNotNull(a1);
            List<AnimalEntity> list1 = accessor.find(Collections.singletonList(
                    new GeneralAccessor.ConditionTuple("name", "John")), AnimalEntity.class);
            assertNotNull(list1);
            assertEquals(1, list1.size());
            list1 = accessor.find(Collections.singletonList(
                    new GeneralAccessor.ConditionTuple("name", "cat")), AnimalEntity.class);
            assertNotNull(list1);
            assertEquals(1, list1.size());

            List<GeneralAccessor.ConditionTuple> tuples = Collections.singletonList(
                    new GeneralAccessor.ConditionTuple("name", "John"));
            List<Base> list2 = ((ElasticAccessor) accessor).find(tuples,
                    Arrays.asList(UserEntity.class, AnimalEntity.class));
            assertEquals(2, list2.size());

            accessor.remove(user1, false);
            accessor.remove(animal1, false);
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }
}
