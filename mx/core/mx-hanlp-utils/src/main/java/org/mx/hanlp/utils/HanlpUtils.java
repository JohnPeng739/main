package org.mx.hanlp.utils;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.py.Pinyin;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 描述： HanLP常用工具方法
 *
 * @author john peng
 * Date time 2018/6/30 下午4:02
 */
public class HanlpUtils {
    private static final Log logger = LogFactory.getLog(HanlpUtils.class);

    /**
     * 获取文字关键字
     *
     * @param content 正文
     * @param size    最大关键字个数
     * @return 关键字列表
     */
    public static List<String> keywords(String content, int size) {
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
     * 获取指定中文字串的拼音信息
     *
     * @param content 中文字串
     * @return 拼音信息列表
     */
    public static List<Pinyin> pinyin(String content) {
        if (StringUtils.isBlank(content)) {
            if (logger.isWarnEnabled()) {
                logger.warn("The content is blank.");
            }
            return null;
        }
        if (!StringUtils.isChinese(content)) {
            if (logger.isWarnEnabled()) {
                logger.warn(String.format("The string[%s] is not chinese.", content));
            }
            return null;
        }
        return HanLP.convertToPinyinList(content);
    }

    /**
     * 将简体中文字串转化为繁体中文字串
     *
     * @param content 简体中文字串
     * @return 繁体中文字串
     */
    public static String s2t(String content) {
        if (StringUtils.isBlank(content)) {
            if (logger.isWarnEnabled()) {
                logger.warn("The content is blank.");
            }
            return "";
        }
        return HanLP.s2t(content);
    }

    /**
     * 获取文字摘要内容
     *
     * @param content   正文
     * @param maxLength 摘要最大长度
     * @return 摘要
     */
    public static String summary(String content, int maxLength) {
        if (StringUtils.isBlank(content)) {
            if (logger.isWarnEnabled()) {
                logger.warn("The content is blank.");
            }
            return null;
        }
        return HanLP.getSummary(content, maxLength);
    }

    /**
     * 将繁体中文字串转化为简体中文字串
     *
     * @param content 繁体中文字串
     * @return 简体中文字串
     */
    public static String t2s(String content) {
        if (StringUtils.isBlank(content)) {
            if (logger.isWarnEnabled()) {
                logger.warn("The content is blank.");
            }
            return "";
        }
        return HanLP.t2s(content);
    }

    /**
     * 获取指定字串的音头
     *
     * @param content 中文字串
     * @return 音头字串
     */
    public static String yinTou(String content) {
        List<Pinyin> list = pinyin(content);
        if (list != null && !list.isEmpty()) {
            StringBuffer sb = new StringBuffer();
            for (Pinyin pinyin : list) {
                sb.append(pinyin.getShengmu());
            }
            return sb.toString();
        } else {
            return "";
        }
    }
}
