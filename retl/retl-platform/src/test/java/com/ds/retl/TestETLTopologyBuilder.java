package com.ds.retl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import org.junit.Test;

import java.io.InputStreamReader;

import static org.junit.Assert.fail;

/**
 * Created by john on 2017/9/11.
 */
public class TestETLTopologyBuilder {

    @Test
    public void testJdbc2Jdbc() {
        test("/sample-jdbc-2-jdbc.json");
    }

    @Test
    public void testJms2Jdbc() {
        test("/sample-jms-2-jdbc.json");
    }

    @Test
    public void testJdbc2Jms() {
        test("/sample-jdbc-2-jms.json");
    }

    @Test
    public void testJms2Jms() {
        test("/sample-jms-2-jms.json");
    }

    @Test
    public void testPersist2Jdbc() {
        test("/sample-persist-2-jdbc.json");
    }

    private void test(String configFile) {
        try {
            ETLTopologyBuilder builder = new ETLTopologyBuilder();
            JSONReader reader = new JSONReader(
                    new InputStreamReader(this.getClass().getResourceAsStream(configFile)));
            JSONObject json = JSON.parseObject(reader.readString());
            reader.close();

            boolean isCluster = false;
            builder.buildTopology(isCluster,json);
        } catch (Exception ex) {
            fail();
        }
    }
}
