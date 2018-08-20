package org.mx.dal.entity;

import org.mx.dal.annotation.ElasticField;

/**
 * 描述： 带有地理定位点的实体定义
 *
 * @author john peng
 * Date time 2018/8/20 下午2:25
 */
public class ElasticGeoPointBaseEntity extends ElasticBaseEntity {
    @ElasticField(type = "geo_point")
    private GeoPointLocation location;

    @ElasticField(type = "double", store = false)
    private double distance;

    public GeoPointLocation getLocation() {
        return location;
    }

    public void setLocation(GeoPointLocation location) {
        this.location = location;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
