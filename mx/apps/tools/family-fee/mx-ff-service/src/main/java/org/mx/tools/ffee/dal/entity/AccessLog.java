package org.mx.tools.ffee.dal.entity;

import org.mx.dal.entity.Base;

/**
 * 描述： 账户访问操作日志接口定义
 *
 * @author john peng
 * Date time 2018/9/15 下午9:00
 */
public interface AccessLog extends Base {
    String getAccountCode();

    void setAccountCode(String accountCode);

    String getContent();

    void setContent(String content);

    double getLatitude();

    void setLatitude(double latitude);

    double getLongitude();

    void setLongitude(double longitude);
}
