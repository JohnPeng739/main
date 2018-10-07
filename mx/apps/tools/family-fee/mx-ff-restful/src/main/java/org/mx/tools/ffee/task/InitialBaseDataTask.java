package org.mx.tools.ffee.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.DigestUtils;
import org.mx.dal.EntityFactory;
import org.mx.dal.service.GeneralDictAccessor;
import org.mx.dal.session.SessionDataStore;
import org.mx.spring.task.BaseTask;
import org.mx.tools.ffee.dal.entity.Course;
import org.mx.tools.ffee.error.UserInterfaceFfeeErrorException;
import org.mx.tools.ffee.service.AccountService;
import org.mx.tools.ffee.service.FamilyService;
import org.mx.tools.ffee.service.bean.AccountInfoBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component("initialBaseDataTask")
public class InitialBaseDataTask extends BaseTask {
    private static final Log logger = LogFactory.getLog(InitialBaseDataTask.class);

    private AccountService accountService;
    private GeneralDictAccessor generalDictAccessor;
    private SessionDataStore sessionDataStore;
    private FamilyService familyService;

    @Autowired
    public InitialBaseDataTask(AccountService accountService,
                               FamilyService familyService,
                               SessionDataStore sessionDataStore,
                               @Qualifier("generalDictAccessor") GeneralDictAccessor generalDictAccessor) {
        super();
        this.accountService = accountService;
        this.familyService = familyService;
        this.sessionDataStore = sessionDataStore;
        this.generalDictAccessor = generalDictAccessor;
    }

    private void initialAdminAccount() {
        String openId = "Administrator";
        AccountService.AccountSummary summary = accountService.getAccountSummaryByOpenId(openId);
        if (summary == null || summary.getAccount() == null) {
            AccountInfoBean accountInfoBean = new AccountInfoBean();
            accountInfoBean.setNickname("管理员");
            accountInfoBean.setOpenId(openId);
            accountInfoBean.setPassword(DigestUtils.md5("edmund-FFEE"));
            accountInfoBean.setMobile("12345678901");
            accountInfoBean.setEmail("admin@ffee.com");
            summary = accountService.registry(accountInfoBean);
            if (logger.isDebugEnabled()) {
                logger.debug("Create the default account successfully.");
            }
        }
        if (summary.getAccount() == null) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("The account[%s] not found.", openId));
            }
            throw new UserInterfaceFfeeErrorException(
                    UserInterfaceFfeeErrorException.FfeeErrors.ACCOUNT_NOT_EXISTED
            );
        }
    }

    @SuppressWarnings("unchecked")
    private void importCourses(JSONArray array, Course parent) {
        for (int index = 0; index < array.size(); index++) {
            JSONObject json = array.getJSONObject(index);
            String code = json.getString("code");
            Course course = generalDictAccessor.getByCode(code, Course.class);
            if (course == null) {
                course = EntityFactory.createEntity(Course.class);
                course.setCode(json.getString("code"));
                course.setName(json.getString("name"));
                course.setType(Course.CourseType.valueOf(json.getString("type")));
                course.setDesc(json.getString("desc"));
                course.setOrder(json.getFloat("order"));
                course.setParent(parent);
                generalDictAccessor.save(course);
            }
            JSONArray children = json.getJSONArray("children");
            if (children != null && children.size() > 0) {
                importCourses(children, course);
            }
        }
    }

    private void initialPublicCourses() {
        try (InputStream in = InitialBaseDataTask.class.getResourceAsStream(
                "/public-courses.json")) {
            importCourses(JSON.parseObject(in, JSONArray.class), null);
        } catch (IOException ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Load the courses json file fail.", ex);
            }
        }
    }

    @Override
    public void invoke() {
        if (logger.isInfoEnabled()) {
            logger.info("Initialize the public courses......");
        }
        sessionDataStore.setCurrentUserCode("System");
        initialAdminAccount();
        initialPublicCourses();
        sessionDataStore.removeCurrentUserCode();
        if (logger.isInfoEnabled()) {
            logger.info("Initialize the public courses successfully.");
        }
    }
}
