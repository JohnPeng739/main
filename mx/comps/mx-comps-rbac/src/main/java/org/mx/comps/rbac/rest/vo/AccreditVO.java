package org.mx.comps.rbac.rest.vo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.comps.rbac.dal.entity.Account;
import org.mx.comps.rbac.dal.entity.Accredit;
import org.mx.comps.rbac.dal.entity.Role;
import org.mx.dal.EntityFactory;
import org.mx.dal.exception.EntityInstantiationException;
import org.mx.rest.vo.BaseVO;

import java.util.Date;
import java.util.List;

public class AccreditVO extends BaseVO {
    private static final Log logger = LogFactory.getLog(AccreditVO.class);

    private AccountVO src, tar;
    private List<RoleVO> roles;
    private long startTime, endTime;
    private String desc;

    public static void transform(Accredit accredit, AccreditVO accreditVO) {
        if (accredit == null || accreditVO == null) {
            return;
        }
        BaseVO.transform(accredit, accreditVO);
        accreditVO.startTime = accredit.getStartTime() == null ? 0 : accredit.getStartTime().getTime();
        accreditVO.endTime = accredit.getEndTime() == null ? 0 : accredit.getEndTime().getTime();
        accreditVO.desc = accredit.getDesc();
        if (accredit.getSrc() != null) {
            AccountVO vo = new AccountVO();
            AccountVO.transform(accredit.getSrc(), vo);
            accreditVO.src = vo;
        }
        if (accredit.getTar() != null) {
            AccountVO vo = new AccountVO();
            AccountVO.transform(accredit.getTar(), vo);
            accreditVO.tar = vo;
        }
        List<Role> roles = accredit.getRoles();
        if (roles != null && !roles.isEmpty()) {
            accreditVO.roles = RoleVO.transformRoleVOs(roles);
        }
    }

    public static void transform(AccreditVO accreditVO, Accredit accredit) {
        if (accredit == null || accreditVO == null) {
            return;
        }
        BaseVO.transform(accreditVO, accredit);
        accredit.setStartTime(new Date(accreditVO.getStartTime()));
        accredit.setEndTime(new Date(accreditVO.getEndTime()));
        accredit.setDesc(accreditVO.getDesc());
        if (accreditVO.getSrc() != null) {
            try {
                Account account = EntityFactory.createEntity(Account.class);
                AccountVO.transform(accreditVO.getSrc(), account);
                accredit.setSrc(account);
            } catch (EntityInstantiationException ex) {
                if (logger.isWarnEnabled()) {
                    logger.warn(ex);
                }
            }
        }
        if (accreditVO.getTar() != null) {
            try {
                Account account = EntityFactory.createEntity(Account.class);
                AccountVO.transform(accreditVO.getTar(), account);
                accredit.setTar(account);
            } catch (EntityInstantiationException ex) {
                if (logger.isWarnEnabled()) {
                    logger.warn(ex);
                }
            }
        }
        List<RoleVO> roleVOs = accreditVO.getRoles();
        if (roleVOs != null && !roleVOs.isEmpty()) {
            accredit.setRoles(RoleVO.transformRoles(roleVOs));
        }
    }

    public void setSrc(AccountVO src) {
        this.src = src;
    }

    public void setTar(AccountVO tar) {
        this.tar = tar;
    }

    public void setRoles(List<RoleVO> roles) {
        this.roles = roles;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public AccountVO getSrc() {
        return src;
    }

    public AccountVO getTar() {
        return tar;
    }

    public List<RoleVO> getRoles() {
        return roles;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public String getDesc() {
        return desc;
    }
}
