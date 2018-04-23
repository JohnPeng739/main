package org.mx.hanlp.factory.suggest;

import org.mx.hanlp.ItemSuggester;
import org.springframework.core.env.Environment;

/**
 * 描述： 推荐初始内容提供器接口定义
 *
 * @author John.Peng
 *         Date time 2018/4/16 下午5:46
 */
public interface SuggestContentProvider {
    /**
     * 从Environment中获取指定的配置信息，便于初始化Provider。
     *
     * @param env    上下午环境
     * @param prefix 配置项的前缀
     */
    void initEnvironment(Environment env, String prefix);

    /**
     * 将配置的建议内容加载到指定的条目推荐器中
     *
     * @param itemSuggester 指定的条目推荐器
     * @return 加载条目数量
     */
    long loadSuggestContent(ItemSuggester itemSuggester);
}
