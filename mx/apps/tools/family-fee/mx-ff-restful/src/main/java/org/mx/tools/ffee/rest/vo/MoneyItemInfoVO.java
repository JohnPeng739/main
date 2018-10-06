package org.mx.tools.ffee.rest.vo;

import org.mx.StringUtils;
import org.mx.dal.EntityFactory;
import org.mx.tools.ffee.dal.entity.*;

public class MoneyItemInfoVO extends BaseParamsVO {
    private String id, familyId, courseId, desc, ownerId;
    private double money;
    private long occurTime = System.currentTimeMillis();

    private void transform(MoneyItem moneyItem) {
        moneyItem.setId(id);
        moneyItem.setDesc(desc);
        moneyItem.setMoney(money);
        moneyItem.setOccurTime(occurTime);
        if (!StringUtils.isBlank(familyId)) {
            Family family = EntityFactory.createEntity(Family.class);
            family.setId(familyId);
            moneyItem.setFamily(family);
        }
        if (!StringUtils.isBlank(courseId)) {
            Course course = EntityFactory.createEntity(Course.class);
            course.setId(courseId);
            moneyItem.setCourse(course);
        }
        if (!StringUtils.isBlank(ownerId)) {
            FfeeAccount account = EntityFactory.createEntity(FfeeAccount.class);
            account.setId(ownerId);
            moneyItem.setOwner(account);
        }
    }

    public Income getIncome() {
        Income income = EntityFactory.createEntity(Income.class);
        transform(income);
        return income;
    }

    public Spending getSpending() {
        Spending spending = EntityFactory.createEntity(Spending.class);
        transform(spending);
        return spending;
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

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public long getOccurTime() {
        return occurTime;
    }

    public void setOccurTime(long occurTime) {
        this.occurTime = occurTime;
    }
}
