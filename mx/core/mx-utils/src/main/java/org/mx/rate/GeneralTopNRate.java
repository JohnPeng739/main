/* 
 * @(#)GeneralTopNRate.java
 *
 * Copyright 2016, 迪爱斯通信设备有限公司保留.
 */
package org.mx.rate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * 通用的速率计算类，可以支持对Integer、Long、Float、Double数据类型的速率计算。
 *
 * @author john peng
 */
public abstract class GeneralTopNRate<T> {

    /**
     * 静态变量：系统日志
     */
    private static final Log logger = LogFactory.getLog(GeneralTopNRate.class);

    private long lastCleanTime = 0, startTime = 0;

    private double scale = 1; // 每秒多少个单位，1表示每秒钟记录一个数字，最小位0.01，表示10ms一个数值。
    private int N = 300, size = (int) (N / scale); // 最多能够支持到多少秒的数字

    private T total = null;
    private Map<Long, T> topNNumbers = null;

    private ValueType valueType = ValueType.Integer;

    /**
     * 默认构造方法
     */
    public GeneralTopNRate() {
        this(300, 1);
    }

    /**
     * 默认构造方法
     *
     * @param N 记录元数据个数
     */
    public GeneralTopNRate(int N) {
        this(N, 1);
    }

    /**
     * 默认构造方法
     *
     * @param N     记录元数据个数
     * @param scale 比例系数,表示每秒可以有多少个值
     */
    public GeneralTopNRate(int N, double scale) {
        super();
        this.N = N;
        this.scale = scale;
        if (this.scale <= 0) {
            this.scale = 1;
        }
        if (this.scale < 0.01) {
            this.scale = 0.01;
        }
        this.size = (int) (this.N / this.scale);
        this.topNNumbers = new HashMap<>(this.size);
        this.startTime = System.currentTimeMillis();
        this.lastCleanTime = System.currentTimeMillis();

        Type gt = getClass().getGenericSuperclass();
        if (ParameterizedType.class.isAssignableFrom(gt.getClass())) {
            Type t = ((ParameterizedType) gt).getActualTypeArguments()[0];
            String typeName = t.getTypeName();
            if (Float.class.getName().equalsIgnoreCase(typeName)) {
                valueType = ValueType.Float;
            } else if (Double.class.getName().equalsIgnoreCase(typeName)) {
                valueType = ValueType.Double;
            } else if (Integer.class.getName().equalsIgnoreCase(typeName)) {
                valueType = ValueType.Integer;
            } else if (Long.class.getName().equalsIgnoreCase(typeName)) {
                valueType = ValueType.Long;
            } else {
                throw new UnsupportedOperationException(
                        "This operate only support Int | Long | Float | Double data types.");
            }
        }
    }

    /**
     * 清理队列中过期的数据
     *
     * @return 清理的时间，参照了scale比例。
     */
    private long doClean() {
        long time = System.currentTimeMillis();
        int right = N * 1000 + 300, left1 = (int) (time - lastCleanTime);
        if (left1 > right) {
            Long[] keys = topNNumbers.keySet().toArray(new Long[0]);
            for (long key : keys) {
                int left2 = (int) (time - key);
                if (left2 >= right) {
                    topNNumbers.remove(key);
                }
            }
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("doClean: removed=%d, totalSize=%d, current=%d, last=%d.", keys.length,
                        topNNumbers.size(), time, lastCleanTime));
            }
            lastCleanTime = time;
        }
        return time;
    }

    /**
     * 将两个泛型的值相加并返回
     *
     * @param t1 泛型值1
     * @param t2 泛型值2
     * @return 和
     */
    protected abstract T add(T t1, T t2);

    /**
     * 添加一个值，仅支持：Integer、Long、Float、Double数据类型
     *
     * @param t 值
     */
    public void addNumber(T t) {
        long time = doClean();
        total = add(total, t);
        T num = add(topNNumbers.get(time), t);
        topNNumbers.put(time, num);
    }

    public int getN() {
        return N;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public T getTotal() {
        return total;
    }

    /**
     * 获取迄今位置的平均值
     *
     * @return 平均值
     */
    public double getTotalRate() {
        return getTopNRate(-1);
    }

    /**
     * 获取前n个数字的平均值
     *
     * @param lastSeconds 前多少秒数
     * @return 平均值
     */
    public double getTopNRate(int lastSeconds) {
        long time = doClean();
        T num = null;
        long l = time - startTime;
        int last = l % 1000 == 0 ? (int) (l / 1000) : (int) (l / 1000) + 1;
        if (lastSeconds == -1 || lastSeconds > N) {
            num = total;
            lastSeconds = lastSeconds == -1 ? Integer.MAX_VALUE : lastSeconds;
        } else {
            int count = lastSeconds * 1000;
            for (Long key : topNNumbers.keySet()) {
                if (time - key <= count) {
                    T t = topNNumbers.get(key);
                    num = add(num, t);
                }
            }
        }
        lastSeconds = Math.min(lastSeconds, last);

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("num=%d, total=%d, time=%d, start=%d, last=%d - %d.", lastSeconds, num,
                    time, startTime, (time - startTime) / 1000, time - startTime));
        }
        return getRate(num, lastSeconds);
    }

    protected abstract double getRate(T num, int size);

    /**
     * 重置所有统计项
     */
    public void reset() {
        this.total = null;
        this.topNNumbers.clear();
        this.startTime = System.currentTimeMillis();
        this.lastCleanTime = System.currentTimeMillis();
    }

    public enum ValueType {
        Integer, Long, Float, Double
    }
}
