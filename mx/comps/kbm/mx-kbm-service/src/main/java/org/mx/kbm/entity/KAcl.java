package org.mx.kbm.entity;

/**
 * 描述： 知识访问控制枚举类型定义，如：读、写、分享、删除等。
 *
 * @author John.Peng
 *         Date time 2018/3/24 下午6:57
 */
public enum KAcl {
    /**
     * 读
     */
    READ(),
    /**
     * 写
     */
    WRITE(2, "WRITE"),
    /**
     * 删除
     */
    DELETE(4, "DELETE"),
    /**
     * 分享
     */
    SHARE(8, "SHARE");

    private int ordinal = 1;
    private String code = "READ";

    KAcl() {
    }

    KAcl(int state, String code) {
        this.ordinal = state;
        this.code = code;
    }

    public int getOrdinal() {
        return this.ordinal;
    }

    public String getCode() {
        return code;
    }
}
