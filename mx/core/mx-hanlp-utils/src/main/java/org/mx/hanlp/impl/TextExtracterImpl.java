package org.mx.hanlp.impl;

import com.hankcs.hanlp.HanLP;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.hanlp.TextExtracter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 描述： 基于HanLP的关键字提取器实现
 *
 * @author John.Peng
 *         Date time 2018/4/21 下午2:52
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
        if (StringUtils.isBlank(content)) {
            if (logger.isWarnEnabled()) {
                logger.warn("The content is blank.");
            }
            return null;
        }
        List<String> list = HanLP.extractKeyword(content, size);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.stream().filter(keyword -> keyword.length() >= 2).collect(Collectors.toList());
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
        if (StringUtils.isBlank(content)) {
            if (logger.isWarnEnabled()) {
                logger.warn("The content is blank.");
            }
            return null;
        }
        return HanLP.getSummary(content, maxLength);
    }
}
