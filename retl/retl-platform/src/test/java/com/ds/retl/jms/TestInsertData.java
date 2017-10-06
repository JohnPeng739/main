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
                "nio://mqserver:61616?trace=true");
        Connection conn = connectionFactory.createConnection();
        // 这里统一使用CLIENT的确认方式。
        Session session = conn.createSession(false,
                Session.CLIENT_ACKNOWLEDGE);
        Destination destination = session.createQueue("src-jjd-queue");
        MessageProducer mp = session.createProducer(destination);
        Map<String, Object> map = new HashMap<String, Object>() {{
            put("id", UUID.randomUUID().toString());
            put("code", "joy");
            put("name", "Joy Peng");
            put("families", 1);
            put("age", 16);
            put("money", 38284723.12);
            put("birthday", new Date());
            put("createdtime", new Date());
            put("updatedtime", new Date());
            put("email", "joy.peng@163.com");
            put("maried", false);
            put("indexNo", 1);
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
        Destination destination = session.createQueue("src-cjd-queue");
        MessageProducer mp = session.createProducer(destination);
        int start = 120001;
        for (int index = start; index < 9990000; index++) {
            final int indexNo = index;
            Map<String, Object> map = new HashMap<String, Object>() {{
                put("id", UUID.randomUUID().toString());
                put("code", "cjd");
                put("name", "Cjd Sample");
                put("families", 3);
                put("age", 16);
                put("money", 38284723.12);
                put("birthday", new Date());
                put("createdtime", new Date());
                put("updatedtime", new Date());
                put("email", "cjd@163.com");
                put("married", false);
                put("indexNo", indexNo);
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
