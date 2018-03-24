package org.mx.kbm.service;

import org.mx.kbm.entity.KTag;
import org.mx.kbm.entity.Tenant;

import java.util.List;

/**
 * 描述： 知识标签服务接口定义
 *
 * @author John.Peng
 *         Date time 2018/3/24 下午9:47
 */
public interface KTagService {
    KTag getTag(String tag);
    KTag saveTag(KTag tag);

    /**
     * 获取指定租户的标签集合，包括共有的和自己私有的标签。
     * @return 标签集合
     */
    List<KTag> getMyTags(Tenant tenant);
}
