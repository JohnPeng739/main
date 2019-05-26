package org.mx.test.entity;

import org.mx.dal.entity.BaseDictEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

@Entity
@Table(name = "JDBC_TEST")
public class JdbcTestEntity extends BaseDictEntity {
    private BigDecimal bigDecimalValue;
    @Column(name = "BYTE_V")
    private byte byteValue;
    private short shortValue;
    private int intValue;
    private long longValue;
    private float floatValue;
    private double doubleValue;
    private byte[] byteArrayValue;
    private Date sqlDateValue;
    private Time sqlTimeValue;
    private Timestamp sqlTimestampValue;

    public BigDecimal getBigDecimalValue() {
        return bigDecimalValue;
    }

    public void setBigDecimalValue(BigDecimal bigDecimalValue) {
        this.bigDecimalValue = bigDecimalValue;
    }

    public byte getByteValue() {
        return byteValue;
    }

    public void setByteValue(byte byteValue) {
        this.byteValue = byteValue;
    }

    public short getShortValue() {
        return shortValue;
    }

    public void setShortValue(short shortValue) {
        this.shortValue = shortValue;
    }

    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    public long getLongValue() {
        return longValue;
    }

    public void setLongValue(long longValue) {
        this.longValue = longValue;
    }

    public float getFloatValue() {
        return floatValue;
    }

    public void setFloatValue(float floatValue) {
        this.floatValue = floatValue;
    }

    public double getDoubleValue() {
        return doubleValue;
    }

    public void setDoubleValue(double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public byte[] getByteArrayValue() {
        return byteArrayValue;
    }

    public void setByteArrayValue(byte[] byteArrayValue) {
        this.byteArrayValue = byteArrayValue;
    }

    public Date getSqlDateValue() {
        return sqlDateValue;
    }

    public void setSqlDateValue(Date sqlDateValue) {
        this.sqlDateValue = sqlDateValue;
    }

    public Time getSqlTimeValue() {
        return sqlTimeValue;
    }

    public void setSqlTimeValue(Time sqlTimeValue) {
        this.sqlTimeValue = sqlTimeValue;
    }

    public Timestamp getSqlTimestampValue() {
        return sqlTimestampValue;
    }

    public void setSqlTimestampValue(Timestamp sqlTimestampValue) {
        this.sqlTimestampValue = sqlTimestampValue;
    }
}
