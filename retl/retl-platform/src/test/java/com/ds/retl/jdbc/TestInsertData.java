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
        JdbcManager.getManager().initManager(config);
        Connection conn = JdbcManager.getManager().getConnection("dataSource1");
        PreparedStatement ps = conn.prepareStatement("INSERT INTO sample1(id,code,name,families,age,money,birthday,createdtime,updatedtime,email,married,indexNo) " +
                "VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");
        ps.setString(1, UUID.randomUUID().toString());
        ps.setString(2, "john");
        ps.setString(3, "John Peng");
        ps.setInt(4, 1);
        ps.setInt(5, 43);
        ps.setDouble(6, 4567344.33);
        ps.setDate(7, new java.sql.Date(new Date().getTime()));
        ps.setTimestamp(8, new java.sql.Timestamp(new Date().getTime()));
        ps.setTimestamp(9, new java.sql.Timestamp(new Date().getTime()));
        ps.setString(10, "pengmingxi@dscomm.com.cn");
        ps.setBoolean(11, true);
        ps.setLong(12, 1);
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
        JdbcManager.getManager().initManager(config);
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
        JdbcManager.getManager().initManager(config);
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
