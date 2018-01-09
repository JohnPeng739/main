/* 
 * @(#)TestIntegerTopNRate.java
 *
 * Copyright 2016, 迪爱斯通信设备有限公司保留.
 */
package org.mx.rate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestIntegerTopNRate {
	private static final Log logger = LogFactory.getLog(TestIntegerTopNRate.class);

	private int[][] value = { { 1, 2, 3, 4 }, { 3, 4, 6, 7, 8, 2 }, { 1, 2, 3, 4, 5, 67, 7 }, { 3, 4, 6, 2, 32 },
			{ 2, 3, 43, 35, 5, 67 }, { 1, 2, 4, 3, 5, 6, 8, 9, 0, 4 }, { 0 }, { 1, 3, 5 }, { 7, 8, 4, 3 },
			{ 0, 2, 3, 5 }, { 5, 5, 4, 3 }, { 1, 2, 3 }, { 9, 5, 7, 7 }, { 3, 4, 6, 7 }, { 2, 66, 3 }, { 32, 5, 78 },
			{ 2, 6, 9, 5, 9 }, { 1, 5, 7, 3 }, { 3, 5, 8, 9 }, { 2, 5, 8, 5 }, { 3, 4, 6, 2 }, { 7, 4, 2, 7 },
			{ 5, 3, 7, 8 }, { 2, 5, 9, 5 }, { 4, 6, 2 }, { 2, 4 }, { 7, 4, 7 }, { 5, 8, 3 }, { 4, 4, 4 },
			{ 2, 4, 2, 4, 5 }, { 4, 3, 1 }, { 3, 3, 8 }, { 4, 5, 1, 7 }, { 3, 3, 8 } };
	
	@Test
	public void test1() throws Exception {
		double scale = 1;
		IntegerTopNRate rate = new IntegerTopNRate(6, scale);
		assertEquals(rate.getValueType(), GeneralTopNRate.ValueType.Integer);
		int[] vs = null;
		int total = 0;
		while (System.currentTimeMillis() % 1000 > 200) {
		    Thread.sleep(10);
        }
		long t0 = System.currentTimeMillis();
		for (int[] vv : value) {
			long t1 = System.currentTimeMillis();
			for (int v : vv) {
				rate.addNumber(v);
				total += v;
			}
			while (System.currentTimeMillis() - t1 < 300) {
				Thread.sleep(10);
			}
			int size = (int) ((System.currentTimeMillis() - t0) / 1000) + 1;
            assertEquals(rate.getTotalRate(), (double) (total / size), 0.00001);
			while (System.currentTimeMillis() - t1 < 1000) {
				Thread.sleep(10);
			}
		}

		vs = new int[value.length];
        while (System.currentTimeMillis() % 1000 > 100) {
            Thread.sleep(10);
        }
		rate.reset();
		for (int index = 0; index < value.length; index++) {
			long t1 = System.currentTimeMillis();
			for (int v : value[index]) {
				rate.addNumber(v);
				vs[index] += v;
			}
			while (System.currentTimeMillis() - t1 < 200) {
				Thread.sleep(10);
			}
			assertEquals(rate.getTopNRate(1), calaRate(vs, index, scale, 1, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(2), calaRate(vs, index, scale, 2, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(3), calaRate(vs, index, scale, 3, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(4), calaRate(vs, index, scale, 4, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(5), calaRate(vs, index, scale, 5, rate.getN()), 0.0001);
			while (System.currentTimeMillis() - t1 < 1000) {
				Thread.sleep(10);
			}
		}
		
		vs = new int[value.length];
        while (System.currentTimeMillis() % 1000 > 100) {
            Thread.sleep(10);
        }
		rate.reset();
		for (int index = 0; index < value.length; index++) {
			long t1 = System.currentTimeMillis();
			for (int v : value[index]) {
				rate.addNumber(v);
				vs[index] += v;
			}
			while (System.currentTimeMillis() - t1 < 200) {
				Thread.sleep(10);
			}
			assertEquals(rate.getTopNRate(6), calaRate(vs, index, scale, 6, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(7), calaRate(vs, index, scale, 7, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(8), calaRate(vs, index, scale, 8, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(9), calaRate(vs, index, scale, 9, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(10), calaRate(vs, index, scale, 10, rate.getN()), 0.0001);
			while (System.currentTimeMillis() - t1 < 1000) {
				Thread.sleep(10);
			}
		}

		vs = new int[value.length];
        while (System.currentTimeMillis() % 1000 > 100) {
            Thread.sleep(10);
        }
		rate.reset();
		for (int index = 0; index < value.length; index++) {
			long t1 = System.currentTimeMillis();
			for (int v : value[index]) {
				rate.addNumber(v);
				vs[index] += v;
			}
			while (System.currentTimeMillis() - t1 < 200) {
				Thread.sleep(10);
			}
			assertEquals(rate.getTopNRate(11), calaRate(vs, index, scale, 11, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(12), calaRate(vs, index, scale, 12, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(13), calaRate(vs, index, scale, 13, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(14), calaRate(vs, index, scale, 14, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(15), calaRate(vs, index, scale, 15, rate.getN()), 0.0001);
			while (System.currentTimeMillis() - t1 < 1000) {
				Thread.sleep(10);
			}
		}

		vs = new int[value.length];
        while (System.currentTimeMillis() % 1000 > 100) {
            Thread.sleep(10);
        }
		rate.reset();
		for (int index = 0; index < value.length; index++) {
			long t1 = System.currentTimeMillis();
			for (int v : value[index]) {
				rate.addNumber(v);
				vs[index] += v;
			}
			while (System.currentTimeMillis() - t1 < 200) {
				Thread.sleep(10);
			}
			assertEquals(rate.getTopNRate(16), calaRate(vs, index, scale, 16, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(17), calaRate(vs, index, scale, 17, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(18), calaRate(vs, index, scale, 18, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(19), calaRate(vs, index, scale, 19, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(20), calaRate(vs, index, scale, 20, rate.getN()), 0.0001);
			while (System.currentTimeMillis() - t1 < 1000) {
				Thread.sleep(10);
			}
		}

		vs = new int[value.length];
        while (System.currentTimeMillis() % 1000 > 100) {
            Thread.sleep(10);
        }
		rate.reset();
		for (int index = 0; index < value.length; index++) {
			long t1 = System.currentTimeMillis();
			for (int v : value[index]) {
				rate.addNumber(v);
				vs[index] += v;
			}
			while (System.currentTimeMillis() - t1 < 200) {
				Thread.sleep(10);
			}
			assertEquals(rate.getTopNRate(21), calaRate(vs, index, scale, 21, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(22), calaRate(vs, index, scale, 22, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(23), calaRate(vs, index, scale, 23, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(24), calaRate(vs, index, scale, 24, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(25), calaRate(vs, index, scale, 25, rate.getN()), 0.0001);
			while (System.currentTimeMillis() - t1 < 1000) {
				Thread.sleep(10);
			}
		}

		vs = new int[value.length];
        while (System.currentTimeMillis() % 1000 > 100) {
            Thread.sleep(10);
        }
		rate.reset();
		for (int index = 0; index < value.length; index++) {
			long t1 = System.currentTimeMillis();
			for (int v : value[index]) {
				rate.addNumber(v);
				vs[index] += v;
			}
			while (System.currentTimeMillis() - t1 < 200) {
				Thread.sleep(10);
			}
			assertEquals(rate.getTopNRate(26), calaRate(vs, index, scale, 26, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(27), calaRate(vs, index, scale, 27, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(28), calaRate(vs, index, scale, 28, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(29), calaRate(vs, index, scale, 29, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(30), calaRate(vs, index, scale, 30, rate.getN()), 0.0001);
			while (System.currentTimeMillis() - t1 < 1000) {
				Thread.sleep(10);
			}
		}

		vs = new int[value.length];
        while (System.currentTimeMillis() % 1000 > 100) {
            Thread.sleep(10);
        }
		rate.reset();
		for (int index = 0; index < value.length; index++) {
			long t1 = System.currentTimeMillis();
			for (int v : value[index]) {
				rate.addNumber(v);
				vs[index] += v;
			}
			while (System.currentTimeMillis() - t1 < 200) {
				Thread.sleep(10);
			}
			assertEquals(rate.getTopNRate(31), calaRate(vs, index, scale, 31, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(32), calaRate(vs, index, scale, 32, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(33), calaRate(vs, index, scale, 33, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(34), calaRate(vs, index, scale, 34, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(35), calaRate(vs, index, scale, 35, rate.getN()), 0.0001);
			while (System.currentTimeMillis() - t1 < 1000) {
				Thread.sleep(10);
			}
		}

		vs = new int[value.length];
        while (System.currentTimeMillis() % 1000 > 100) {
            Thread.sleep(10);
        }
		rate.reset();
		for (int index = 0; index < value.length; index++) {
			long t1 = System.currentTimeMillis();
			for (int v : value[index]) {
				rate.addNumber(v);
				vs[index] += v;
			}
			while (System.currentTimeMillis() - t1 < 200) {
				Thread.sleep(10);
			}
			assertEquals(rate.getTopNRate(36), calaRate(vs, index, scale, 36, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(37), calaRate(vs, index, scale, 37, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(38), calaRate(vs, index, scale, 38, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(39), calaRate(vs, index, scale, 39, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(40), calaRate(vs, index, scale, 40, rate.getN()), 0.0001);
			while (System.currentTimeMillis() - t1 < 1000) {
				Thread.sleep(10);
			}
		}

	}

	@Test
	public void test2() throws Exception {
		double scale = 2;
		IntegerTopNRate rate = new IntegerTopNRate(10, scale);
		assertEquals(rate.getValueType(), GeneralTopNRate.ValueType.Integer);
		int[] vs = null;

		vs = new int[value.length];
        while (System.currentTimeMillis() % 1000 > 100) {
            Thread.sleep(10);
        }
		rate.reset();
		for (int index = 0; index < value.length;) {
			long t1 = System.currentTimeMillis();
			while (System.currentTimeMillis() - t1 < 200) {
				Thread.sleep(10);
			}
			for (int j = 0; j < value[index].length; j++) {
				rate.addNumber(value[index][j]);
				vs[index] += value[index][j];
			}
			while (System.currentTimeMillis() - t1 < 1000) {
				Thread.sleep(10);
			}
			index++;
			for (int j = 0; j < value[index].length; j++) {
				rate.addNumber(value[index][j]);
				vs[index] += value[index][j];
			}
			while (System.currentTimeMillis() - t1 < 1500) {
				Thread.sleep(10);
			}
			assertEquals(rate.getTopNRate(1), calaRate(vs, index, scale, 1, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(2), calaRate(vs, index, scale, 2, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(3), calaRate(vs, index, scale, 3, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(4), calaRate(vs, index, scale, 4, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(5), calaRate(vs, index, scale, 5, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(6), calaRate(vs, index, scale, 6, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(7), calaRate(vs, index, scale, 7, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(8), calaRate(vs, index, scale, 8, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(9), calaRate(vs, index, scale, 9, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(10), calaRate(vs, index, scale, 10, rate.getN()), 0.0001);
			while (System.currentTimeMillis() - t1 < 2000) {
				Thread.sleep(10);
			}
			index++;
		}

		vs = new int[value.length];
        while (System.currentTimeMillis() % 1000 > 100) {
            Thread.sleep(10);
        }
		rate.reset();
		for (int index = 0; index < value.length;) {
			long t1 = System.currentTimeMillis();
			while (System.currentTimeMillis() - t1 < 200) {
				Thread.sleep(10);
			}
			for (int j = 0; j < value[index].length; j++) {
				rate.addNumber(value[index][j]);
				vs[index] += value[index][j];
			}
			while (System.currentTimeMillis() - t1 < 1000) {
				Thread.sleep(10);
			}
			index++;
			for (int j = 0; j < value[index].length; j++) {
				rate.addNumber(value[index][j]);
				vs[index] += value[index][j];
			}
			while (System.currentTimeMillis() - t1 < 1500) {
				Thread.sleep(10);
			}
			assertEquals(rate.getTopNRate(11), calaRate(vs, index, scale, 11, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(12), calaRate(vs, index, scale, 12, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(13), calaRate(vs, index, scale, 13, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(14), calaRate(vs, index, scale, 14, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(15), calaRate(vs, index, scale, 15, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(16), calaRate(vs, index, scale, 16, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(17), calaRate(vs, index, scale, 17, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(18), calaRate(vs, index, scale, 18, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(19), calaRate(vs, index, scale, 19, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(20), calaRate(vs, index, scale, 20, rate.getN()), 0.0001);
			while (System.currentTimeMillis() - t1 < 2000) {
				Thread.sleep(10);
			}
			index++;
		}

		vs = new int[value.length];
        while (System.currentTimeMillis() % 1000 > 100) {
            Thread.sleep(10);
        }
		rate.reset();
		for (int index = 0; index < value.length;) {
			long t1 = System.currentTimeMillis();
			while (System.currentTimeMillis() - t1 < 200) {
				Thread.sleep(10);
			}
			for (int j = 0; j < value[index].length; j++) {
				rate.addNumber(value[index][j]);
				vs[index] += value[index][j];
			}
			while (System.currentTimeMillis() - t1 < 1000) {
				Thread.sleep(10);
			}
			index++;
			for (int j = 0; j < value[index].length; j++) {
				rate.addNumber(value[index][j]);
				vs[index] += value[index][j];
			}
			while (System.currentTimeMillis() - t1 < 1500) {
				Thread.sleep(10);
			}
			assertEquals(rate.getTopNRate(21), calaRate(vs, index, scale, 21, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(22), calaRate(vs, index, scale, 22, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(23), calaRate(vs, index, scale, 23, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(24), calaRate(vs, index, scale, 24, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(25), calaRate(vs, index, scale, 25, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(26), calaRate(vs, index, scale, 26, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(27), calaRate(vs, index, scale, 27, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(28), calaRate(vs, index, scale, 28, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(29), calaRate(vs, index, scale, 29, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(30), calaRate(vs, index, scale, 30, rate.getN()), 0.0001);
			while (System.currentTimeMillis() - t1 < 2000) {
				Thread.sleep(10);
			}
			index++;
		}

		vs = new int[value.length];
        while (System.currentTimeMillis() % 1000 > 100) {
            Thread.sleep(10);
        }
		rate.reset();
		for (int index = 0; index < value.length;) {
			long t1 = System.currentTimeMillis();
			while (System.currentTimeMillis() - t1 < 200) {
				Thread.sleep(10);
			}
			for (int j = 0; j < value[index].length; j++) {
				rate.addNumber(value[index][j]);
				vs[index] += value[index][j];
			}
			while (System.currentTimeMillis() - t1 < 1000) {
				Thread.sleep(10);
			}
			index++;
			for (int j = 0; j < value[index].length; j++) {
				rate.addNumber(value[index][j]);
				vs[index] += value[index][j];
			}
			while (System.currentTimeMillis() - t1 < 1500) {
				Thread.sleep(10);
			}
			assertEquals(rate.getTopNRate(31), calaRate(vs, index, scale, 31, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(32), calaRate(vs, index, scale, 32, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(33), calaRate(vs, index, scale, 33, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(34), calaRate(vs, index, scale, 34, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(35), calaRate(vs, index, scale, 35, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(36), calaRate(vs, index, scale, 36, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(37), calaRate(vs, index, scale, 37, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(38), calaRate(vs, index, scale, 38, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(39), calaRate(vs, index, scale, 39, rate.getN()), 0.0001);
			assertEquals(rate.getTopNRate(40), calaRate(vs, index, scale, 40, rate.getN()), 0.0001);
			while (System.currentTimeMillis() - t1 < 2000) {
				Thread.sleep(10);
			}
			index++;
		}
	}

	private double calaRate(int[] vs, int index, double scale, int num, int N) {
		int total = 0;
		for (int n = index; n >= 0 && (index - n < num || num > N); n--) {
			total += vs[n];
		}
		num  = Math.min(index + 1, num);
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("num=%d, total=%d, rate=%6.2f.", num, total, (double) (total / num)));
		}
		return (double) (total / num);
	}
}
