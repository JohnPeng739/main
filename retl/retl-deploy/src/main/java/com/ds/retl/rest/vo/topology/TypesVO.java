package com.ds.retl.rest.vo.topology;

/**
 * Created by john on 2017/10/10.
 */
public class TypesVO {
    public class TopologyTypes {
        private String retl = "数据抽取、校验、转换";
        private String persist = "数据存储";

        public void setRetl(String retl) {
            this.retl = retl;
        }

        public void setPersist(String persist) {
            this.persist = persist;
        }

        public String getRetl() {
            return retl;
        }

        public String getPersist() {
            return persist;
        }
    }

    public class  SpoutTypes {
        private String jms = "推送型JMS", jmsPull = "拉取型JMS（推荐使用）", jdbc = "关系型数据库";

        public void setJms(String jms) {
            this.jms = jms;
        }

        public void setJmsPull(String jmsPull) {
            this.jmsPull = jmsPull;
        }

        public void setJdbc(String jdbc) {
            this.jdbc = jdbc;
        }

        public String getJms() {
            return jms;
        }

        public String getJmsPull() {
            return jmsPull;
        }

        public String getJdbc() {
            return jdbc;
        }
    }

    public class BoltTypes {
        private String structure = "归一化处理", validate = "数据校验", transform = "数据转换", error = "错误处理",
                jms = "JMS存储", jdbc = "关系型数据库存储";

        public void setStructure(String structure) {
            this.structure = structure;
        }

        public void setValidate(String validate) {
            this.validate = validate;
        }

        public void setTransform(String transform) {
            this.transform = transform;
        }

        public void setError(String error) {
            this.error = error;
        }

        public void setJms(String jms) {
            this.jms = jms;
        }

        public void setJdbc(String jdbc) {
            this.jdbc = jdbc;
        }

        public String getStructure() {
            return structure;
        }

        public String getValidate() {
            return validate;
        }

        public String getTransform() {
            return transform;
        }

        public String getError() {
            return error;
        }

        public String getJms() {
            return jms;
        }

        public String getJdbc() {
            return jdbc;
        }
    }

    public class JdbcDriverTypes {
        private String mysql = "com.mysql.jdbc.Driver", h2 = "org.h2.Driver";

        public void setMysql(String mysql) {
            this.mysql = mysql;
        }

        public void setH2(String h2) {
            this.h2 = h2;
        }

        public String getMysql() {
            return mysql;
        }

        public String getH2() {
            return h2;
        }
    }

    private TopologyTypes topologyTypes = new TopologyTypes();
    private SpoutTypes spoutTypes = new SpoutTypes();
    private BoltTypes boltTypes = new BoltTypes();
    private JdbcDriverTypes jdbcDriverTypes = new JdbcDriverTypes();

    public void setTopologyTypes(TopologyTypes topologyTypes) {
        this.topologyTypes = topologyTypes;
    }

    public void setSpoutTypes(SpoutTypes spoutTypes) {
        this.spoutTypes = spoutTypes;
    }

    public void setBoltTypes(BoltTypes boltTypes) {
        this.boltTypes = boltTypes;
    }

    public void setJdbcDriverTypes(JdbcDriverTypes jdbcDriverTypes) {
        this.jdbcDriverTypes = jdbcDriverTypes;
    }

    public TopologyTypes getTopologyTypes() {
        return topologyTypes;
    }

    public SpoutTypes getSpoutTypes() {
        return spoutTypes;
    }

    public BoltTypes getBoltTypes() {
        return boltTypes;
    }

    public JdbcDriverTypes getJdbcDriverTypes() {
        return jdbcDriverTypes;
    }
}
