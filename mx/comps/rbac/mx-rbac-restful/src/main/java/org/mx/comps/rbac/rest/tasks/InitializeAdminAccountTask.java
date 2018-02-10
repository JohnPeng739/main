package org.mx.comps.rbac.rest.tasks;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.DigestUtils;
import org.mx.comps.rbac.dal.entity.Account;
import org.mx.comps.rbac.dal.entity.Role;
import org.mx.dal.EntityFactory;
import org.mx.dal.service.GeneralDictAccessor;
import org.mx.spring.InitializeTask;
import org.mx.spring.SpringContextHolder;

import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;

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
        GeneralDictAccessor accessor = SpringContextHolder.getBean("generalDictAccessor", GeneralDictAccessor.class);

        // 创建相关的角色
        createRole(accessor, "admin", "系统管理员", "系统管理员角色");
        createRole(accessor, "user", "用户", "一般业务操作用户角色");
        createRole(accessor, "guest", "客人", "客人访问角色，只能不需要权限认证的功能");

        // 创建相关的账户
        createAccount(accessor, "admin", "系统管理员", "ds110119", "系统管理员账户", "admin");
        createAccount(accessor, "guest", "客人", "guest", "客人账户", "guest");
    }

    private void createRole(GeneralDictAccessor accessor, String code, String name, String desc) {
        Role role = accessor.getByCode(code, Role.class);
        if (role == null) {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("The role for %s is not exist, will create it.", code));
            }
            role = EntityFactory.createEntity(Role.class);
            role.setCode(code);
            role.setName(name);
            role.setDesc(desc);
            accessor.save(role);
        } else {
            if (logger.isInfoEnabled()) {
                logger.info(String.format("The role for %s has existed, this task will ignored.", code));
            }
        }
    }

    private void createAccount(GeneralDictAccessor accessor, String code, String name, String password, String desc,
                               String... roleCode) {
        Set<Role> roles = new HashSet<>();
        if (roleCode != null && roleCode.length > 0) {
            for (int index = 0; index < roleCode.length; index++) {
                Role role = accessor.getByCode(roleCode[index], Role.class);
                if (role == null) {
                    if (logger.isErrorEnabled()) {
                        logger.error(String.format("The role for %s is not existed.", roleCode));
                    }
                    return;
                }
                roles.add(role);
            }
        }
        Account admin = accessor.getByCode(code, Account.class);
        if (admin == null) {
            if (logger.isInfoEnabled()) {
                logger.info(String.format("The account for %s not exist, will create it.", code));
            }
            try {
                admin = EntityFactory.createEntity(Account.class);
                admin.setCode(code);
                admin.setName(name);
                admin.setPassword(DigestUtils.md5(password));
                admin.setRoles(roles);
                admin.setDesc(desc);
                admin.setValid(true);
                accessor.save(admin);
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Create the %s account successfully.", code));
                }
            } catch (NoSuchAlgorithmException ex) {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Create the %s account fail.", code), ex);
                }
            }
        } else {
            if (logger.isInfoEnabled()) {
                logger.info(String.format("The account for %s has existed, this task will ignored.", code));
            }
        }
    }
}
