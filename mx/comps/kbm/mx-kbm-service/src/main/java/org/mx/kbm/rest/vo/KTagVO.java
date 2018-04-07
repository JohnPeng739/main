package org.mx.kbm.rest.vo;

import org.mx.dal.EntityFactory;
import org.mx.kbm.entity.KTag;
import org.mx.kbm.entity.Tenant;
import org.mx.service.rest.vo.BaseVO;

/**
 * 描述： 知识标签VO对象定义
 *
 * @author John.Peng
 *         Date time 2018/3/27 上午11:14
 */
public class KTagVO extends BaseVO {
    private String tag = null, desc = null;
    private TenantVO tenant = null;

    public static KTagVO transform(KTag kTag) {
        if (kTag == null) {
            return null;
        }
        KTagVO vo = new KTagVO();
        BaseVO.transform(kTag, vo);
        vo.setTag(kTag.getTag());
        vo.setDesc(kTag.getDesc());
        if (kTag.getTenant() != null) {
            vo.setTenant(TenantVO.transform(kTag.getTenant()));
        }
        return vo;
    }

    public KTag transform() {
        KTag kTag = EntityFactory.createEntity(KTag.class);
        BaseVO.transform(this, kTag);
        kTag.setTag(this.tag);
        kTag.setDesc(this.desc);
        if (this.tenant != null) {
            Tenant tenant = this.tenant.transform();
            kTag.setTenant(tenant);
        }
        return kTag;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public TenantVO getTenant() {
        return tenant;
    }

    public void setTenant(TenantVO tenant) {
        this.tenant = tenant;
    }
}
