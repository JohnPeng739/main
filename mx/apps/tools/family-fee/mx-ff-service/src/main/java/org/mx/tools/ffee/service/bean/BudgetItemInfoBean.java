package org.mx.tools.ffee.service.bean;

public class BudgetItemInfoBean {
    private String id, courseId, familyId, desc;
    private int year;
    private double money;
    private boolean valid;

    public String getId() {
        return id;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getFamilyId() {
        return familyId;
    }

    public String getDesc() {
        return desc;
    }

    public int getYear() {
        return year;
    }

    public double getMoney() {
        return money;
    }

    public boolean isValid() {
        return valid;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public void setFamilyId(String familyId) {
        this.familyId = familyId;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
