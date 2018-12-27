package org.mx.dal.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mx.DigestUtils;
import org.mx.dal.EntityFactory;
import org.mx.dal.entity.GeoPointLocation;
import org.mx.dal.service.ElasticAccessor;
import org.mx.dal.service.GeneralAccessor;
import org.mx.dal.test.entity.AnimalEntityElastic;
import org.mx.dal.test.entity.UserEntityElastic;
import org.mx.dal.test.entity.WeatherEntityElastic;
import org.mx.dal.utils.ElasticUtil;
import org.mx.hanlp.utils.HanlpUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class TestElastic {
    private AnnotationConfigApplicationContext context = null;

    @Before
    public void before() {
        // 先清理，再重建
        context = new AnnotationConfigApplicationContext(TestConfig.class);
        GeneralAccessor accessor = context.getBean("generalAccessorElastic", GeneralAccessor.class);
        assertNotNull(accessor);
        accessor.clear(UserEntityElastic.class);
        accessor.clear(AnimalEntityElastic.class);
        accessor.clear(WeatherEntityElastic.class);
        context.close();
        context = new AnnotationConfigApplicationContext(TestConfig.class);
    }

    @After
    public void after() {
        // 先清理，再关闭
        GeneralAccessor accessor = context.getBean("generalAccessorElastic", GeneralAccessor.class);
        assertNotNull(accessor);
        context.close();
    }

    @Test
    public void testGeo() {
        GeneralAccessor accessor = context.getBean("generalAccessorElastic", GeneralAccessor.class);
        assertNotNull(accessor);
        ElasticUtil elasticUtil = context.getBean(ElasticUtil.class);
        assertNotNull(elasticUtil);
        elasticUtil.deleteIndex(UserEntityElastic.class);

        try {
            WeatherEntityElastic w1 = EntityFactory.createEntity(WeatherEntityElastic.class);
            w1.setLocation(new GeoPointLocation(0, 0));
            w1.setName("原点");
            w1.setType("晴天");
            w1.setTemperature(38.0f);
            accessor.save(w1);

            WeatherEntityElastic w2 = EntityFactory.createEntity(WeatherEntityElastic.class);
            w2.setName("地方1");
            w2.setTemperature(22.0f);
            w2.setType("阴天");
            w2.setLocation(new GeoPointLocation(10, 45));
            accessor.save(w2);

            Thread.sleep(1000);

            List<WeatherEntityElastic> list = elasticUtil.geoNearBy(new GeoPointLocation(1, 1),
                    200 * 1000, null, Collections.singletonList(WeatherEntityElastic.class));
            assertEquals(1, list.size());
            list = elasticUtil.geoNearBy(new GeoPointLocation(1, 1),
                    5000 * 1000, null, Collections.singletonList(WeatherEntityElastic.class));
            assertEquals(2, list.size());

            list = elasticUtil.geoWithInPolygon(null, Arrays.asList(
                    new GeoPointLocation(0, 0),
                    new GeoPointLocation(20, 0),
                    new GeoPointLocation(20, 30),
                    new GeoPointLocation(0, 30)
            ), null, Collections.singletonList(WeatherEntityElastic.class));
            assertEquals(1, list.size());
            list = elasticUtil.geoWithInPolygon(new GeoPointLocation(10, 25), Arrays.asList(
                    new GeoPointLocation(0, 0),
                    new GeoPointLocation(20, 0),
                    new GeoPointLocation(20, 50),
                    new GeoPointLocation(0, 50)
            ), null, Collections.singletonList(WeatherEntityElastic.class));
            assertEquals(2, list.size());

            accessor.clear(WeatherEntityElastic.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }

    @Test
    public void testPerformance() {
        GeneralAccessor accessor = context.getBean("generalAccessorElastic", GeneralAccessor.class);
        assertNotNull(accessor);

        try {
            long t0 = System.currentTimeMillis();
            for (int index = 1000; index < 2000; index++) {
                UserEntityElastic user = EntityFactory.createEntity(UserEntityElastic.class);
                user.setCode("john " + index);
                user.setName("John Peng");
                user.setDesc("description");
                accessor.save(user);
            }
            long t1 = System.currentTimeMillis() - t0;
            Thread.sleep(3000);
            assertEquals(1000, accessor.count(UserEntityElastic.class));

            t0 = System.currentTimeMillis();
            List<UserEntityElastic> users = new ArrayList<>(1000);
            for (int index = 2000; index < 3000; index++) {
                UserEntityElastic user = EntityFactory.createEntity(UserEntityElastic.class);
                user.setCode("john " + index);
                user.setName("John Peng");
                user.setDesc("description");
                users.add(user);
            }
            accessor.save(users);
            long t2 = System.currentTimeMillis() - t0;
            Thread.sleep(3000);
            assertEquals(2000, accessor.count(UserEntityElastic.class));
            assertTrue(t2 <= t1);

            accessor.clear(UserEntityElastic.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }

    @Test
    public void testAnyItemsQuery() {
        GeneralAccessor accessor = context.getBean("generalAccessorElastic", GeneralAccessor.class);
        assertNotNull(accessor);

        try {
            long delay = 1000;
            assertEquals(0, accessor.count(UserEntityElastic.class));

            List<UserEntityElastic> users = new ArrayList<>(50);
            for (int i = 0; i < 100; i++) {
                UserEntityElastic user1 = EntityFactory.createEntity(UserEntityElastic.class);
                user1.setAge(i);
                user1.setCode("john " + i);
                user1.setName("John Peng " + i);
                user1.setDesc("我是一个正高级工程师。");
                assertNull(user1.getId());
                users.add(user1);
            }
            accessor.save(users);
            Thread.sleep(3000);
            List<UserEntityElastic> list = accessor.find(GeneralAccessor.ConditionGroup.and(
                    GeneralAccessor.ConditionTuple.contain("desc", "我")), UserEntityElastic.class);
            assertEquals(100, list.size());
            list = accessor.find(GeneralAccessor.ConditionGroup.and(
                    GeneralAccessor.ConditionTuple.contain("desc", "高级工程师")),
                    UserEntityElastic.class);
            assertEquals(100, list.size());
            list = accessor.find(GeneralAccessor.ConditionGroup.and(
                    GeneralAccessor.ConditionTuple.contain("desc", "高级工程师"),
                    GeneralAccessor.ConditionTuple.eq("code", "john 19"),
                    GeneralAccessor.ConditionTuple.eq("age", "19")),
                    UserEntityElastic.class);
            assertEquals(1, list.size());
            list = accessor.find(GeneralAccessor.ConditionGroup.and(
                    GeneralAccessor.ConditionTuple.contain("desc", "高级工程师"),
                    GeneralAccessor.ConditionTuple.contain("code", "john 1")),
                    UserEntityElastic.class);
            // 1, 10 - 19
            assertEquals(11, list.size());
            list = accessor.find(GeneralAccessor.ConditionGroup.and(
                    GeneralAccessor.ConditionTuple.contain("desc", "高级工程师"),
                    GeneralAccessor.ConditionTuple.prefix("code", "john 1")),
                    UserEntityElastic.class);
            // 1, 10 - 19
            assertEquals(11, list.size());
            assertEquals(11, list.size());
            list = accessor.find(GeneralAccessor.ConditionGroup.and(
                    GeneralAccessor.ConditionTuple.contain("desc", "高级工程师"),
                    GeneralAccessor.ConditionTuple.contain("code", "hn 1")),
                    UserEntityElastic.class);
            // 1, 10 - 19
            assertEquals(11, list.size());
            list = accessor.find(GeneralAccessor.ConditionGroup.and(
                    GeneralAccessor.ConditionTuple.contain("desc", "高级工程师"),
                    GeneralAccessor.ConditionTuple.prefix("code", "hn 1")),
                    UserEntityElastic.class);
            assertEquals(0, list.size());

            list = accessor.find(GeneralAccessor.ConditionGroup.and(
                    GeneralAccessor.ConditionTuple.contain("desc", "高级工程师"),
                    GeneralAccessor.ConditionTuple.lte("age", 50),
                    GeneralAccessor.ConditionTuple.gt("age", 10)),
                    UserEntityElastic.class);
            assertEquals(40, list.size());

            accessor.clear(UserEntityElastic.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("fail");
        }
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
            user1.setYt(HanlpUtils.yinTou(user1.getDesc()));
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
            user2.setYt(HanlpUtils.yinTou(user2.getDesc()));
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
            u1.setYt(HanlpUtils.yinTou(u1.getDesc()));
            accessor.save(u1);
            Thread.sleep(delay);
            assertEquals(2, accessor.count(UserEntityElastic.class));
            assertEquals(2, accessor.count(UserEntityElastic.class, true));
            u1 = accessor.getById(user2.getId(), UserEntityElastic.class);
            assertNotNull(u1);
            assertEquals(user2.getCode(), u1.getCode());

            List<UserEntityElastic> list = accessor.find(GeneralAccessor.ConditionGroup.and(
                    GeneralAccessor.ConditionTuple.eq("code", "john")), UserEntityElastic.class);
            assertNotNull(list);
            assertEquals(1, list.size());
            list = accessor.find(GeneralAccessor.ConditionGroup.and(
                    GeneralAccessor.ConditionTuple.eq("code", "彭明")), UserEntityElastic.class);
            assertNotNull(list);
            assertEquals(0, list.size());
            list = accessor.find(GeneralAccessor.ConditionGroup.and(
                    GeneralAccessor.ConditionTuple.eq("code", "彭明喜")), UserEntityElastic.class);
            assertNotNull(list);
            assertEquals(1, list.size());
            list = accessor.find(GeneralAccessor.ConditionGroup.and(
                    GeneralAccessor.ConditionTuple.contain("name", "Peng")), UserEntityElastic.class);
            assertNotNull(list);
            assertEquals(2, list.size());
            list = accessor.find(GeneralAccessor.ConditionGroup.and(
                    GeneralAccessor.ConditionTuple.contain("name", "Joy")), UserEntityElastic.class);
            assertNotNull(list);
            assertEquals(1, list.size());
            list = accessor.find(GeneralAccessor.ConditionGroup.and(
                    GeneralAccessor.ConditionTuple.contain("desc", "高级工程师")), UserEntityElastic.class);
            assertNotNull(list);
            assertEquals(1, list.size());
            list = accessor.find(GeneralAccessor.ConditionGroup.and(
                    GeneralAccessor.ConditionTuple.contain("desc", "student")), UserEntityElastic.class);
            assertNotNull(list);
            assertEquals(1, list.size());
            assertEquals(1, list.size());
            list = accessor.find(GeneralAccessor.ConditionGroup.and(
                    GeneralAccessor.ConditionTuple.contain("desc", "学生")), UserEntityElastic.class);
            assertNotNull(list);
            assertEquals(1, list.size());
            // 增加了对z和zh的识别
            list = accessor.find(GeneralAccessor.ConditionGroup.or(
                    GeneralAccessor.ConditionTuple.contain("yt", "zgj"),
                    GeneralAccessor.ConditionTuple.contain("yt", "zhgj")
            ), UserEntityElastic.class);
            assertNotNull(list);
            assertEquals(1, list.size());

            // 测试条件组
            list = accessor.find(GeneralAccessor.ConditionGroup.and(
                    GeneralAccessor.ConditionTuple.lt("age", 100),
                    GeneralAccessor.ConditionTuple.gt("age", 0)),
                    UserEntityElastic.class);
            assertEquals(2, list.size());
            list = accessor.find(GeneralAccessor.ConditionGroup.and(
                    GeneralAccessor.ConditionTuple.lt("age", 45),
                    GeneralAccessor.ConditionTuple.gt("age", 0)),
                    UserEntityElastic.class);
            assertEquals(1, list.size());
            list = accessor.find(GeneralAccessor.ConditionGroup.and(
                    GeneralAccessor.ConditionTuple.lte("age", 45),
                    GeneralAccessor.ConditionTuple.gte("age", 17)),
                    UserEntityElastic.class);
            assertEquals(2, list.size());
            list = accessor.find(GeneralAccessor.ConditionGroup.and(
                    GeneralAccessor.ConditionTuple.lt("age", 45),
                    GeneralAccessor.ConditionTuple.gt("age", 17)),
                    UserEntityElastic.class);
            assertEquals(0, list.size());
            list = accessor.find(GeneralAccessor.ConditionGroup.and(
                    GeneralAccessor.ConditionTuple.eq("code", "彭明喜"),
                    GeneralAccessor.ConditionTuple.lt("age", 100),
                    GeneralAccessor.ConditionTuple.gt("age", 0),
                    GeneralAccessor.ConditionTuple.contain("name", "Joy")),
                    UserEntityElastic.class);
            assertEquals(1, list.size());
            list = accessor.find(GeneralAccessor.ConditionGroup.and(
                    GeneralAccessor.ConditionTuple.eq("code", "joy"),
                    GeneralAccessor.ConditionTuple.lt("age", 100),
                    GeneralAccessor.ConditionTuple.gt("age", 0),
                    GeneralAccessor.ConditionTuple.contain("name", "Joy")),
                    UserEntityElastic.class);
            assertEquals(0, list.size());

            list = accessor.find(GeneralAccessor.ConditionGroup.and(
                    GeneralAccessor.ConditionTuple.lte("age", 45),
                    GeneralAccessor.ConditionTuple.gte("age", 17),
                    GeneralAccessor.ConditionGroup.or(
                            GeneralAccessor.ConditionTuple.contain("desc", "学生"),
                            GeneralAccessor.ConditionTuple.contain("name", "John")
                    )),
                    UserEntityElastic.class);
            assertEquals(2, list.size());

            list = accessor.find(GeneralAccessor.ConditionGroup.and(
                    GeneralAccessor.ConditionTuple.lte("age", 45),
                    GeneralAccessor.ConditionTuple.gte("age", 17),
                    GeneralAccessor.ConditionGroup.or(
                            GeneralAccessor.ConditionTuple.contain("desc", "学生"),
                            GeneralAccessor.ConditionTuple.contain("name", "Joy")
                    )),
                    UserEntityElastic.class);
            assertEquals(1, list.size());

            list = accessor.find(GeneralAccessor.ConditionTuple.isNotNull("age"), UserEntityElastic.class);
            assertEquals(2, list.size());
            list = accessor.find(GeneralAccessor.ConditionTuple.isNull("age"), UserEntityElastic.class);
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

            accessor.clear(UserEntityElastic.class);
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

            List<UserEntityElastic> list = accessor.find(GeneralAccessor.ConditionGroup.and(
                    new GeneralAccessor.ConditionTuple("code", "john")), UserEntityElastic.class);
            assertNotNull(list);
            assertEquals(1, list.size());
            list = accessor.find(GeneralAccessor.ConditionGroup.and(
                    new GeneralAccessor.ConditionTuple("name", "John")), UserEntityElastic.class);
            assertNotNull(list);
            assertEquals(1, list.size());
            list = accessor.find(GeneralAccessor.ConditionGroup.and(
                    new GeneralAccessor.ConditionTuple("name", "Peng")), UserEntityElastic.class);
            assertNotNull(list);
            assertEquals(1, list.size());
            list = accessor.find(GeneralAccessor.ConditionGroup.and(
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
            List<AnimalEntityElastic> list1 = accessor.find(GeneralAccessor.ConditionGroup.and(
                    new GeneralAccessor.ConditionTuple("name", "John")), AnimalEntityElastic.class);
            assertNotNull(list1);
            assertEquals(1, list1.size());
            list1 = accessor.find(GeneralAccessor.ConditionGroup.and(
                    new GeneralAccessor.ConditionTuple("name", "cat")), AnimalEntityElastic.class);
            assertNotNull(list1);
            assertEquals(1, list1.size());

            list1 = ((ElasticAccessor) accessor).find(GeneralAccessor.ConditionGroup.and(
                    GeneralAccessor.ConditionTuple.contain("name", "cat")
            ), Arrays.asList(AnimalEntityElastic.class, UserEntityElastic.class));
            assertNotNull(list1);
            assertEquals(1, list1.size());

            accessor.remove(user1, false);
            accessor.remove(animal1, false);

            accessor.clear(UserEntityElastic.class);
            accessor.clear(AnimalEntityElastic.class);
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }
}
