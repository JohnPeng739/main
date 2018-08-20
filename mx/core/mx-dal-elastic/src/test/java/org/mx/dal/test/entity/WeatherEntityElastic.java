package org.mx.dal.test.entity;

import org.mx.dal.annotation.ElasticField;
import org.mx.dal.annotation.ElasticIndex;
import org.mx.dal.entity.ElasticGeoPointBaseEntity;

/**
 * 描述：
 *
 * @author : john date : 2018/8/20 上午10:40
 */
@ElasticIndex("weather")
public class WeatherEntityElastic extends ElasticGeoPointBaseEntity {
    @ElasticField
    private String name, type;
    @ElasticField(type = "float")
    private float temperature;

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }
}
