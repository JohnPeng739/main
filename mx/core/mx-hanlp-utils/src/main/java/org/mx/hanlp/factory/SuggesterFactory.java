package org.mx.hanlp.factory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.error.UserInterfaceSystemErrorException;
import org.mx.hanlp.ItemSuggester;
import org.mx.hanlp.factory.suggest.SuggestContentProvider;
import org.mx.hanlp.impl.ItemSuggesterImpl;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 描述： 推荐工厂
 *
 * @author John.Peng
 *         Date time 2018/4/16 下午5:25
 */
@Component
public class SuggesterFactory implements InitializingBean, DisposableBean {
    private static final Log logger = LogFactory.getLog(SuggesterFactory.class);

    private Map<String, ItemSuggester> suggesters = null;

    @Autowired
    private Environment env = null;

    /**
     * 默认的构造函数
     */
    public SuggesterFactory() {
        super();
        this.suggesters = new HashMap<>();
    }

    /**
     * 获取系统中存在的默认推荐器，其类型为<pre>{@link ItemSuggester.SuggestItem#DEFAULT_TYPE}</pre>。
     *
     * @return 推荐器，如果不存在，则返回null。
     * @see ItemSuggester.SuggestItem#DEFAULT_TYPE
     */
    public ItemSuggester getSuggester() {
        return getSuggester(ItemSuggester.SuggestItem.DEFAULT_TYPE);
    }

    /**
     * 获取系统中存在的推荐器
     *
     * @param type 指定的推荐器类型
     * @return 推荐器，如果不存在，则返回null。
     */
    public ItemSuggester getSuggester(String type) {
        if (suggesters.containsKey(type)) {
            return suggesters.get(type);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @see DisposableBean#destroy()
     */
    @Override
    public void destroy() throws Exception {
        if (suggesters != null && !suggesters.isEmpty()) {
            for (ItemSuggester suggester : suggesters.values()) {
                suggester.close();
            }
            suggesters.clear();
            suggesters = null;
        }
        if (logger.isInfoEnabled()) {
            logger.info("Destroy the suggester factory successfully.");
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        int num = env.getProperty("suggester.num", Integer.class, 0);
        if (num <= 0) {
            if (logger.isWarnEnabled()) {
                logger.warn("There not config any suggester.");
            }
            return;
        }
        int total = 0;
        for (int index = 1; index <= num; index++) {
            String type = env.getProperty(String.format("suggester.%d.type", index)),
                    clazzName = env.getProperty(String.format("suggester.%d.provider", index));
            if (StringUtils.isBlank(type) || StringUtils.isBlank(clazzName)) {
                if (logger.isWarnEnabled()) {
                    logger.warn("The type or class's name maybe blank for the suggester.");
                }
                continue;
            }
            SuggestContentProvider provider = (SuggestContentProvider) Class.forName(clazzName).newInstance();
            provider.initEnvironment(env, String.format("suggester.%d.config", index));
            ItemSuggester itemSuggester = new ItemSuggesterImpl(type);
            provider.loadSuggestContent(itemSuggester);
            suggesters.put(type, itemSuggester);
            total++;
        }
        if (logger.isInfoEnabled()) {
            logger.info(String.format("Initialize %d suggester successfully.", total));
        }
    }
}
