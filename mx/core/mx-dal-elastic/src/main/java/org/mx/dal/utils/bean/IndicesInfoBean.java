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
    private String health, status, index, uuid;
    private int pri, rep;
    private long docsCount, docDeleted, storeSize, priStoreSize;

    public IndicesInfoBean() {
        super();
    }

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

    public String getHealth() {
        return health;
    }

    public String getStatus() {
        return status;
    }

    public String getIndex() {
        return index;
    }

    public String getUuid() {
        return uuid;
    }

    public int getPri() {
        return pri;
    }

    public int getRep() {
        return rep;
    }

    public long getDocsCount() {
        return docsCount;
    }

    public long getDocDeleted() {
        return docDeleted;
    }

    public long getStoreSize() {
        return storeSize;
    }

    public long getPriStoreSize() {
        return priStoreSize;
    }
}
