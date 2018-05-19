package org.mx;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.config.SystemConfig;

import java.io.IOException;

/**
 * 操作系统配置信息的工具类。
 *
 * @author : john.peng date : 2017/10/15
 */
public class ConfigUtils {
    private static final Log LOG = LogFactory.getLog(ConfigUtils.class);

    private static final SystemConfig config = SystemConfig.instance();

    /**
     * 默认的构造函数
     */
    private ConfigUtils() {
        super();
    }

    public static void loadConfig(String file) throws IOException {
        config.load(file);
    }

    /**
     * 加载指定的配置文件中的配置信息
     *
     * @param files 配置文件列表
     * @throws IOException 加载过程中发生的异常
     * @see SystemConfig#load(String)
     */
    public static void loadConfigs(String[] files) throws IOException {
        for (String file : files) {
            config.load(file);
        }
    }

    /**
     * 清除所有已经被加载的配置信息
     */
    public static void cleanConfig() {
        config.clean();
    }

    /**
     * 更新指定配置项的值
     *
     * @param key   配置项名
     * @param value 值
     */
    public static void updateConfig(String key, String value) {
        config.setValue(key, value);
    }

    /**
     * 获取指定配置项的字符串类型的值
     *
     * @param key          配置项名
     * @param defaultValue 默认值
     * @return 值
     */
    public static String getStringValue(String key, String defaultValue) {
        String value = config.getValue(key);
        return value == null ? defaultValue : value;
    }

    /**
     * 获取指定配置项的整型值
     *
     * @param key          配置项名
     * @param defaultValue 默认值
     * @return 值
     * @see #getIntValue(String, TypeUtils.Radix, int)
     */
    public static int getIntValue(String key, int defaultValue) {
        return getIntValue(key, TypeUtils.Radix.Decimal, defaultValue);
    }

    /**
     * 获取指定配置项的整型值
     *
     * @param key          配置项名
     * @param radix        数制
     * @param defaultValue 默认值
     * @return 值
     */
    public static int getIntValue(String key, TypeUtils.Radix radix, int defaultValue) {
        return TypeUtils.string2Int(config.getValue(key), radix, defaultValue);
    }

    /**
     * 获取指定配置项的布尔值
     *
     * @param key          配置项名
     * @param defaultValue 默认值
     * @return 值
     */
    public static boolean getBooleanValue(String key, boolean defaultValue) {
        return TypeUtils.string2Boolean(config.getValue(key), defaultValue);
    }

    /**
     * 获取指定配置项的字符值
     *
     * @param key          配置项名
     * @param defaultValue 默认值
     * @return 值
     */
    public static char getCharValue(String key, char defaultValue) {
        String value = config.getValue(key);
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }
        return value.charAt(0);
    }

    /**
     * 获取指定配置项的长整型值
     *
     * @param key          配置项名
     * @param defaultValue 默认值
     * @return 值
     * @see #getLongValue(String, TypeUtils.Radix, long)
     */
    public static long getLongValue(String key, long defaultValue) {
        return getLongValue(key, TypeUtils.Radix.Decimal, defaultValue);
    }

    /**
     * 获取指定配置项的长整型值
     *
     * @param key          配置项名
     * @param radix        数制
     * @param defaultValue 默认值
     * @return 值
     */
    public static long getLongValue(String key, TypeUtils.Radix radix, long defaultValue) {
        return TypeUtils.string2Long(config.getValue(key), radix, defaultValue);
    }

    /**
     * 获取指定配置项的单精度值
     *
     * @param key          配置项名
     * @param defaultValue 默认值
     * @return 值
     */
    public static float getFloatValue(String key, float defaultValue) {
        return TypeUtils.string2Float(config.getValue(key), defaultValue);
    }

    /**
     * 从配置信息中获取一个双精度值
     *
     * @param key          配置项名
     * @param defaultValue 默认值
     * @return 值
     */
    public static double getDoubleValue(String key, double defaultValue) {
        return TypeUtils.string2Double(config.getValue(key), defaultValue);
    }
}
