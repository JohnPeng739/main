package org.mx.tools.ffee.rest;

import org.mx.StringUtils;
import org.mx.error.UserInterfaceSystemErrorException;
import org.mx.service.rest.vo.DataVO;
import org.mx.tools.ffee.dal.entity.Course;
import org.mx.tools.ffee.rest.vo.CourseInfoVO;
import org.mx.tools.ffee.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Component("courseManageResource")
@Path("rest/v1")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CourseManageResource {
    private CourseService courseService;

    @Autowired
    public CourseManageResource(CourseService courseService) {
        super();
        this.courseService = courseService;
    }

    @Path("courses")
    @GET
    public DataVO<List<Course>> getAllCourse() {
        return new DataVO<>(courseService.getAllCourses());
    }

    @Path("courses/new")
    @POST
    public DataVO<Course> newCourse(CourseInfoVO courseInfoVO) {
        if (courseInfoVO == null) {
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        Course course = courseInfoVO.get();
        course.setId(null);
        return new DataVO<>(courseService.saveCourse(course));
    }

    @Path("courses/{courseId}")
    @PUT
    public DataVO<Course> modifyCourse(@PathParam("courseId") String courseId, CourseInfoVO courseInfoVO) {
        if (StringUtils.isBlank(courseId) || courseInfoVO == null) {
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        Course course = courseInfoVO.get();
        return new DataVO<>(courseService.saveCourse(course));
    }

    @Path("courses/{courseId}")
    @DELETE
    public DataVO<Course> deleteCourse(@PathParam("courseId") String courseId) {
        if (StringUtils.isBlank(courseId)) {
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        return new DataVO<>(courseService.deleteCourse(courseId));
    }

    @Path("courses/{courseId}")
    @GET
    public DataVO<Course> getCourse(@PathParam("courseId") String courseId) {
        if (StringUtils.isBlank(courseId)) {
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        return new DataVO<>(courseService.getCourse(courseId));
    }
}
