package org.mx.rest.vo;

import org.mx.dal.entity.BaseDict;

/**
 * Created by john on 2017/10/8.
 */
public class BaseDictVO extends BaseVO {
    private String code, name, desc;

    public static void transform(BaseDict baseDict, BaseDictVO baseDictVO) {
        if (baseDict == null || baseDictVO == null) {
            return;
        }
        BaseVO.transform(baseDict, baseDictVO);
        baseDictVO.code = baseDict.getCode();
        baseDictVO.name = baseDict.getName();
        baseDictVO.desc = baseDict.getDesc();
    }

    public static void transfrom(BaseDictVO baseDictVO, BaseDict baseDict) {
        if (baseDict == null || baseDictVO == null) {
            return;
        }
        BaseVO.transform(baseDictVO, baseDict);
        baseDict.setCode(baseDictVO.getCode());
        baseDict.setName(baseDictVO.getName());
        baseDict.setDesc(baseDictVO.getDesc());
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }
}
