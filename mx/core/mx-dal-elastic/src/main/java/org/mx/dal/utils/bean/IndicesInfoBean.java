package org.mx.dal.utils.bean;

import org.mx.StringUtils;
import org.mx.TypeUtils;

/**
 * 描述： ES中Indice的相关信息类定义
 *
 * @author john peng
 * Date time 2018/8/30 下午9:40
 */
public class IndicesInfoBean {
    private String health, status, index, uuid; // 健康度、状态、索引名、UUID
    private int pri, rep; // 主用数、复制数
    private long docsCount, docDeleted, storeSize, priStoreSize; // 文档数、删除文档数、存储大小、主用存储大小

    /**
     * 默认的构造函数
     */
    public IndicesInfoBean() {
        super();
    }

    /**
     * 默认的构造函数
     *
     * @param line 文本数据
     */
    public IndicesInfoBean(String line) {
        this();
        if (!StringUtils.isBlank(line)) {
            String[] segs = line.split(" ");
            health = segs[0];
            status = segs[1];
            index = segs[2];
            uuid = segs[3];
            pri = TypeUtils.string2Int(segs[4], TypeUtils.Radix.Decimal, 0);
            rep = TypeUtils.string2Int(segs[5], TypeUtils.Radix.Decimal, 0);
            docsCount = TypeUtils.string2Long(segs[6], TypeUtils.Radix.Decimal, 0);
            docDeleted = TypeUtils.string2Long(segs[7], TypeUtils.Radix.Decimal, 0);
            storeSize = TypeUtils.string2Size(segs[8], 0);
            priStoreSize = TypeUtils.string2Size(segs[9], 0);
        }
    }

    /**
     * 获取健康度
     *
     * @return 健康度
     */
    public String getHealth() {
        return health;
    }

    /**
     * 获取状态
     *
     * @return 状态
     */
    public String getStatus() {
        return status;
    }

    /**
     * 获取索引名
     *
     * @return 索引名
     */
    public String getIndex() {
        return index;
    }

    /**
     * 获取UUID
     *
     * @return UUID
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * 获取主用数
     *
     * @return 主用数
     */
    public int getPri() {
        return pri;
    }

    /**
     * 获取复制数
     *
     * @return 复制数
     */
    public int getRep() {
        return rep;
    }

    /**
     * 获取文档数量
     *
     * @return 文档数量
     */
    public long getDocsCount() {
        return docsCount;
    }

    /**
     * 获取删除文档数量
     *
     * @return 删除文档数量
     */
    public long getDocDeleted() {
        return docDeleted;
    }

    /**
     * 获取存储大小
     *
     * @return 存储大小
     */
    public long getStoreSize() {
        return storeSize;
    }

    /**
     * 获取主用存储大小
     *
     * @return 主用存储大小
     */
    public long getPriStoreSize() {
        return priStoreSize;
    }
}
