package org.mx.dal.entity;

import org.elasticsearch.common.geo.GeoPoint;

/**
 * 描述： ES平台中GIS点位数据对象
 *
 * @author john peng
 * Date time 2018/8/20 上午10:35
 */
public class GeoPointLocation {
    private double lat, lon;

    public GeoPointLocation() {
        super();
    }

    public GeoPointLocation(double lon, double lat) {
        this();
        this.lon = lon;
        this.lat = lat;
    }

    public GeoPoint get() {
        return new GeoPoint(lat, lon);
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
