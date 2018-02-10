package org.mx.comps.rbac.rest.vo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.comps.rbac.dal.entity.Accredit;
import org.mx.comps.rbac.dal.entity.Role;
import org.mx.service.rest.vo.BaseVO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class AccreditVO extends BaseVO {
    private static final Log logger = LogFactory.getLog(AccreditVO.class);

    private AccountVO src, tar;
    private List<RoleVO> roles;
    private long startTime, endTime;
    private String desc;
    private boolean closed;

    public static AccreditVO transform(Accredit accredit, boolean iterate) {
        if (accredit == null) {
            return null;
        }
        AccreditVO accreditVO = new AccreditVO();
        BaseVO.transform(accredit, accreditVO);
        accreditVO.startTime = accredit.getStartTime() == null ? 0 : accredit.getStartTime().getTime();
        accreditVO.endTime = accredit.getEndTime() == null ? 0 : accredit.getEndTime().getTime();
        accreditVO.closed = accredit.isClosed();
        accreditVO.desc = accredit.getDesc();
        if (accredit.getSrc() != null) {
            accreditVO.src = AccountVO.transform(accredit.getSrc(), false);
        }
        if (accredit.getTar() != null) {
            accreditVO.tar = AccountVO.transform(accredit.getTar(), false);
        }
        Set<Role> roles = accredit.getRoles();
        if (roles != null && !roles.isEmpty() && iterate) {
            accreditVO.roles = RoleVO.transform(roles);
        }
        return accreditVO;
    }

    public static List<AccreditVO> transform(Collection<Accredit> accredits) {
        List<AccreditVO> accreditVOS = new ArrayList<>();
        if (accredits != null && !accredits.isEmpty()) {
            for (Accredit accredit : accredits) {
                AccreditVO accreditVO = AccreditVO.transform(accredit, false);
                if (accreditVO != null) {
                    accreditVOS.add(accreditVO);
                }
            }
        }
        return accreditVOS;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public AccountVO getSrc() {
        return src;
    }

    public void setSrc(AccountVO src) {
        this.src = src;
    }

    public AccountVO getTar() {
        return tar;
    }

    public void setTar(AccountVO tar) {
        this.tar = tar;
    }

    public List<RoleVO> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleVO> roles) {
        this.roles = roles;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
