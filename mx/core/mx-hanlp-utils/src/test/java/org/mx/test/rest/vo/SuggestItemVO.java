package org.mx.test.rest.vo;

import org.mx.hanlp.ItemSuggester;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述： 推荐内容的值对象定义
 *
 * @author John.Peng
 *         Date time 2018/4/21 下午1:00
 */
public class SuggestItemVO {
    private String id, content;

    public SuggestItemVO() {
        super();
    }

    public SuggestItemVO(String id, String content) {
        this();
        this.id = id;
        this.content = content;
    }

    public static SuggestItemVO transform(ItemSuggester.SuggestItem item) {
        if (item == null) {
            return null;
        }
        return new SuggestItemVO(item.getId(), item.getContent());
    }

    public static List<SuggestItemVO> transform(List<ItemSuggester.SuggestItem> items) {
        List<SuggestItemVO> list = new ArrayList<>();
        if (items == null || items.isEmpty()) {
            return list;
        }
        for (ItemSuggester.SuggestItem item : items) {
            SuggestItemVO vo = SuggestItemVO.transform(item);
            if (vo != null) {
                list.add(vo);
            }
        }
        return list;
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
