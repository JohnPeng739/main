package org.mx.kbm.service;

import org.mx.kbm.entity.KnowledgeContact;
import org.mx.kbm.service.bean.ContactRegisterRequest;

/**
 * 描述： 联系人信息服务接口定义
 *
 * @author John.Peng
 *         Date time 2018/4/29 下午9:39
 */
public interface ContactService {
    /**
     * 根据指定的联系人代码获取联系人对象
     *
     * @param code 联系人代码
     * @return 联系人对象，如果不存在，则返回null。
     */
    KnowledgeContact getContactByCode(String code);

    /**
     * 根据输入的联系人信息注册一个新的联系人账户
     *
     * @param registerRequest 注册信息
     * @return 注册成功的联系人账户，如果发生异常，则抛出异常。
     */
    KnowledgeContact register(ContactRegisterRequest registerRequest);

    /**
     * 根据输入的联系人信息注册一个新的联系人账户
     *
     * @param registerRequest 注册信息
     * @param needWriteLog    设置为true，表示记录日志，否则不记录
     * @return 注册成功的联系人账户，如果发生异常，则抛出异常。
     */
    KnowledgeContact register(ContactRegisterRequest registerRequest, boolean needWriteLog);
}
