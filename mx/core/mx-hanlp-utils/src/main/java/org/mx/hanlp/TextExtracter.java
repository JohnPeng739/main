package org.mx.hanlp;

import java.util.List;

/**
 * 描述： 关键字提取接口定义
 *
 * @author John.Peng
 * Date time 2018/4/21 下午2:50
 * @deprecated 本接口及其相关实现可以被 {@link org.mx.hanlp.utils.HanlpUtils} 取代，未来版本可能会被删除。
 */
public interface TextExtracter {
    /**
     * 获取文字关键字，默认获取5个关键字
     *
     * @param content 正文
     * @return 关键字列表
     */
    List<String> keywords(String content);

    /**
     * 获取文字关键字
     *
     * @param content 正文
     * @param size    最大关键字个数
     * @return 关键字列表
     */
    List<String> keywords(String content, int size);

    /**
     * 获取文字摘要内容，摘要默认最大长度300字
     *
     * @param content 正文
     * @return 摘要
     */
    String summary(String content);

    /**
     * 获取文字摘要内容
     *
     * @param content   正文
     * @param maxLength 摘要最大长度
     * @return 摘要
     */
    String summary(String content, int maxLength);
}
