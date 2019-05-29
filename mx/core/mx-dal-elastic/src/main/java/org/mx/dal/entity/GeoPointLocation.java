package org.mx.dal.entity;

import org.elasticsearch.common.geo.GeoPoint;

/**
 * 描述： ES平台中GIS点位数据对象
 *
 * @author john peng
 * Date time 2018/8/20 上午10:35
 */
public class GeoPointLocation {
    private double lat, lon; // 纬度、经度

    /**
     * 默认的构造函数
     */
    public GeoPointLocation() {
        super();
    }

    /**
     * 默认的构造函数
     *
     * @param lon 经度
     * @param lat 纬度
     */
    public GeoPointLocation(double lon, double lat) {
        this();
        this.lon = lon;
        this.lat = lat;
    }

    /**
     * 获取标定的GeoPoint对象
     *
     * @return GeoPoint
     */
    public GeoPoint get() {
        return new GeoPoint(lat, lon);
    }

    /**
     * 获取纬度
     *
     * @return 纬度
     */
    public double getLat() {
        return lat;
    }

    /**
     * 设置纬度
     *
     * @param lat 纬度
     */
    public void setLat(double lat) {
        this.lat = lat;
    }

    /**
     * 获取经度
     *
     * @return 纬度
     */
    public double getLon() {
        return lon;
    }

    /**
     * 设置经度
     *
     * @param lon 经度
     */
    public void setLon(double lon) {
        this.lon = lon;
    }
}
