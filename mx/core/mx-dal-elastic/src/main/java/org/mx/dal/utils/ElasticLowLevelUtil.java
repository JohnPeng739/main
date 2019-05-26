package org.mx.dal.utils;

import com.alibaba.fastjson.JSONObject;
import org.mx.dal.entity.Base;
import org.mx.dal.utils.bean.IndicesInfoBean;
import org.mx.dal.utils.bean.NodeInfoBean;

import java.util.List;
import java.util.Map;

/**
 * 描述： ES Util的低层次实现，采用非实体对象方式实现
 *
 * @author john peng
 * Date time 2018/8/31 下午7:53
 */
public interface ElasticLowLevelUtil {

    /**
     * 获取所有的集群节点信息
     *
     * @return 节点信息列表
     */
    List<NodeInfoBean> getAllNodes();

    /**
     * 获取所有索引信息
     *
     * @return 索引信息列表
     */
    List<IndicesInfoBean> getAllIndexes();

    JSONObject getById(String id, String index);

    /**
     * 创建一个ES索引
     *
     * @param index      索引名称
     * @param settings   索引特定的settings配置
     * @param properties 索引特定的属性配置
     * @param <T>        泛型定义
     */
    <T extends Base> void createIndex(String index, Map<String, Object> settings, Map<String, Object> properties);

    /**
     * 删除指定的ES索引
     *
     * @param index 索引名字
     */
    void deleteIndex(String index);

    /**
     * 删除指定的ID的文档索引
     *
     * @param id    文档的ID
     * @param index 指定的索引
     */
    void deleteIndex(String id, String index);
}
