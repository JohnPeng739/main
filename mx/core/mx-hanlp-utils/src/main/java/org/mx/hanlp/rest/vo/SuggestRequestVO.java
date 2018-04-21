package org.mx.hanlp.rest.vo;

/**
 * 描述： 请求推荐的值对象定义
 *
 * @author John.Peng
 *         Date time 2018/4/21 下午12:59
 */
public class SuggestRequestVO {
    private String type, keyword;
    private int size = 1;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKeyword() {
        return keyword;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setKeywords(String keyword) {
        this.keyword = keyword;
    }
}
