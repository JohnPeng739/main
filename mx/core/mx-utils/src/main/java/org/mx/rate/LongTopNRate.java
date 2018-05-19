/* 
 * @(#)IntegerTopNRate.java
 *
 * Copyright 2016, 迪爱斯通信设备有限公司保留.
 */
package org.mx.rate;

/**
 * 面向Long型数值的速率计算类
 * 
 * @author john peng
 */
public class LongTopNRate extends GeneralTopNRate<Long> {

	public LongTopNRate() {
		super();
	}

	public LongTopNRate(int N) {
		super(N);
	}

	public LongTopNRate(int N, double scale) {
		super(N, scale);
	}

    /**
     * {@inheritDoc}
     * @see GeneralTopNRate#add(Object, Object)
     */
    @Override
    protected Long add(Long t1, Long t2) {
        if (t1 == null) {
            t1 = 0L;
        }
        if (t2 == null) {
            t2 = 0L;
        }
        return t1 + t2;
    }

    /**
     * {@inheritDoc}
     * @see GeneralTopNRate#getRate(Object, int)
     */
    @Override
    protected double getRate(Long num, int size) {
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
	 * @see GeneralTopNRate#getTotal()
	 */
	@Override
	public Long getTotal() {
		Long total = super.getTotal();
		return total == null ?  0 : total;
	}

}
