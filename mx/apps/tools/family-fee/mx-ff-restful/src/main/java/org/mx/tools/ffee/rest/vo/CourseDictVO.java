package org.mx.tools.ffee.rest.vo;

import org.mx.tools.ffee.dal.entity.Course;

import java.util.ArrayList;
import java.util.List;

public class CourseDictVO {
    private String id, code, name, desc;
    private Course.CourseType type;

    public static CourseDictVO valueOf(Course course) {
        if (course == null) {
            return null;
        }
        CourseDictVO vo = new CourseDictVO();
        vo.id = course.getId();
        vo.type = course.getType();
        vo.code = course.getCode();
        vo.name = course.getName();
        vo.desc = course.getDesc();
        return vo;
    }

    public static List<CourseDictVO> valueOf(List<Course> courses) {
        List<CourseDictVO> list = new ArrayList<>();
        if (courses != null && !courses.isEmpty()) {
            courses.forEach(course -> list.add(valueOf(course)));
        }
        return list;
    }

    public String getId() {
        return id;
    }

    public Course.CourseType getType() {
        return type;
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
}
