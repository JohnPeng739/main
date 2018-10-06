package org.mx.tools.ffee.service.bean;

import org.mx.tools.ffee.dal.entity.Course;

/**
 * 描述：
 *
 * @author : john date : 2018/10/6 下午4:48
 */
public class CourseInfoBean {
    private String id, code, name, desc, ownerId;
    private Course.CourseType type;
    private boolean valid;

    public String getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public Course.CourseType getType() {
        return type;
    }

    public boolean isValid() {
        return valid;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public void setType(Course.CourseType type) {
        this.type = type;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
