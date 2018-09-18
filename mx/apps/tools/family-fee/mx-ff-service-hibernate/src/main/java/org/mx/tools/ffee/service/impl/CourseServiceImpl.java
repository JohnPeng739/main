package org.mx.tools.ffee.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.dal.service.GeneralDictAccessor;
import org.mx.error.UserInterfaceSystemErrorException;
import org.mx.tools.ffee.dal.entity.Course;
import org.mx.tools.ffee.error.UserInterfaceFfeeErrorException;
import org.mx.tools.ffee.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component("courseService")
public class CourseServiceImpl implements CourseService {
    private static final Log logger = LogFactory.getLog(CourseServiceImpl.class);

    private GeneralDictAccessor generalDictAccessor;

    @Autowired
    public CourseServiceImpl(@Qualifier("generalDictAccessor") GeneralDictAccessor generalDictAccessor) {
        super();
        this.generalDictAccessor = generalDictAccessor;
    }

    @Override
    public List<Course> getAllCourses() {
        return generalDictAccessor.list(Course.class);
    }

    @Transactional(readOnly = true)
    @Override
    public Course getCourse(String courseId) {
        if (StringUtils.isBlank(courseId)) {
            if (logger.isErrorEnabled()) {
                logger.error("The course's id is blank.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        return generalDictAccessor.getById(courseId, Course.class);
    }

    @Transactional
    @Override
    public Course saveCourse(Course course) {
        if (course == null) {
            if (logger.isErrorEnabled()) {
                logger.error("The course is null.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        if (!StringUtils.isBlank(course.getOwner().getId()))
        if (!StringUtils.isBlank(course.getCode())) {
            // 根据代码来判定科目是否已经存在
            Course checked = generalDictAccessor.getByCode(course.getCode(), Course.class);
            if (checked != null && StringUtils.isBlank(course.getId())) {
                // 如果ID为空而checked不为null，表示新增了同样代码的科目
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("The course[%s] has existed, code: %s.",
                            course.getId(), course.getCode()));
                }
                throw new UserInterfaceFfeeErrorException(
                        UserInterfaceFfeeErrorException.FfeeErrors.COURSE_HAS_EXISTED
                );
            } else if (checked != null && !StringUtils.isBlank(course.getId())) {
                // 修改科目
                checked.setType(course.getType());
            }
        }
        return null;
    }

    @Transactional
    @Override
    public Course deleteCourse(String courseId) {
        if (StringUtils.isBlank(courseId)) {
            if (logger.isErrorEnabled()) {
                logger.error("The course's id is blank.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        Course course = generalDictAccessor.remove(courseId, Course.class);
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Logical remove the course[%s] successfully.", courseId));
        }
        return course;
    }
}
