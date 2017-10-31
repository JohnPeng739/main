package com.ds.retl.jms;

import com.alibaba.fastjson.JSONObject;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Test;

import javax.jms.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by john on 2017/9/21.
 */
public class TestInsertData {
    @Test
    public void testSingle() throws Exception {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("", "",
                "tcp://mqserver:61616?trace=true");
        Connection conn = connectionFactory.createConnection();
        // 这里统一使用CLIENT的确认方式。
        Session session = conn.createSession(false,
                Session.CLIENT_ACKNOWLEDGE);
        Destination destination = session.createQueue("tar-jms-pull-src");
        MessageProducer mp = session.createProducer(destination);
        Map<String, Object> map = new HashMap<String, Object>() {{
            put("id", UUID.randomUUID().toString());
            put("code", "joy");
            put("name", "Joy Peng");
            put("families", 5);
            put("age", 16);
            put("money", 38284723.12);
            put("birthday", new Date());
            put("createdTime", new Date());
            put("updatedTime", new Date());
            put("email", "joy.peng@163.com");
            put("married", false);
            put("indexNo", 1);
            put("sex", "FEMALE");
            put("departmentCode", "kfsb1");
            put("lastOrderId", "47852787-90ea-49a2-9eee-0fb0df479612");
        }};
        JSONObject json = new JSONObject(map);
        TextMessage msg = session.createTextMessage(json.toJSONString());
        mp.send(msg);
        Thread.sleep(5);
        mp.close();
        session.close();
        conn.close();
    }

    @Test
    public void testSingleNew() throws Exception {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("", "",
                "tcp://mqserver:61616?trace=true");
        Connection conn = connectionFactory.createConnection();
        // 这里统一使用CLIENT的确认方式。
        Session session = conn.createSession(false,
                Session.CLIENT_ACKNOWLEDGE);
        Destination destination = session.createQueue("src-jms-new");
        MessageProducer mp = session.createProducer(destination);
        Map<String, Object> map = new HashMap<String, Object>() {{
            put("data.id", UUID.randomUUID().toString());
            put("data.code", "joy");
            put("data.name", "Joy Peng");
            put("data.families", 5);
            put("data.age", 16);
            put("data.money", 38284723.12);
            put("data.birthday", new Date());
            put("data.createdTime", new Date());
            put("data.updatedTime", new Date());
            put("data.email", "joy.peng@163.com");
            put("data.married", false);
            put("data.indexNo", 1);
            put("data.sex", "FEMALE");
            put("data.departmentCode", "kfsb");
            put("data.lastOrderId", "47852787-90ea-49a2-9eee-0fb0df479612");
        }};
        JSONObject json = new JSONObject(map);
        TextMessage msg = session.createTextMessage(json.toJSONString());
        mp.send(msg);
        Thread.sleep(5);
        mp.close();
        session.close();
        conn.close();
    }

    @Test
    public void testBatch() throws Exception {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("", "",
                "nio://mqserver:61616?trace=true");
        Connection conn = connectionFactory.createConnection();
        // 这里统一使用CLIENT的确认方式。
        Session session = conn.createSession(false,
                Session.CLIENT_ACKNOWLEDGE);
        Destination destination = session.createQueue("tar-jms-pull-src");
        MessageProducer mp = session.createProducer(destination);
        int start = 1;
        for (int index = start; index < 9990000; index++) {
            final int indexNo = index;
            Map<String, Object> map = new HashMap<String, Object>() {{
                put("id", UUID.randomUUID().toString());
                put("code", "joy");
                put("name", "Joy Peng");
                put("families", 5);
                put("age", 16);
                put("money", 38284723.12);
                put("birthday", new Date());
                put("createdTime", new Date());
                put("updatedTime", new Date());
                put("email", "joy.peng@163.com");
                put("married", false);
                put("indexNo", indexNo);
                put("sex", "FEMALE");
                put("departmentCode", "kfsb");
                put("lastOrderId", "47852787-90ea-49a2-9eee-0fb0df479612");
            }};
            JSONObject json = new JSONObject(map);
            TextMessage msg = session.createTextMessage(json.toJSONString());
            mp.send(msg);
            if (index % 30000 == 0) {
                System.out.println(index);
                Thread.sleep(50);
            }
        }
        mp.close();
        session.close();
        conn.close();
    }
}
