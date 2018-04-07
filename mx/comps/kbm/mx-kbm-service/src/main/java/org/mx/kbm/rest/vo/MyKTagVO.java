package org.mx.kbm.rest.vo;

import org.mx.kbm.entity.KTag;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述： 指定租户拥有的知识标签VO对象定义
 *
 * @author John.Peng
 *         Date time 2018/3/27 上午11:20
 */
public class MyKTagVO {
    private List<KTagVO> publicKTags, privateKTags;

    public MyKTagVO() {
        super();
        publicKTags = new ArrayList<>();
        privateKTags = new ArrayList<>();
    }

    public MyKTagVO(List<KTag> publicKTags, List<KTag> privateKTags) {
        this();
        this.publicKTags.clear();
        this.privateKTags.clear();
        if (publicKTags != null && !publicKTags.isEmpty()) {
            publicKTags.forEach(kTag -> this.publicKTags.add(KTagVO.transform(kTag)));
        }
        if (privateKTags != null && !privateKTags.isEmpty()) {
            privateKTags.forEach(kTag -> this.privateKTags.add(KTagVO.transform(kTag)));
        }
    }

    public List<KTagVO> getPublicKTags() {
        return publicKTags;
    }

    public void setPublicKTags(List<KTagVO> publicKTags) {
        this.publicKTags = publicKTags;
    }

    public List<KTagVO> getPrivateKTags() {
        return privateKTags;
    }

    public void setPrivateKTags(List<KTagVO> privateKTags) {
        this.privateKTags = privateKTags;
    }
}
