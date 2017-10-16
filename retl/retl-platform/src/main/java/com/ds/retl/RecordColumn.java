package com.ds.retl;

/**
 * 数据列定义，中文描述用于后续处理过程中的信息提示。
 *
 * @author : john.peng created on date : 2017/19/7
 */
public class RecordColumn {
    private String name, desc;

    /**
     * 获取数据列名称
     *
     * @return 名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置数据列名称
     *
     * @param name 名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取数据列描述
     *
     * @return 描述
     */
    public String getDesc() {
        return desc;
    }

    /**
     * 设置数据列描述
     *
     * @param desc 描述
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }
}
