package org.mx.comps.rbac;

import org.junit.Test;
import org.mx.comps.rbac.dal.entity.Privilege;
import org.mx.comps.rbac.dal.entity.Role;
import org.mx.comps.rbac.service.RoleManageService;
import org.mx.dal.EntityFactory;
import org.mx.dal.service.GeneralDictAccessor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;

/**
 * 特权实体单元测试类
 *
 * @author : john.peng created on date : 2017/11/28
 */
public class TestPrivilege extends BaseTest {
    public static String p1Id, p2Id, p3Id;

    public static void testInsertPrivilege(GeneralDictAccessor service) {
        Privilege p1 = EntityFactory.createEntity(Privilege.class);
        p1.setCode("privilege1");
        p1.setName("privilege1");
        p1.setDesc("desc");
        p1 = service.save(p1);
        assertEquals(1, service.count(Privilege.class));
        assertNotNull(p1);
        assertNotNull(p1.getId());
        p1Id = p1.getId();
        p1 = service.getById(p1Id, Privilege.class);
        assertNotNull(p1);
        assertEquals("privilege1", p1.getCode());
        assertEquals("privilege1", p1.getName());
        assertEquals("desc", p1.getDesc());
        assertTrue(p1.isValid());

        Privilege p2 = EntityFactory.createEntity(Privilege.class);
        p2.setCode("privilege2");
        p2.setName("privilege2");
        p2.setDesc("desc");
        service.save(p2);
        assertEquals(2, service.count(Privilege.class));
        p2 = service.getById(p2.getId(), Privilege.class);
        assertNotNull(p2);
        p2Id = p2.getId();
        assertEquals("privilege2", p2.getCode());
        assertEquals("privilege2", p2.getName());
        assertEquals("desc", p2.getDesc());
        assertTrue(p2.isValid());
    }

    public static void testEditPrivilege(GeneralDictAccessor service) {
        Privilege p3 = EntityFactory.createEntity(Privilege.class);
        p3.setCode("privilege3");
        p3.setName("privilege3");
        service.save(p3);
        assertEquals(3, service.count(Privilege.class));
        p3 = service.getById(p3.getId(), Privilege.class);
        assertNotNull(p3);
        p3Id = p3.getId();
        assertEquals("privilege3", p3.getCode());
        assertEquals("privilege3", p3.getName());
        p3.setName("new name");
        p3.setValid(false);
        service.save(p3);
        assertEquals(2, service.count(Privilege.class));
        assertEquals(3, service.count(Privilege.class, false));

        p3.setName("privilege3");
        p3.setValid(true);
        service.save(p3);
        assertEquals(3, service.count(Privilege.class));
    }

    @Test
    public void testPrivilegeCrud() {
        GeneralDictAccessor service = context.getBean("generalDictAccessor", GeneralDictAccessor.class);
        assertNotNull(service);

        try {
            assertEquals(0, service.count(Privilege.class));
            // insert
            testInsertPrivilege(service);
            // edit
            testEditPrivilege(service);
            // delete
            testDeletePrivilege(service);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }

    private void testDeletePrivilege(GeneralDictAccessor service) {
        List<Privilege> ps = service.list(Privilege.class);
        int pnum = ps.size();
        for (Privilege p : ps) {
            service.remove(p);
            assertEquals(--pnum, service.count(Privilege.class));
            assertEquals(ps.size(), service.count(Privilege.class, false));
        }
        pnum = ps.size();
        for (Privilege p : ps) {
            service.remove(p, false);
            assertEquals(--pnum, service.count(Privilege.class, false));
        }
    }
}
