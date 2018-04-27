package org.mx.kbm.entity;

import org.mx.comps.rbac.dal.entity.Account;
import org.mx.dal.entity.Base;

import java.util.Set;

/**
 * 描述： 知识条目接口定义，定义了知识的描述信息信息，一个知识点就是一条记录。
 *
 * @author John.Peng
 *         Date time 2018/3/24 下午6:14
 */
public interface KnowledgeNode extends Base {
    /**
     * 获取附件类型
     *
     * @return 附件类型
     * @see AttachmentType
     */
    AttachmentType getType();

    /**
     * 设置附件类型
     *
     * @param type 附件类型
     * @see AttachmentType
     */
    void setType(AttachmentType type);

    /**
     * 获取附件长度
     *
     * @return 附件长度
     */
    long getLength();

    /**
     * 设置附件长度
     *
     * @param length 附件长度
     */
    void setLength(long length);

    /**
     * 获取附件访问的URI
     *
     * @return 附件URI
     */
    String getUri();

    /**
     * 设置附件访问的URI
     *
     * @param uri 附件URI
     */
    void setUri(String uri);

    /**
     * 获取附件的最近一个版本号
     *
     * @return 版本号
     */
    String getLastVersion();

    /**
     * 设置附件的最近一个版本号
     *
     * @param version 版本号
     */
    void setLastVersion(String version);

    /**
     * 获取抽取的关键字列表
     *
     * @return 关键字列表
     */
    String getKeywords();

    /**
     * 设置抽取的关键字列表
     *
     * @param keywords 关键字列表
     */
    void setKeywords(String keywords);

    /**
     * 获取摘要
     *
     * @return 摘要
     */
    String getSummary();

    /**
     * 设置摘要
     *
     * @param summary 摘要
     */
    void setSummary(String summary);

    /**
     * 获取租户
     *
     * @return 租户
     */
    KnowledgeTenant getTenant();

    /**
     * 设置租户
     *
     * @param knowledgeTenant 租户
     */
    void setTenant(KnowledgeTenant knowledgeTenant);

    /**
     * 获取知识分类对象
     *
     * @return 分类对象
     */
    KnowledgeCategory getCategory();

    /**
     * 设置知识分类对象
     *
     * @param knowledgeCategory 分类对象
     */
    void setCategory(KnowledgeCategory knowledgeCategory);

    /**
     * 获取知识的拥有者
     *
     * @return 知识拥有者
     */
    Account getOwner();

    /**
     * 设置知识拥有者
     *
     * @param owner 知识拥有者
     */
    void setOwner(Account owner);

    /**
     * 获取知识关联的知识列表
     *
     * @return 知识列表
     */
    Set<KnowledgeNode> getReferences();

    /**
     * 设置知识关联的知识列表
     *
     * @param references 知识列表
     */
    void setReferences(Set<KnowledgeNode> references);

    /**
     * 附件类型枚举定义
     */
    enum AttachmentType {
        /**
         * Mark down类型文档
         */
        MD,
        /**
         * 文本文件类型文档
         */
        TXT,
        /**
         * JSON类型文档
         */
        JSON,
        /**
         * HTML类型文档
         */
        HTML,
        /**
         * Word类型文档
         */
        DOC,
        /**
         * Excel类型文档
         */
        XLS,
        /**
         * Power Point类型文档
         */
        PPT,
        /**
         * PDF类型文档
         */
        PDF
    }
}
