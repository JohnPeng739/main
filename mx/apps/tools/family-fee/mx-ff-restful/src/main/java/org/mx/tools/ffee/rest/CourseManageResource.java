package org.mx.tools.ffee.rest;

import org.mx.dal.session.SessionDataStore;
import org.mx.error.UserInterfaceSystemErrorException;
import org.mx.service.rest.vo.DataVO;
import org.mx.tools.ffee.dal.entity.Course;
import org.mx.tools.ffee.rest.vo.CourseInfoVO;
import org.mx.tools.ffee.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("rest/v1")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CourseManageResource {
    private CourseService courseService;
    private SessionDataStore sessionDataStore;

    @Autowired
    public CourseManageResource(CourseService courseService, SessionDataStore sessionDataStore) {
        super();
        this.courseService = courseService;
        this.sessionDataStore = sessionDataStore;
    }

    @Path("courses")
    @GET
    public DataVO<List<CourseService.CourseBean>> getAllCourse() {
        return new DataVO<>(courseService.getAllCourses());
    }

    @Path("courses/families/{familyId}")
    @GET
    public DataVO<List<CourseService.CourseBean>> getAllCoursesByFamily(@PathParam("familyId") String familyId) {
        List<CourseService.CourseBean> courses = courseService.getAllCoursesByFamily(familyId);
        return new DataVO<>(courses);
    }

    @Path("courses/new")
    @POST
    public DataVO<CourseService.CourseBean> newCourse(CourseInfoVO courseInfoVO) {
        if (courseInfoVO == null) {
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        sessionDataStore.setCurrentUserCode(courseInfoVO.getOpenId());
        Course course = courseInfoVO.get();
        course.setId(null);
        return new DataVO<>(courseService.saveCourse(course));
    }

    @Path("courses/{courseId}")
    @PUT
    public DataVO<CourseService.CourseBean> modifyCourse(@PathParam("courseId") String courseId,
                                                         CourseInfoVO courseInfoVO) {
        if (courseInfoVO == null) {
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        sessionDataStore.setCurrentUserCode(courseInfoVO.getOpenId());
        Course course = courseInfoVO.get();
        course.setId(courseId);
        return new DataVO<>(courseService.saveCourse(course));
    }

    @Path("courses/{courseId}")
    @DELETE
    public DataVO<CourseService.CourseBean> deleteCourse(@PathParam("courseId") String courseId,
                                                         @QueryParam("userCode") String userCode) {
        sessionDataStore.setCurrentUserCode(userCode);
        return new DataVO<>(courseService.deleteCourse(courseId));
    }

    @Path("courses/{courseId}")
    @GET
    public DataVO<CourseService.CourseBean> getCourse(@PathParam("courseId") String courseId) {
        return new DataVO<>(courseService.getCourse(courseId));
    }
}
