package org.mx.kbm.entity;

import org.mx.dal.entity.Base;

/**
 * 描述： 知识附件接口定义，定义了知识附件（一般是文件）的描述性信息。
 *
 * @author John.Peng
 *         Date time 2018/3/24 下午6:56
 */
public interface KAttachment extends Base {
    /**
     * 获取知识条目
     *
     * @return 知识条目
     * @see KNode
     */
    KNode getKNode();

    /**
     * 设置知识条目
     *
     * @param kNode 知识条目
     * @see KNode
     */
    void setKNode(KNode kNode);

    /**
     * 获取类型
     *
     * @return 类型
     * @see AttachmentType
     */
    AttachmentType getType();

    /**
     * 设置类型
     *
     * @param type 类型
     * @see AttachmentType
     */
    void setType(AttachmentType type);

    /**
     * 获取URL
     *
     * @return URL
     */
    String getUrl();

    /**
     * 设置URL
     *
     * @param url URL
     */
    void setUrl(String url);

    /**
     * 获取名称
     *
     * @return 名称
     */
    String getName();

    /**
     * 设置名称
     *
     * @param name 名称
     */
    void setName(String name);

    /**
     * 获取描述
     *
     * @return 描述
     */
    String getDesc();

    /**
     * 设置描述
     *
     * @param desc 描述
     */
    void setDesc(String desc);

    /**
     * 获取租户
     *
     * @return 租户
     */
    Tenant getTenant();

    /**
     * 设置租户
     *
     * @param tenant 租户
     */
    void setTenant(Tenant tenant);

    /**
     * 附件文件类型枚举定义
     */
    enum AttachmentType {
        /**
         * 文本文件
         */
        TXT,
        /**
         * Word文件
         */
        DOC,
        /**
         * Excel文件
         */
        XLS,
        /**
         * Power Point文件
         */
        PPT,
        /**
         * PDF文件
         */
        PDF,
        /**
         * 图片文件
         */
        PIC,
        /**
         * 音频文件
         */
        AUDIO,
        /**
         * 视频文件
         */
        VIDEO,
        /**
         * 其他文件
         */
        OTHER
    }
}
