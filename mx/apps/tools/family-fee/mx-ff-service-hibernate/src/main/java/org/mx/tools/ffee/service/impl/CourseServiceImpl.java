package org.mx.tools.ffee.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.dal.EntityFactory;
import org.mx.dal.service.GeneralDictAccessor;
import org.mx.error.UserInterfaceSystemErrorException;
import org.mx.tools.ffee.dal.entity.Course;
import org.mx.tools.ffee.dal.entity.Family;
import org.mx.tools.ffee.error.UserInterfaceFfeeErrorException;
import org.mx.tools.ffee.repository.CourseRepository;
import org.mx.tools.ffee.service.AccountService;
import org.mx.tools.ffee.service.CourseService;
import org.mx.tools.ffee.service.bean.CourseInfoBean;
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
    private AccountService accountService;

    @Autowired
    public CourseServiceImpl(@Qualifier("generalDictAccessor") GeneralDictAccessor generalDictAccessor,
                             CourseRepository courseRepository,
                             AccountService accountService) {
        super();
        this.generalDictAccessor = generalDictAccessor;
        this.courseRepository = courseRepository;
        this.accountService = accountService;
    }

    private List<CourseBean> transform(List<Course> courses) {
        List<CourseBean> list = new ArrayList<>();
        if (courses != null && !courses.isEmpty()) {
            courses.forEach(course -> list.add(new CourseBean(course)));
        }
        list.sort((o1, o2) -> (int) ((o1.getOrder() - o2.getOrder()) * 1000));
        return list;
    }

    @Transactional
    @Override
    public List<CourseBean> getAllCourses() {
        accountService.writeAccessLog("获取所有科目数据。");
        return transform(courseRepository.findCourses());
    }

    @Transactional
    @Override
    public List<CourseBean> getAllCoursesByFamily(String familyId) {
        Family family = null;
        if (!StringUtils.isBlank(familyId)) {
            family = generalDictAccessor.getById(familyId, Family.class);
        }
        if (family == null) {
            familyId = "";
        }
        accountService.writeAccessLog(String.format("获取%s科目数据。",
                family == null ? "全部" : (family.getName() + "家庭")));
        return transform(courseRepository.findCoursesByFamily(familyId));
    }

    @Transactional
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
        Course course = generalDictAccessor.getById(courseId, Course.class);
        accountService.writeAccessLog(String.format("获取%s科目数据。", course.getName()));
        return new CourseBean(course);
    }

    @Transactional
    @Override
    public CourseBean saveCourse(CourseInfoBean courseInfoBean) {
        if (courseInfoBean == null) {
            if (logger.isErrorEnabled()) {
                logger.error("The course info bean is null.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        String parentId = courseInfoBean.getParentId();
        Course parent = null;
        if (!StringUtils.isBlank(parentId)) {
            parent = generalDictAccessor.getById(parentId, Course.class);
            if (parent == null && logger.isErrorEnabled()) {
                logger.error(String.format("The parent course[%s] not found.", parentId));
                throw new UserInterfaceFfeeErrorException(
                        UserInterfaceFfeeErrorException.FfeeErrors.COURSE_NOT_EXISTED
                );
            }
        }
        String ownerId = courseInfoBean.getOwnerId();
        Family owner = null;
        if (!StringUtils.isBlank(ownerId)) {
            owner = generalDictAccessor.getById(ownerId, Family.class);
            if (owner == null) {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("The family[%s] not found.", ownerId));
                }
                throw new UserInterfaceFfeeErrorException(
                        UserInterfaceFfeeErrorException.FfeeErrors.FAMILY_NOT_EXISTED
                );
            }
        }
        String id = courseInfoBean.getId(),
                code = courseInfoBean.getCode();
        Course saved = null;
        if (!StringUtils.isBlank(code)) {
            // 根据代码来判定科目是否已经存在
            Course checked = generalDictAccessor.getByCode(code, Course.class);
            if (checked != null && StringUtils.isBlank(id) && checked.isValid()) {
                // 如果ID为空而checked不为null，表示新增了同样代码的科目
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("The course[%s] has existed, code: %s.", id, code));
                }
                throw new UserInterfaceFfeeErrorException(
                        UserInterfaceFfeeErrorException.FfeeErrors.COURSE_HAS_EXISTED
                );
            } else if (checked != null) {
                // 修改科目
                saved = checked;

            }
            if (saved == null) {
                saved = EntityFactory.createEntity(Course.class);
            }
            saved.setParent(parent);
            saved.setType(courseInfoBean.getType());
            saved.setCode(courseInfoBean.getCode());
            saved.setName(courseInfoBean.getName());
            saved.setDesc(courseInfoBean.getDesc());
            saved.setOwner(owner);
            saved.setValid(true);
            saved = generalDictAccessor.save(saved);
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Save course[%s] successfully, code: %s, name: %s, desc: %s.",
                        saved.getId(), saved.getCode(), saved.getName(), saved.getDesc()));
            }
            accountService.writeAccessLog(String.format("新增或修改%s科目数据。", saved.getName()));
            return new CourseBean(saved);
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
    public CourseBean deleteCourse(CourseInfoBean courseInfoBean) {
        if (courseInfoBean == null) {
            if (logger.isErrorEnabled()) {
                logger.error("The course info bean is null.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        String courseId = courseInfoBean.getId();
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
        accountService.writeAccessLog(String.format("删除%s科目数据。", course.getName()));
        return new CourseBean(course);
    }
}
