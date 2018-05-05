package org.mx.hanlp.factory;

/**
 * 描述： 推荐其状态信息类定义
 *
 * @author john peng
 * Date time 2018/5/5 下午11:02
 */
public class SuggesterStat {
    private String suggesterType;
    private long lastStartTime = 0, lastFinishTime = 0, reloadTotal = 0, itemTotal = 0;

    public SuggesterStat() {
        super();
    }

    public SuggesterStat(String type) {
        this();
        this.suggesterType = type;
    }

    public String getSuggesterType() {
        return suggesterType;
    }

    public void setSuggesterType(String suggesterType) {
        this.suggesterType = suggesterType;
    }

    public long getLastStartTime() {
        return lastStartTime;
    }

    public void setLastStartTime(long lastStartTime) {
        this.lastStartTime = lastStartTime;
    }

    public long getLastFinishTime() {
        return lastFinishTime;
    }

    public void setLastFinishTime(long lastFinishTime) {
        this.lastFinishTime = lastFinishTime;
    }

    public long getReloadTotal() {
        return reloadTotal;
    }

    public void setReloadTotal(long reloadTotal) {
        this.reloadTotal = reloadTotal;
    }

    public long getItemTotal() {
        return itemTotal;
    }

    public void setItemTotal(long itemTotal) {
        this.itemTotal = itemTotal;
    }
}
