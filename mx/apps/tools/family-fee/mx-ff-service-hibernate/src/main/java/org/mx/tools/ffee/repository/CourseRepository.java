package org.mx.tools.ffee.repository;

import org.mx.tools.ffee.dal.entity.Course;
import org.mx.tools.ffee.dal.entity.CourseEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface CourseRepository extends Repository<CourseEntity, String> {
    @Query("SELECT c FROM CourseEntity  c WHERE c.parent IS NULL")
    List<Course> findCourses();

    @Query("SELECT c FROM CourseEntity  c WHERE c.parent IS NULL AND (c.owner IS NULL OR c.owner.id = ?1)")
    List<Course> findCoursesByFamily(String familyId);
}
