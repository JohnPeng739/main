package org.mx.dal;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mx.DigestUtils;
import org.mx.dal.config.TestDalMongodbConfig;
import org.mx.dal.entity.SchoolEntity;
import org.mx.dal.entity.User;
import org.mx.dal.service.GeneralAccessor;
import org.mx.dal.service.GeneralDictAccessor;
import org.mx.dal.service.GeneralTextSearchAccessor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.geo.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.springframework.data.geo.Metrics.KILOMETERS;

public class TestDatabase {
    protected AnnotationConfigApplicationContext context;
    private MongodExecutable mongodExecutable;
    private MongodProcess mongod;

    @Before
    public void before() {
        try {
            IMongodConfig config = new MongodConfigBuilder().version(Version.Main.PRODUCTION)
                    .net(new Net("localhost", 27017, Network.localhostIsIPv6())).build();
            mongodExecutable = MongodStarter.getDefaultInstance().prepare(config);
            mongod = mongodExecutable.start();
            context = new AnnotationConfigApplicationContext(TestDalMongodbConfig.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }

    @After
    public void after() {
        if (context != null) {
            context.close();
        }
        if (mongod != null) {
            mongod.stop();
        }
        if (mongodExecutable != null) {
            mongodExecutable.stop();
        }
    }

    @Test
    public void testPerformance() {
        GeneralDictAccessor accessor = context.getBean("generalDictAccessorMongodb",
                GeneralDictAccessor.class);
        assertNotNull(accessor);

        accessor.clear(User.class);

        long t0 = System.currentTimeMillis();
        for (int index = 1000; index < 2000; index++) {
            User user = EntityFactory.createEntity(User.class);
            user.setCode("john " + index);
            user.setName("John Peng");
            user.setAddress("some address is here, 中华人民共和国，美利坚合众国。");
            user.setEmail("email");
            user.setPostCode("zip");
            user.setDesc("description");
            accessor.save(user);
        }
        long t1 = System.currentTimeMillis() - t0;
        assertEquals(1000, accessor.count(User.class));

        t0 = System.currentTimeMillis();
        List<User> users = new ArrayList<>(1000);
        for (int index = 2000; index < 3000; index++) {
            User user = EntityFactory.createEntity(User.class);
            user.setCode("john " + index);
            user.setName("John Peng");
            user.setAddress("some address is here, 中华人民共和国，美利坚合众国。");
            user.setEmail("email");
            user.setPostCode("zip");
            user.setDesc("description");
            users.add(user);
        }
        accessor.save(users);
        long t2 = System.currentTimeMillis() - t0;
        assertEquals(2000, accessor.count(User.class));
        assertTrue(t2 <= t1);
        System.out.println(String.format("t1: %dms, t2: %sms.", t1, t2));

        accessor.clear(User.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUserInterface() {
        GeneralDictAccessor accessor = context.getBean("generalDictAccessorMongodb",
                GeneralDictAccessor.class);
        assertNotNull(accessor);
        GeneralTextSearchAccessor searchAccessor = context.getBean("generalDictAccessorMongodb",
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

            List<User> list = accessor.find(GeneralAccessor.ConditionGroup.and(
                    GeneralAccessor.ConditionGroup.or(
                            GeneralAccessor.ConditionTuple.eq("code", "john"),
                            GeneralAccessor.ConditionTuple.eq("code", "josh")
                    ),
                    GeneralAccessor.ConditionTuple.eq("valid", true)
            ), User.class);
            assertEquals(2, list.size());

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

            list = accessor.find(GeneralAccessor.ConditionTuple.isNull("code"), User.class);
            assertEquals(0, list.size());
            list = accessor.find(GeneralAccessor.ConditionTuple.isNotNull("code"), User.class);
            assertEquals(2, list.size());

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
        GeneralDictAccessor accessor = context.getBean("generalDictAccessorMongodb",
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

    @Test
    public void testGeoFunc() {
        MongoTemplate template = context.getBean(MongoTemplate.class);
        assertNotNull(template);
        GeneralDictAccessor accessor = context.getBean("generalDictAccessorMongodb",
                GeneralDictAccessor.class);
        assertNotNull(accessor);
        accessor.clear(SchoolEntity.class);
        assertEquals(0, accessor.count(SchoolEntity.class));

        try {
            // 1.5420KM
            SchoolEntity s1 = EntityFactory.createEntity(SchoolEntity.class);
            s1.setName("SJTU");
            s1.setLocation(new double[] {121.4038, 29.1234});
            accessor.save(s1);
            SchoolEntity s2 = EntityFactory.createEntity(SchoolEntity.class);
            s2.setName("NO1");
            s2.setLocation(new double[]{121.4148, 29.1334});
            accessor.save(s2);
            SchoolEntity check = accessor.getById(s1.getId(), SchoolEntity.class);
            assertNotNull(check);
            assertEquals("SJTU", check.getName());
            check = accessor.getById(s2.getId(), SchoolEntity.class);
            assertNotNull(check);
            assertEquals("NO1", check.getName());
            assertEquals(2, accessor.count(SchoolEntity.class));

            Circle circle = new Circle(new Point(121.4038, 29.1234), new Distance(1.5, KILOMETERS));
            List<SchoolEntity> list = template.find(new Query(Criteria.where("location").withinSphere(circle)), SchoolEntity.class);
            assertEquals(1, list.size());
            assertEquals("SJTU", list.get(0).getName());

            circle = new Circle(new Point(121.4038, 29.1234), new Distance(1.6, KILOMETERS));
            list = template.find(new Query(Criteria.where("location").withinSphere(circle)), SchoolEntity.class);
            assertEquals(2, list.size());
            assertEquals("SJTU", list.get(0).getName());
            assertEquals("NO1", list.get(1).getName());

            Box box = new Box(new Point(121.4030, 29.1230), new Point(121.4147, 29.1333));
            list = template.find(new Query(Criteria.where("location").within(box)), SchoolEntity.class);
            assertEquals(1, list.size());
            assertEquals("SJTU", list.get(0).getName());

            box = new Box(new Point(121.4030, 29.1230), new Point(121.4149, 29.1335));
            list = template.find(new Query(Criteria.where("location").within(box)), SchoolEntity.class);
            assertEquals(2, list.size());
            assertEquals("SJTU", list.get(0).getName());
            assertEquals("NO1", list.get(1).getName());

            NearQuery query = NearQuery.near(new Point(121.4038, 29.1234)).maxDistance(new Distance(1.5, Metrics.KILOMETERS));
            GeoResults<SchoolEntity> results = template.geoNear(query, SchoolEntity.class);
            assertEquals(1, results.getContent().size());
            assertEquals("SJTU", results.getContent().get(0).getContent().getName());

            query = NearQuery.near(new Point(121.4038, 29.1234)).maxDistance(new Distance(1.6, Metrics.KILOMETERS));
            results = template.geoNear(query, SchoolEntity.class);
            assertEquals(2, results.getContent().size());
            assertEquals("SJTU", results.getContent().get(0).getContent().getName());
            assertEquals("NO1", results.getContent().get(1).getContent().getName());

            accessor.clear(SchoolEntity.class);
            assertEquals(0, accessor.count(SchoolEntity.class));
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }
}
