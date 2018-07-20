package org.mx.hanlp;

import org.springframework.util.Assert;

import java.util.List;

/**
 * 描述： 文本条目推荐操作接口
 *
 * @author John.Peng
 * Date time 2018/4/16 下午4:40
 */
public interface ItemSuggester {
    /**
     * 向推荐器中添加一条推荐条目
     *
     * @param suggestItem 推荐条目
     */
    void addSuggestItem(SuggestItem suggestItem);

    /**
     * 清除推荐器中的所有条目
     */
    void clear();

    /**
     * 获取推荐器中条目总数
     *
     * @return 条目总数
     */
    long getTotal();

    /**
     * 重新刷新并加载推荐条目，不会清除原来的题目，只会替换
     *
     * @return 本次加载条目数量
     */
    long reload();

    /**
     * 关闭推荐器
     */
    void close();

    /**
     * 获取推荐器名称
     *
     * @return 推荐器名称
     */
    String getName();

    /**
     * 根据关键字获取推荐条目，也就是所谓的"推荐"，默认返回1条推荐。
     *
     * @param keyword 关键字
     * @return 推荐的条目列表
     */
    List<SuggestItem> suggest(String keyword);

    /**
     * 根据关键字获取推荐条目
     *
     * @param keyword 关键字
     * @param size    推荐最多条目数
     * @return 推荐的条目列表
     */
    List<SuggestItem> suggest(String keyword, int size);

    /**
     * 推荐条目实体类
     */
    class SuggestItem {
        public static final String DEFAULT_NAME = "default";
        private String name = DEFAULT_NAME, id = null, content = null;
        private double score = 0.0f;

        /**
         * 默认的构造函数
         */
        private SuggestItem() {
            super();
        }

        /**
         * 默认的构造函数
         *
         * @param id      ID
         * @param content 推荐内容
         */
        private SuggestItem(String id, String content) {
            this();
            Assert.notNull(id, "The ID can not be null.");
            Assert.notNull(content, "The content can not be null.");
            this.id = id;
            this.content = content;
        }

        /**
         * 默认的构造函数
         *
         * @param name    推荐器名称
         * @param id      ID
         * @param content 推荐内容
         */
        private SuggestItem(String name, String id, String content) {
            this(id, content);
            if (name != null) {
                this.name = name;
            }
        }

        /**
         * 默认的构造函数
         *
         * @param name    推荐器名称
         * @param id      ID
         * @param content 推荐内容
         * @param score   推荐分数
         */
        private SuggestItem(String name, String id, String content, double score) {
            this(id, content);
            if (name != null) {
                this.name = name;
            }
            this.score = score;
        }


        /**
         * 根据指定参数构造一个推荐条目对象，使用默认的推荐器类型
         *
         * @param id      ID
         * @param content 推荐内容
         * @return 推荐条目对象
         * @see #valueOf(String, String, String)
         * @see #DEFAULT_NAME
         */
        public static SuggestItem valueOf(String id, String content) {
            return new SuggestItem(id, content);
        }

        /**
         * 根据指定参数构造一个推荐条目对象
         *
         * @param name    推荐器名称
         * @param id      ID
         * @param content 推荐内容
         * @return 推荐条目对象
         */
        public static SuggestItem valueOf(String name, String id, String content) {
            return new SuggestItem(name, id, content);
        }

        /**
         * 根据指定参数构造一个推荐条目对象
         *
         * @param name    推荐器名称
         * @param id      ID
         * @param content 推荐内容
         * @param score   推荐分数
         * @return 推荐条目对象
         */
        public static SuggestItem valueOf(String name, String id, String content, double score) {
            return new SuggestItem(name, id, content, score);
        }

        /**
         * 获取推荐器名称
         *
         * @return 推荐器名称
         */
        public String getName() {
            return name;
        }

        /**
         * 设置推荐器名称
         *
         * @param name 推荐器名称
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * 获取ID
         *
         * @return ID
         */
        public String getId() {
            return id;
        }

        /**
         * 设置ID
         *
         * @param id ID
         */
        public void setId(String id) {
            this.id = id;
        }

        /**
         * 获取推荐内容
         *
         * @return 推荐内容
         */
        public String getContent() {
            return content;
        }

        /**
         * 设置推荐内容
         *
         * @param content 推荐内容
         */
        public void setContent(String content) {
            this.content = content;
        }

        /**
         * 获取推荐分数
         *
         * @return 分数
         */
        public double getScore() {
            return score;
        }
    }
}
