package org.mx.tools.ffee.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.dal.service.GeneralDictAccessor;
import org.mx.error.UserInterfaceSystemErrorException;
import org.mx.tools.ffee.dal.entity.Course;
import org.mx.tools.ffee.dal.entity.Family;
import org.mx.tools.ffee.error.UserInterfaceFfeeErrorException;
import org.mx.tools.ffee.repository.CourseRepository;
import org.mx.tools.ffee.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component("courseService")
public class CourseServiceImpl implements CourseService {
    private static final Log logger = LogFactory.getLog(CourseServiceImpl.class);

    private GeneralDictAccessor generalDictAccessor;
    private CourseRepository courseRepository;

    @Autowired
    public CourseServiceImpl(@Qualifier("generalDictAccessor") GeneralDictAccessor generalDictAccessor,
                             CourseRepository courseRepository) {
        super();
        this.generalDictAccessor = generalDictAccessor;
        this.courseRepository = courseRepository;
    }

    private List<CourseBean> transform(List<Course> courses) {
        List<CourseBean> list = new ArrayList<>();
        if (courses != null && !courses.isEmpty()) {
            courses.forEach(course -> list.add(new CourseBean(course)));
        }
        list.sort((o1, o2) -> (int)((o1.getOrder() - o2.getOrder()) * 1000));
        return list;
    }

    @Transactional(readOnly = true)
    @Override
    public List<CourseBean> getAllCourses() {
        return transform(courseRepository.findCourses());
    }

    @Transactional(readOnly = true)
    @Override
    public List<CourseBean> getAllCoursesByFamily(String familyId) {
        if (StringUtils.isBlank(familyId)) {
            familyId = "";
        }
        return transform(courseRepository.findCoursesByFamily(familyId));
    }

    @Transactional(readOnly = true)
    @Override
    public CourseBean getCourse(String courseId) {
        if (StringUtils.isBlank(courseId)) {
            if (logger.isErrorEnabled()) {
                logger.error("The course's id is blank.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        return new CourseBean(generalDictAccessor.getById(courseId, Course.class));
    }

    @Transactional
    @Override
    public CourseBean saveCourse(Course course) {
        if (course == null) {
            if (logger.isErrorEnabled()) {
                logger.error("The course is null.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        Family owner = null;
        if (course.getOwner() != null && !StringUtils.isBlank(course.getOwner().getId())) {
            owner = generalDictAccessor.getById(course.getOwner().getId(), Family.class);
            if (owner == null) {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("The family[%s] not found.", course.getOwner().getId()));
                }
                throw new UserInterfaceFfeeErrorException(
                        UserInterfaceFfeeErrorException.FfeeErrors.FAMILY_NOT_EXISTED
                );
            }
        }
        if (!StringUtils.isBlank(course.getCode())) {
            // 根据代码来判定科目是否已经存在
            Course checked = generalDictAccessor.getByCode(course.getCode(), Course.class);
            if (checked != null && StringUtils.isBlank(course.getId()) && checked.isValid()) {
                // 如果ID为空而checked不为null，表示新增了同样代码的科目
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("The course[%s] has existed, code: %s.",
                            course.getId(), course.getCode()));
                }
                throw new UserInterfaceFfeeErrorException(
                        UserInterfaceFfeeErrorException.FfeeErrors.COURSE_HAS_EXISTED
                );
            } else if (checked != null) {
                // 修改科目
                checked.setType(course.getType());
                checked.setCode(course.getCode());
                checked.setName(course.getName());
                checked.setDesc(course.getDesc());
                course = checked;
            }
            course.setOwner(owner);
            course.setValid(true);
            course = generalDictAccessor.save(course);
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Save course[%s] successfully, code: %s, name: %s, desc: %s.",
                        course.getId(), course.getCode(), course.getName(), course.getDesc()));
            }
            return new CourseBean(course);
        } else {
            if (logger.isErrorEnabled()) {
                logger.error("The course's code is blank.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
    }

    @Transactional
    @Override
    public CourseBean deleteCourse(String courseId) {
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
        return new CourseBean(course);
    }
}
