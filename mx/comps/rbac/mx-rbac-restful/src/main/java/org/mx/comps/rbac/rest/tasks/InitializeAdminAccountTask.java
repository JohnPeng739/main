package org.mx.comps.rbac.rest.tasks;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.DigestUtils;
import org.mx.comps.rbac.dal.entity.Account;
import org.mx.dal.EntityFactory;
import org.mx.dal.service.GeneralDictAccessor;
import org.mx.spring.InitializeTask;
import org.mx.spring.SpringContextHolder;

import java.security.NoSuchAlgorithmException;

/**
 * 初始化管理员账户的任务
 *
 * @author : john.peng created on date : 2018/1/17
 */
public class InitializeAdminAccountTask extends InitializeTask {
    private static final Log logger = LogFactory.getLog(InitializeAdminAccountTask.class);

    /**
     * 默认的构造函数
     */
    public InitializeAdminAccountTask() {
        super("Initialize admin account task", false);
    }

    /**
     * {@inheritDoc}
     *
     * @see InitializeTask#invokeTask()
     */
    @Override
    public void invokeTask() {
        GeneralDictAccessor accessor = SpringContextHolder.getBean("generalDictAccessor"); //super.context.getBean("generalDictAccessor", GeneralDictAccessor.class);
        Account admin = accessor.getByCode("admin", Account.class);
        if (admin == null) {
            if (logger.isInfoEnabled()) {
                logger.info("The administrator not exist, will initialize it.");
            }
            try {
                admin = EntityFactory.createEntity(Account.class);
                admin.setCode("admin");
                admin.setName("管理员");
                admin.setPassword(DigestUtils.md5("ds110119"));
                admin.setValid(true);
                accessor.save(admin);
                if (logger.isDebugEnabled()) {
                    logger.debug("Create the administrator account successfully.");
                }
            } catch (NoSuchAlgorithmException ex) {
                if (logger.isErrorEnabled()) {
                    logger.error("Create the administrator account fail.", ex);
                }
            }
        } else {
            if (logger.isInfoEnabled()) {
                logger.info("The administrator has existed, this task ignored.");
            }
        }
    }
}
