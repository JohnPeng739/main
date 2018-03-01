package org.mx.dal;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class TestEntityFactory {
    @Test
    public void testInterface () {
        User user = EntityFactory.createEntity(User.class);
        assertNotNull(user);
        assertNull(user.getCode());
        user.setCode("john");
        assertNotNull(user.getCode());
        assertEquals("john", user.getCode());
    }

    @Test
    public void testEntity() {
        User user = EntityFactory.createEntity(UserEntity.class);
        assertNotNull(user);
        assertNull(user.getCode());
        user.setCode("john");
        assertNotNull(user.getCode());
        assertEquals("john", user.getCode());
    }
}
