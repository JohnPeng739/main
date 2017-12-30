/* 
 * @(#)IntegerTopNRate.java
 *
 * Copyright 2016, 迪爱斯通信设备有限公司保留.
 */
package org.mx.rate;

/**
 * 面向Double数值的速率计算类
 *
 * @author john peng
 */
public class DoubleTopNRate extends GeneralTopNRate<Double> {

    public DoubleTopNRate() {
        super();
    }

    public DoubleTopNRate(int N) {
        super(N);
    }

    public DoubleTopNRate(int N, double scale) {
        super(N, scale);
    }

    /**
     * {@inheritDoc}
     * @see GeneralTopNRate#add(Object, Object)
     */
    @Override
    protected Double add(Double t1, Double t2) {
        if (t1 == null) {
            t1 = 0.0;
        }
        if (t2 == null) {
            t2 = 0.0;
        }
        return t1 + t2;
    }

    /**
     * {@inheritDoc}
     * @see GeneralTopNRate#getRate(Object, int)
     */
    @Override
    protected double getRate(Double num, int size) {
        if (num == null) {
            return 0;
        }
        if (size <= 0) {
            size = 1;
        }
        return num / size;
    }

    /**
     * {@inheritDoc}
     *
     * @see GeneralTopNRate#getTotal()
     */
    @Override
    public Double getTotal() {
        Double total = super.getTotal();
        return total == null ? 0.0 : total;
    }

}
