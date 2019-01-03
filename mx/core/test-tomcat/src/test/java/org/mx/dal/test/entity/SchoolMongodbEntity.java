package org.mx.dal.test.entity;

import org.mx.dal.entity.MongoBaseEntity;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "school")
public class SchoolMongodbEntity extends MongoBaseEntity implements School {
    private String name;
    @GeoSpatialIndexed(name = "location", type = GeoSpatialIndexType.GEO_2DSPHERE)
    private double[] location;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double[] getLocation() {
        return location;
    }

    public void setLocation(double[] location) {
        this.location = location;
    }
}
