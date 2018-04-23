package org.mx.hanlp.impl;

import com.alibaba.fastjson.JSON;
import com.hankcs.hanlp.suggest.Suggester;
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
 *         Date time 2018/4/16 下午4:49
 */
public class ItemSuggesterImpl implements ItemSuggester {
    private static final Log logger = LogFactory.getLog(ItemSuggester.class);

    private String type = SuggestItem.DEFAULT_TYPE;
    // fingerprint: id
    private Map<String, String> fingerprints = null;

    private Suggester suggester = null;
    private SuggestContentProvider provider = null;

    /**
     * 默认的构造函数
     */
    public ItemSuggesterImpl() {
        super();
        this.fingerprints = new HashMap<>();
        this.suggester = new Suggester();
    }

    /**
     * 默认的构造函数
     *
     * @param type     推荐器类型
     * @param provider 推荐内容提供器
     */
    public ItemSuggesterImpl(String type, SuggestContentProvider provider) {
        this();
        this.type = type;
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
        String type = suggestItem.getType(), id = suggestItem.getId(), content = suggestItem.getContent();
        if (!this.type.equalsIgnoreCase(type)) {
            throw new UserInterfaceHanlpErrorException(
                    UserInterfaceHanlpErrorException.HanlpErrors.SUGGESTER_TYPE_UNMATCH);
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
     * @see ItemSuggester#getType()
     */
    @Override
    public String getType() {
        return type;
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
        List<String> contents = suggester.suggest(keyword, size);
        for (String content : contents) {
            String id = fingerprints.get(DigestUtils.md5(content));
            if (id == null) {
                if (logger.isWarnEnabled()) {
                    logger.warn(String.format("The suggest item[id: %s, content: %s] not found, " +
                            "the cache is out of sync.", id, content));
                }
            } else {
                list.add(SuggestItem.valueOf(type, id, content));
            }
        }
        return list;
    }
}
