package org.mx.tools.ffee.dal.entity;

import org.mx.dal.entity.BaseDictTree;

/**
 * 描述： 科目类型信息定义，科目为树状字典。
 *
 * @author John.Peng
 * Date time 2018/2/17 下午4:46
 */
public interface Course extends BaseDictTree {
    CourseType getType();

    void setType(CourseType type);

    Family getOwner();

    void setOwner(Family owner);

    float getOrder();

    void setOrder(float order);

    enum CourseType {
        INCOME, SPENDING, ALL
    }
}
