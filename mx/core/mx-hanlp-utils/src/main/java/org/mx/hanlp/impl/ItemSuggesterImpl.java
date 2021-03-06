package org.mx.hanlp.impl;

import com.alibaba.fastjson.JSON;
import com.hankcs.hanlp.suggest.SuggesterByScore;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.DigestUtils;
import org.mx.hanlp.ItemSuggester;
import org.mx.hanlp.error.UserInterfaceHanlpErrorException;
import org.mx.hanlp.factory.suggest.SuggestContentProvider;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述： 基于HanLP实现的条目推荐
 *
 * @author John.Peng
 * Date time 2018/4/16 下午4:49
 */
public class ItemSuggesterImpl implements ItemSuggester {
    private static final Log logger = LogFactory.getLog(ItemSuggester.class);

    private String name = SuggestItem.DEFAULT_NAME;
    // fingerprint: id
    private Map<String, String> fingerprints = null;

    private SuggesterByScore suggester = null;
    private SuggestContentProvider provider = null;

    /**
     * 默认的构造函数
     */
    public ItemSuggesterImpl() {
        super();
        this.fingerprints = new HashMap<>();
        this.suggester = new SuggesterByScore();
    }

    /**
     * 默认的构造函数
     *
     * @param name     推荐器名称
     * @param provider 推荐内容提供器
     */
    public ItemSuggesterImpl(String name, SuggestContentProvider provider) {
        this();
        this.name = name;
        this.provider = provider;
    }

    /**
     * {@inheritDoc}
     *
     * @see ItemSuggester#addSuggestItem(SuggestItem)
     */
    @Override
    public void addSuggestItem(SuggestItem suggestItem) {
        Assert.notNull(suggestItem, "The suggest item can not be null.");
        String name = suggestItem.getName(), id = suggestItem.getId(), content = suggestItem.getContent();
        if (!this.name.equalsIgnoreCase(name)) {
            throw new UserInterfaceHanlpErrorException(
                    UserInterfaceHanlpErrorException.HanlpErrors.SUGGESTER_UNMATCH);
        }
        String fingerprint = DigestUtils.md5(content);
        fingerprints.put(fingerprint, id);
        suggester.addSentence(content);
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Add a suggest item successfully, \n %s.", JSON.toJSONString(suggestItem)));
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see ItemSuggester#clear()
     */
    @Override
    public void clear() {
        if (suggester != null) {
            suggester.removeAllSentences();
        }
        if (fingerprints != null) {
            fingerprints.clear();
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see ItemSuggester#getTotal()
     */
    @Override
    public long getTotal() {
        return fingerprints.size();
    }

    /**
     * {@inheritDoc}
     *
     * @see ItemSuggester#reload()
     */
    @Override
    public long reload() {
        long total = 0;
        if (provider != null) {
            total = provider.loadSuggestContent(this);
        }
        return total;
    }

    /**
     * {@inheritDoc}
     *
     * @see ItemSuggester#close()
     */
    @Override
    public void close() {
        if (suggester != null) {
            suggester.removeAllSentences();
            suggester = null;
        }
        fingerprints.clear();
        fingerprints = null;
    }

    /**
     * {@inheritDoc}
     *
     * @see ItemSuggester#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     *
     * @see ItemSuggester#suggest(String)
     */
    @Override
    public List<SuggestItem> suggest(String keyword) {
        return suggest(keyword, 1);
    }

    /**
     * {@inheritDoc}
     *
     * @see ItemSuggester#suggest(String, int)
     */
    @Override
    public List<SuggestItem> suggest(String keyword, int size) {
        List<SuggestItem> list = new ArrayList<>();
        List<SuggesterByScore.Hit> hits = suggester.suggest2(keyword, size);
        for (SuggesterByScore.Hit hit : hits) {
            String id = fingerprints.get(DigestUtils.md5(hit.getSentence()));
            if (id == null) {
                if (logger.isWarnEnabled()) {
                    logger.warn(String.format("The suggest item[content: %s] not found, " +
                            "the cache is out of sync.", hit.getSentence()));
                }
            } else {
                list.add(SuggestItem.valueOf(name, id, hit.getSentence(), hit.getScore()));
            }
        }
        return list;
    }
}
