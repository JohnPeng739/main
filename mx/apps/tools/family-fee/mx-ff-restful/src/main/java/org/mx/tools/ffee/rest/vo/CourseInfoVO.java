package org.mx.tools.ffee.rest.vo;

import org.mx.StringUtils;
import org.mx.dal.EntityFactory;
import org.mx.tools.ffee.dal.entity.Course;
import org.mx.tools.ffee.dal.entity.FfeeAccount;

public class CourseInfoVO extends BaseParamsVO {
    private String id, code, name, desc;
    private Course.CourseType type = Course.CourseType.INCOME;
    private String ownerId;

    public Course get() {
        Course course = EntityFactory.createEntity(Course.class);
        course.setId(id);
        course.setCode(code);
        course.setName(name);
        course.setDesc(desc);
        course.setType(type);
        if (!StringUtils.isBlank(ownerId)) {
            FfeeAccount owner = EntityFactory.createEntity(FfeeAccount.class);
            owner.setId(ownerId);
            course.setOwner(owner);
        }
        return course;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Course.CourseType getType() {
        return type;
    }

    public void setType(Course.CourseType type) {
        this.type = type;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
}
