package org.mx.rest.vo;

import org.mx.dal.entity.BaseDict;

/**
 * 基础的字典数据值对象
 *
 * @author : john.peng date : 2017/10/8
 * @see BaseVO
 */
public class BaseDictVO extends BaseVO {
    private String code, name, desc;

    /**
     * 将字典对象转换为值对象
     *
     * @param baseDict   字典对象
     * @param baseDictVO 值对象
     */
    public static void transform(BaseDict baseDict, BaseDictVO baseDictVO) {
        if (baseDict == null || baseDictVO == null) {
            return;
        }
        BaseVO.transform(baseDict, baseDictVO);
        baseDictVO.code = baseDict.getCode();
        baseDictVO.name = baseDict.getName();
        baseDictVO.desc = baseDict.getDesc();
    }

    /**
     * 将值对象转换为字典对象
     *
     * @param baseDictVO 值对象
     * @param baseDict   字典对象
     */
    public static void transfrom(BaseDictVO baseDictVO, BaseDict baseDict) {
        if (baseDict == null || baseDictVO == null) {
            return;
        }
        BaseVO.transform(baseDictVO, baseDict);
        baseDict.setCode(baseDictVO.getCode());
        baseDict.setName(baseDictVO.getName());
        baseDict.setDesc(baseDictVO.getDesc());
    }

    /**
     * 获取代码
     *
     * @return 代码
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置代码
     *
     * @param code 代码
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 获取名称
     *
     * @return 名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置名称
     *
     * @param name 名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取描述
     *
     * @return 描述
     */
    public String getDesc() {
        return desc;
    }

    /**
     * 设置描述
     *
     * @param desc 描述
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }
}
