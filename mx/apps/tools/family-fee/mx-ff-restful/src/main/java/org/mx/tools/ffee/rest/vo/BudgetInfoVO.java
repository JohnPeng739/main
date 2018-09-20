package org.mx.tools.ffee.rest.vo;

import org.mx.StringUtils;
import org.mx.dal.EntityFactory;
import org.mx.tools.ffee.dal.entity.BudgetItem;
import org.mx.tools.ffee.dal.entity.Course;
import org.mx.tools.ffee.dal.entity.Family;

public class BudgetInfoVO extends BaseParamsVO {
    private String id, desc, courseId, familyId;
    private int year;
    private double money;

    public BudgetItem get() {
        BudgetItem budgetItem = EntityFactory.createEntity(BudgetItem.class);
        budgetItem.setId(id);
        budgetItem.setDesc(desc);
        budgetItem.setYear(year);
        budgetItem.setMoney(money);
        if (!StringUtils.isBlank(familyId)) {
            Family family = EntityFactory.createEntity(Family.class);
            family.setId(familyId);
            budgetItem.setFamily(family);
        }
        if (!StringUtils.isBlank(courseId)) {
            Course course = EntityFactory.createEntity(Course.class);
            course.setId(courseId);
            budgetItem.setCourse(course);
        }
        return budgetItem;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFamilyId() {
        return familyId;
    }

    public void setFamilyId(String familyId) {
        this.familyId = familyId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }
}
