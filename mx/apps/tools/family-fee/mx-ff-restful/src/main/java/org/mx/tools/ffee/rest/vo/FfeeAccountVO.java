package org.mx.tools.ffee.rest.vo;

import org.mx.comps.rbac.rest.vo.AccountVO;
import org.mx.service.rest.vo.BaseVO;
import org.mx.tools.ffee.dal.entity.FfeeAccount;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 描述： FFEE账户信息值对象
 *
 * @author John.Peng
 *         Date time 2018/2/18 下午4:58
 */
public class FfeeAccountVO extends BaseVO {
    private FfeeAccount.AccountSourceType sourceType;
    private AccountVO accountVO;

    public static FfeeAccountVO transform(FfeeAccount ffeeAccount) {
        if (ffeeAccount == null) {
            return null;
        }
        FfeeAccountVO vo = new FfeeAccountVO();
        BaseVO.transform(ffeeAccount, vo);
        vo.sourceType = ffeeAccount.getSourceType();
        if (ffeeAccount.getAccount() != null) {
            vo.accountVO = AccountVO.transform(ffeeAccount.getAccount(), false);
        }
        return vo;
    }

    public static List<FfeeAccountVO> transform(Collection<FfeeAccount> ffeeAccounts) {
        if (ffeeAccounts == null) {
            return null;
        }
        List<FfeeAccountVO> list = new ArrayList<>();
        ffeeAccounts.forEach(ffeeAccount -> {
            FfeeAccountVO vo = FfeeAccountVO.transform(ffeeAccount);
            if (vo != null) {
                list.add(vo);
            }
        });
        return list;
    }

    public FfeeAccount.AccountSourceType getSourceType() {
        return sourceType;
    }

    public void setSourceType(FfeeAccount.AccountSourceType sourceType) {
        this.sourceType = sourceType;
    }

    public AccountVO getAccountVO() {
        return accountVO;
    }

    public void setAccountVO(AccountVO accountVO) {
        this.accountVO = accountVO;
    }
}
