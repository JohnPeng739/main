package org.mx.tools.ffee.service.bean;

public class MoneyInfoBean {
    private String id, familyId, courseId, ownerId, desc;
    private double money;
    private long occurTime;

    public String getId() {
        return id;
    }

    public String getFamilyId() {
        return familyId;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getDesc() {
        return desc;
    }

    public double getMoney() {
        return money;
    }

    public long getOccurTime() {
        return occurTime;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFamilyId(String familyId) {
        this.familyId = familyId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public void setOccurTime(long occurTime) {
        this.occurTime = occurTime;
    }
}
