package org.mx.tools.ffee.service;

import org.mx.tools.ffee.dal.entity.FfeeAccount;

/**
 * 描述： FFEE账户服务接口定义。
 *
 * @author John.Peng
 *         Date time 2018/2/18 上午11:39
 */
public interface FfeeAccountManageService {
    FfeeAccount regist(String code, String name, String password);

    // TODO 添加： 微信、微博、QQ等第三方接入。
}
