package org.mx.tools.ffee.rest.vo;

import org.mx.StringUtils;
import org.mx.dal.EntityFactory;
import org.mx.tools.ffee.dal.entity.Course;
import org.mx.tools.ffee.dal.entity.Family;
import org.mx.tools.ffee.dal.entity.FfeeAccount;
import org.mx.tools.ffee.dal.entity.MoneyItem;

public class MoneyItemInfoVO {
    private String id, familyId, courseId, desc, ownerId;
    private double money;
    private long occurTime = System.currentTimeMillis();

    public MoneyItem get() {
        MoneyItem moneyItem = EntityFactory.createEntity(MoneyItem.class);
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
        return moneyItem;
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
