package org.mx.hanlp.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.hanlp.TextExtracter;
import org.mx.hanlp.utils.HanlpUtils;

import java.util.List;

/**
 * 描述： 基于HanLP的关键字提取器实现
 *
 * @author John.Peng
 * Date time 2018/4/21 下午2:52
 * @deprecated 本接口及其相关实现可以被 {@link org.mx.hanlp.utils.HanlpUtils} 取代，未来版本可能会被删除。
 */
public class TextExtracterImpl implements TextExtracter {
    private static final Log logger = LogFactory.getLog(TextExtracterImpl.class);

    /**
     * {@inheritDoc}
     *
     * @see TextExtracter#keywords(String)
     */
    @Override
    public List<String> keywords(String content) {
        return keywords(content, 5);
    }

    /**
     * {@inheritDoc}
     *
     * @see TextExtracter#keywords(String, int)
     */
    @Override
    public List<String> keywords(String content, int size) {
        return HanlpUtils.keywords(content, size);
    }

    /**
     * {@inheritDoc}
     *
     * @see TextExtracter#summary(String)
     */
    @Override
    public String summary(String content) {
        return summary(content, 300);
    }

    /**
     * {@inheritDoc}
     *
     * @see TextExtracter#summary(String, int)
     */
    @Override
    public String summary(String content, int maxLength) {
        return HanlpUtils.summary(content, maxLength);
    }
}
