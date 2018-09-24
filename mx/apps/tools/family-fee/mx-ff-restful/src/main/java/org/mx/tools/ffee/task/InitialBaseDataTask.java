package org.mx.tools.ffee.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.dal.EntityFactory;
import org.mx.dal.service.GeneralDictAccessor;
import org.mx.spring.task.BaseTask;
import org.mx.tools.ffee.dal.entity.Course;
import org.mx.tools.ffee.dal.entity.Family;
import org.mx.tools.ffee.dal.entity.FfeeAccount;
import org.mx.tools.ffee.service.AccountService;
import org.mx.tools.ffee.service.FamilyService;
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
    private FamilyService familyService;

    @Autowired
    public InitialBaseDataTask(AccountService accountService,
                               FamilyService familyService,
                               @Qualifier("generalDictAccessor") GeneralDictAccessor generalDictAccessor) {
        super();
        this.accountService = accountService;
        this.familyService = familyService;
        this.generalDictAccessor = generalDictAccessor;
    }

    private void initialMyAccount() {
        String openId = "oZmYI0Yr31LJDlDqTaE9gpm6OTPQ";
        AccountService.AccountSummary summary = accountService.getAccountSummaryByOpenId(openId);
        if (summary == null || summary.getAccount() == null) {
            FfeeAccount account = EntityFactory.createEntity(FfeeAccount.class);
            account.setNickname("上善若水");
            account.setOpenId(openId);
            account.setMobile("13701760212");
            account.setEmail("josh_73_9@hotmail");
            accountService.registry(account);
            if (logger.isDebugEnabled()) {
                logger.debug("Create the default account successfully.");
            }
        }
        if (summary == null || summary.getFamily() == null) {
            Family family = EntityFactory.createEntity(Family.class);
            family.setName("我爱我家");
            family.setDesc("这是一个相亲相爱的家庭。");
            familyService.createFamily(family, openId, "爸爸");
            if (logger.isDebugEnabled()) {
                logger.debug("Create the default family successfully.");
            }
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
                "/org/mx/tools/ffee/task/public-courses.json")) {
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
        initialMyAccount();
        initialPublicCourses();
        if (logger.isInfoEnabled()) {
            logger.info("Initialize the public courses successfully.");
        }
    }
}
