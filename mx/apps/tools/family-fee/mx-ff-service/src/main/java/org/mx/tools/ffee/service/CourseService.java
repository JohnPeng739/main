package org.mx.tools.ffee.service;

import org.mx.tools.ffee.dal.entity.Course;

import java.util.List;

public interface CourseService {
    List<Course> getAllCourses();

    Course getCourse(String courseId);

    Course saveCourse(Course course);

    Course deleteCourse(String courseId);
}
