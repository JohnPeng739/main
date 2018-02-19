package org.mx.tools.ffee.service;

import org.mx.tools.ffee.dal.entity.Family;

/**
 * 描述： 家庭维护服务接口定义
 *
 * @author John.Peng
 *         Date time 2018/2/18 下午8:04
 */
public interface FamilyManageService {
    /**
     * 创建一个家庭
     *
     * @param name          家庭名称
     * @param ffeeAccountId 家主账户ID
     * @param memberRole    家主成员角色
     * @return 创建好的家庭对象
     */
    Family createFamily(String name, String ffeeAccountId, String memberRole);

    /**
     * 作为成员加入一个家庭
     *
     * @param familyId      家庭ID
     * @param ffeeAccountId 成员账户ID
     * @param memberRole    成员角色
     * @return 加入的家庭对象
     */
    Family joinFamily(String familyId, String ffeeAccountId, String memberRole);

    /**
     * 根据家庭ID或者家庭名称获取家庭信息对象，优先使用家庭ID
     *
     * @param id   家庭ID
     * @param name 家庭名称
     * @return 家庭对象
     */
    Family getFamily(String id, String name);
}
