package org.mx.test.pgentity;

import org.mx.dal.entity.BaseEntity;
import org.postgresql.geometric.PGbox;
import org.postgresql.geometric.PGpath;
import org.postgresql.geometric.PGpoint;
import org.postgresql.geometric.PGpolygon;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "pg_geometry")
public class GeometryEntity extends BaseEntity {
    private PGpoint point;
    private PGbox box;
    private PGpolygon polygon;
    private PGpath path;

    public PGpoint getPoint() {
        return point;
    }

    public void setPoint(PGpoint point) {
        this.point = point;
    }

    public PGbox getBox() {
        return box;
    }

    public void setBox(PGbox box) {
        this.box = box;
    }

    public PGpolygon getPolygon() {
        return polygon;
    }

    public void setPolygon(PGpolygon polygon) {
        this.polygon = polygon;
    }

    public PGpath getPath() {
        return path;
    }

    public void setPath(PGpath path) {
        this.path = path;
    }
}
