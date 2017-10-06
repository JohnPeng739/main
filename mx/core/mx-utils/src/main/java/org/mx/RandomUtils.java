package org.mx;

import java.util.Random;

/**
 * 随机数工具类，包括了各种整数、浮点数的随机数生成方法。
 * 
 * @author john peng on 2016/5/29
 *
 */
public class RandomUtils {
	private static Random random = new Random(System.currentTimeMillis());

	/**
	 * 默认的构造函数
	 */
	private RandomUtils() {
		super();
	}

	/**
	 * 获取一个从0.0到1.0之间的随机双精度浮点数。
	 * 
	 * @return 随机数
	 */
	public static double randomDouble() {
		return random.nextDouble();
	}

	/**
	 * 获取一个介于min和max之间的随机双精度浮点数。
	 * 
	 * @param min
	 *            下限
	 * @param max
	 *            上限
	 * @return 随机数
	 */
	public static double randomDouble(long min, long max) {
		return min + (max - min) * random.nextDouble();
	}

	/**
	 * 获取一个介于min和max之间的随机整数。
	 * 
	 * @param min
	 *            下限
	 * @param max
	 *            上限
	 * @return 随机数
	 */
	public static int randomInt(int min, int max) {
		return (int) (min + (max - min) * random.nextDouble());
	}

	/**
	 * 获取一个介于min和max之间的随机长整数。
	 * 
	 * @param min
	 *            下限
	 * @param max
	 *            上限
	 * @return 随机数
	 */
	public static long randomLong(long min, long max) {
		return (long) (min + (max - min) * random.nextDouble());
	}
}
