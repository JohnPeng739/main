package org.mx.tools.ffee.service;

import org.mx.tools.ffee.dal.entity.Course;

import java.util.ArrayList;
import java.util.List;

public interface CourseService {
    List<CourseBean> getAllCourses();

    List<CourseBean> getAllCoursesByFamily(String familyId);

    CourseBean getCourse(String courseId);

    CourseBean saveCourse(Course course);

    CourseBean deleteCourse(String courseId);

    class CourseBean {
        private String id, code, name, desc;
        private Course.CourseType type;
        private float order;
        private List<CourseBean> children = new ArrayList<>();
        private boolean isPublic = true;

        @SuppressWarnings("unchecked")
        public CourseBean(Course course) {
            super();
            this.id = course.getId();
            this.code = course.getCode();
            this.name = course.getName();
            this.desc = course.getDesc();
            this.type = course.getType();
            this.order = course.getOrder();
            if (course.getChildren() != null && !course.getChildren().isEmpty()) {
                course.getChildren().forEach(child -> this.children.add(new CourseBean((Course)child)));
            }
            this.isPublic = (course.getOwner() == null);
        }

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

        public Course.CourseType getType() {
            return type;
        }

        public float getOrder() {
            return order;
        }

        public List<CourseBean> getChildren() {
            return children;
        }

        public boolean isPublic() {
            return isPublic;
        }
    }
}
