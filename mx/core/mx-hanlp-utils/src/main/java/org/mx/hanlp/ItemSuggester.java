package org.mx.hanlp;

import org.springframework.util.Assert;

import java.util.List;

/**
 * 描述： 文本条目推荐操作接口
 *
 * @author John.Peng
 *         Date time 2018/4/16 下午4:40
 */
public interface ItemSuggester {
    void addSuggestItem(SuggestItem suggestItem);

    long getTotal();

    long reload();

    void close();

    String getType();

    List<SuggestItem> suggest(String keyword);

    List<SuggestItem> suggest(String keyword, int size);

    class SuggestItem {
        public static final String DEFAULT_TYPE = "default";
        private String type = DEFAULT_TYPE, id = null, content = null;

        private SuggestItem() {
            super();
        }

        private SuggestItem(String id, String content) {
            this();
            Assert.notNull(id, "The ID can not be null.");
            Assert.notNull(content, "The content can not be null.");
            this.id = id;
            this.content = content;
        }

        private SuggestItem(String type, String id, String content) {
            this(id, content);
            if (type != null) {
                this.type = type;
            }
        }

        public static SuggestItem valueOf(String id, String content) {
            return new SuggestItem(id, content);
        }

        public static SuggestItem valueOf(String type, String id, String content) {
            return new SuggestItem(type, id, content);
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
