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
    private GeoPointLocation location; // 标定的GeoPoint

    private double distance; // 距离

    /**
     * 获取位置点（经度和纬度）
     *
     * @return 位置点坐标
     */
    public GeoPointLocation getLocation() {
        return location;
    }

    /**
     * 设置位置点（经度和维度）
     *
     * @param location 位置点坐标
     */
    public void setLocation(GeoPointLocation location) {
        this.location = location;
    }

    /**
     * 获取距离（单位为米）
     *
     * @return 距离
     */
    public double getDistance() {
        return distance;
    }

    /**
     * 设置距离（单位为米），空间搜索方法内部设置
     *
     * @param distance 距离
     */
    public void setDistance(double distance) {
        this.distance = distance;
    }
}
