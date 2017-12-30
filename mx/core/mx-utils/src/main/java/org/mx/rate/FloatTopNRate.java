/* 
 * @(#)IntegerTopNRate.java
 *
 * Copyright 2016, 迪爱斯通信设备有限公司保留.
 */
package org.mx.rate;

/**
 * 面向Float数值的速率计算类
 *
 * @author john peng
 */
public class FloatTopNRate extends GeneralTopNRate<Float> {

    public FloatTopNRate() {
        super();
    }

    public FloatTopNRate(int N) {
        super(N);
    }

    public FloatTopNRate(int N, double scale) {
        super(N, scale);
    }

    /**
     * {@inheritDoc}
     * @see GeneralTopNRate#add(Object, Object)
     */
    @Override
    protected Float add(Float t1, Float t2) {
        if (t1 == null) {
            t1 = 0f;
        }
        if (t2 == null) {
            t2 = 0f;
        }
        return t1 + t2;
    }

    /**
     * {@inheritDoc}
     * @see GeneralTopNRate#getRate(Object, int)
     */
    @Override
    protected double getRate(Float num, int size) {
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
    public Float getTotal() {
        Float total = super.getTotal();
        return total == null ? 0.0f : total;
    }

}
