package org.mx.comps.rbac.service;

import org.mx.comps.rbac.dal.entity.Role;
import org.mx.comps.rbac.error.UserInterfaceErrorException;
import org.mx.dal.service.GeneralDictAccessor;

/**
 * Created by john on 2017/11/19.
 */
public interface RoleManageService extends GeneralDictAccessor {
    Role save(String id, Role role) throws UserInterfaceErrorException;

}
