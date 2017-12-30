/* 
 * @(#)IntegerTopNRate.java
 *
 * Copyright 2016, 迪爱斯通信设备有限公司保留.
 */
package org.mx.rate;

/**
 * 面向Integer数值的速率计算类
 *
 * @author john peng
 */
public class IntegerTopNRate extends GeneralTopNRate<Integer> {

    public IntegerTopNRate() {
        super();
    }

    public IntegerTopNRate(int N) {
        super(N);
    }

    public IntegerTopNRate(int N, double scale) {
        super(N, scale);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralTopNRate#add(Object, Object)
     */
    @Override
    protected Integer add(Integer t1, Integer t2) {
        if (t1 == null) {
            t1 = 0;
        }
        if (t2 == null) {
            t2 = 0;
        }
        return t1 + t2;
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralTopNRate#getRate(Object, int)
     */
    @Override
    protected double getRate(Integer num, int size) {
        if (num == null) {
            return 0;
        }
        if (size <= 0) {
            size = 1;
        }
        return (double)(num / size);
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralTopNRate#getTotal()
     */
    @Override
    public Integer getTotal() {
        Integer total = super.getTotal();
        return total == null ? 0 : total;
    }

}
