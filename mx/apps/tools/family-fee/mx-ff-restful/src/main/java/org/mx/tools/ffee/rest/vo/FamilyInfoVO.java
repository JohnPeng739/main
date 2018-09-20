package org.mx.tools.ffee.rest.vo;

import org.mx.dal.EntityFactory;
import org.mx.tools.ffee.dal.entity.Family;

public class FamilyInfoVO extends BaseParamsVO {
    private String id, name, desc, avatarUrl;

    public Family get() {
        Family family = EntityFactory.createEntity(Family.class);
        family.setId(id);
        family.setName(name);
        family.setDesc(desc);
        family.setAvatarUrl(avatarUrl);
        return family;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
