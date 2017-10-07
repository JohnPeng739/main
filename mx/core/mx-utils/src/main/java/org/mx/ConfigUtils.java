package org.mx;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.dal.config.SystemConfig;

/**
 * 操作系统配置信息的工具类。
 * 
 * @author john peng on 2016/05/31
 *
 */
public class ConfigUtils {
	private static final Log LOG = LogFactory.getLog(ConfigUtils.class);

	private static final SystemConfig config = SystemConfig.instance();

	/**
	 * 数制枚举
	 */
	public enum Radix {
		/**
		 * 二进制
		 */
		Binary(2),
		/**
		 * 八进制
		 */
		Octonary(8),
		/**
		 * 十进制
		 */
		Decimal(10),
		/**
		 * 十六进制
		 */
		Hexadecimal(16);

		int radix = 10;

		Radix(int radix) {
			this.radix = radix;
		}

		int getRadix() {
			return radix;
		}
	}

	/**
	 * 默认的构造函数
	 */
	private ConfigUtils() {
		super();
	}

	public static void loadConfig(String file) throws IOException {
		config.load(file);
	}

	public static void loadConfigs(String[] files) throws IOException {
		for (String file : files) {
			config.load(file);
		}
	}

	public static void cleanConfig() {
		config.clean();
	}

	public static void updateConfig(String key, String value) {
		config.setValue(key, value);
	}

	public static String getStringValue(String key, String defaultValue) {
		String value = config.getValue(key);
		return value == null ? defaultValue : value;
	}

	public static int getIntValue(String key, int defaultValue) {
		return getIntValue(key, Radix.Decimal, defaultValue);
	}

	public static int getIntValue(String key, Radix radix, int defaultValue) {
		try {
			String value = config.getValue(key);
			return Integer.parseInt(value, radix.getRadix());
		} catch (NumberFormatException ex) {
			if (LOG.isTraceEnabled()) {
				LOG.trace("The String is not match number format.", ex);
			}
		}
		return defaultValue;
	}

	public static boolean getBooleanValue(String key, boolean defaultValue) {
		String value = config.getValue(key);
		if ("true".equalsIgnoreCase(value)) {
			return true;
		} else if ("false".equalsIgnoreCase(value)) {
			return false;
		} else {
			return defaultValue;
		}
	}

	public static char getCharValue(String key, char defaultValue) {
		String value = config.getValue(key);
		if (value == null || value.isEmpty()) {
			return defaultValue;
		}
		return value.charAt(0);
	}

	public static long getLongValue(String key, long defaultValue) {
		return getLongValue(key, Radix.Decimal, defaultValue);
	}

	public static long getLongValue(String key, Radix radix, long defaultValue) {
		try {
			String value = config.getValue(key);
			return Long.parseLong(value, radix.getRadix());
		} catch (NumberFormatException ex) {
			if (LOG.isTraceEnabled()) {
				LOG.trace("The String is not match number format.", ex);
			}
		}
		return defaultValue;
	}

	public static float getFloatValue(String key, float defaultValue) {
		try {
			String value = config.getValue(key);
			return Float.valueOf(value);
		} catch (NumberFormatException ex) {
			if (LOG.isTraceEnabled()) {
				LOG.trace("The String is not match number format.", ex);
			}
		}
		return defaultValue;
	}

	public static double getDoubleValue(String key, double defaultValue) {
		try {
			String value = config.getValue(key);
			return Double.valueOf(value);
		} catch (NumberFormatException ex) {
			if (LOG.isTraceEnabled()) {
				LOG.trace("The String is not match number format.", ex);
			}
		}
		return defaultValue;
	}
}
