package com.ds.retl.jdbc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import com.ds.retl.ETLTopologyBuilder;
import org.junit.Test;

import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.UUID;

/**
 * Created by john on 2017/9/20.
 */
public class TestInsertData {
    @Test
    public void testSingle() throws Exception {
        ETLTopologyBuilder builder = new ETLTopologyBuilder();
        JSONReader reader = new JSONReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/etl-topology-config-sample2.json")));
        JSONObject json = JSON.parseObject(reader.readString());
        reader.close();

        JSONArray config = json.getJSONArray("dataSources");
        JSONObject caches = json.getJSONObject("caches");
        JdbcManager.getManager().initManager(config, caches);
        Connection conn = JdbcManager.getManager().getConnection("dataSource1");
        PreparedStatement ps = conn.prepareStatement("INSERT INTO TB_SRC(ID, CODE, NAME, FAMILIES, AGE, MONEY, " +
                "BIRTHDAY, CREATED_TIME, UPDATED_TIME, EMAIL, MARRIED, INDEX_NO, SEX, DEPARTMENT_CODE, LAST_ORDER_ID) " +
                "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        ps.setString(1, UUID.randomUUID().toString());
        ps.setString(2, "jian");
        ps.setString(3, "Jian Peng");
        ps.setInt(4, 0);
        ps.setInt(5, 43);
        ps.setDouble(6, 4567344.33);
        ps.setDate(7, new java.sql.Date(new Date().getTime()));
        ps.setTimestamp(8, new java.sql.Timestamp(new Date().getTime()));
        ps.setTimestamp(9, new java.sql.Timestamp(new Date().getTime()));
        ps.setString(10, "pengmingxi@dscomm.com.cn");
        ps.setBoolean(11, true);
        ps.setLong(12, 1);
        ps.setString(13, "MALE");
        ps.setString(14, "kfsb");
        ps.setString(15, "ORDER_ID");
        ps.executeUpdate();
        Thread.sleep(10);
        ps.close();
    }

    @Test
    public  void testBatch() throws Exception {
        testBatch("/sample-jdbc-2-jms.json", "TB_SRC");
    }

    @Test
    public void testDataJJD1() throws Exception {
        ETLTopologyBuilder builder = new ETLTopologyBuilder();
        JSONReader reader = new JSONReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/sample-jdbc-2-jms.json")));
        JSONObject json = JSON.parseObject(reader.readString());
        reader.close();

        JSONArray config = json.getJSONArray("dataSources");
        JSONObject caches = json.getJSONObject("caches");
        JdbcManager.getManager().initManager(config, caches);
        Connection conn = JdbcManager.getManager().getConnection("dataSource1");
        PreparedStatement ps = conn.prepareStatement("select indexNo,id from cjd2 order by indexNo");
        ResultSet rs = ps.executeQuery();
        long index = 0, total = 0, errorNum = 0;
        while (rs.next()) {
            total ++;
            long indexNo = rs.getLong(1);
            if (indexNo == index + 1) {
                index = indexNo;
                continue;
            }
            index = indexNo;
            errorNum ++;
            System.out.println(String.format("indexNo: %d, id: %s.", indexNo, rs.getString(2)));
        }
        System.out.println(String.format("Total: %d, error: %d.", total, errorNum));
        rs.close();
        ps.close();
        conn.close();
    }

    private void testBatch(String configFile, String table) throws Exception {
        ETLTopologyBuilder builder = new ETLTopologyBuilder();
        JSONReader reader = new JSONReader(
                new InputStreamReader(this.getClass().getResourceAsStream(configFile)));
        JSONObject json = JSON.parseObject(reader.readString());
        reader.close();

        JSONArray config = json.getJSONArray("dataSources");
        JSONObject caches = json.getJSONObject("caches");
        JdbcManager.getManager().initManager(config, caches);
        Connection conn = JdbcManager.getManager().getConnection("dataSource1");
        PreparedStatement ps = conn.prepareStatement(String.format("INSERT INTO %s(ID,CODE,NAME,FAMILIES,AGE,MONEY," +
                "BIRTHDAY,CREATEDTIME,UPDATEDTIME,EMAIL,MARRIED,INDEXNO) " +
                "VALUES(?,?,?,?,?,?,?,?,?,?,?,?)", table));
        for (int index = 1; index <= 1500; index ++){
            ps.setString(1, UUID.randomUUID().toString());
            ps.setString(2, "jjd");
            ps.setString(3, "Jjd Sample");
            ps.setInt(4, 5);
            ps.setInt(5, 43);
            ps.setDouble(6, 4567344.33);
            ps.setDate(7, new java.sql.Date(new Date().getTime()));
            ps.setTimestamp(8, null);// new java.sql.Timestamp(new Date().getTime()));
            ps.setTimestamp(9, null); //new java.sql.Timestamp(new Date().getTime()));
            ps.setString(10, "jjd@dscomm.com.cn");
            ps.setBoolean(11, true);
            ps.setLong(12, index);
            ps.executeUpdate();
            System.out.println(index);
        }
        ps.close();
    }
}
