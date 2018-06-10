package com.hankcs.hanlp.suggest;

import com.hankcs.hanlp.suggest.scorer.BaseScorer;

import java.util.*;

/**
 * 描述： 基于分数过滤的推荐
 *
 * @author john peng
 * Date time 2018/6/10 下午6:32
 */
public class SuggesterByScore extends Suggester {
    /**
     * 将分数map排序折叠
     */
    private static TreeMap<Double, Set<String>> sortScoreMap(TreeMap<String, Double> scoreMap) {
        TreeMap<Double, Set<String>> result = new TreeMap<>(Collections.reverseOrder());
        for (Map.Entry<String, Double> entry : scoreMap.entrySet()) {
            result.computeIfAbsent(entry.getValue(), k -> new HashSet<>()).add(entry.getKey());
        }

        return result;
    }

    /**
     * 从map的值中找出最大值，这个值是从0开始的
     */
    private static Double max(Map<String, Double> map) {
        Double theMax = 0.0;
        for (Double v : map.values()) {
            theMax = Math.max(theMax, v);
        }

        return theMax;
    }

    /**
     * 返回带有分数的推荐数据
     *
     * @param key  推荐关键字
     * @param size 条目数
     * @return 带有推荐分数的列表
     */
    @SuppressWarnings("unchecked")
    public List<Hit> suggest2(String key, int size) {
        List<Hit> resultList = new ArrayList<>(size);
        TreeMap<String, Double> scoreMap = new TreeMap<>();
        for (BaseScorer scorer : scorerList) {
            Map<String, Double> map = scorer.computeScore(key);
            Double max = max(map);  // 用于正规化一个map
            for (Map.Entry<String, Double> entry : map.entrySet()) {
                Double score = scoreMap.get(entry.getKey());
                if (score == null) score = 0.0;
                scoreMap.put(entry.getKey(), score / max + entry.getValue() * scorer.boost);
            }
        }
        for (Map.Entry<Double, Set<String>> entry : sortScoreMap(scoreMap).entrySet()) {
            for (String sentence : entry.getValue()) {
                if (resultList.size() >= size) {
                    return resultList;
                }
                resultList.add(new Hit(entry.getKey(), sentence));
            }
        }

        return resultList;
    }

    /**
     * 带分数的推荐条目
     */
    public class Hit {
        private double score;
        private String sentence;

        /**
         * 默认的构造函数
         *
         * @param score    分数
         * @param sentence 推荐条目
         */
        private Hit(double score, String sentence) {
            super();
            this.score = score;
            this.sentence = sentence;
        }

        @Override
        public String toString() {
            return String.format("Hit{ score=%.10f, sentence='%s'}", score, sentence);
        }

        /**
         * 获取推荐分数
         *
         * @return 分数
         */
        public double getScore() {
            return score;
        }

        /**
         * 获取推荐内容
         *
         * @return 推荐内容
         */
        public String getSentence() {
            return sentence;
        }
    }
}
