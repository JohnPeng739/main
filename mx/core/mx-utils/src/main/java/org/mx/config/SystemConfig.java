package org.mx.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 系统配置对象
 *
 * @author : john.peng date : 2017/10/15
 */
public class SystemConfig {
    private static final Log LOG = LogFactory.getLog(SystemConfig.class);
    private static SystemConfig config = null;
    /**
     * 配置信息集合
     */
    private ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();

    /**
     * 默认的构造函数
     */
    private SystemConfig() {
        super();
    }

    /**
     * 系统配置对象实例工厂方法
     *
     * @return 系统配置对象
     */
    public static SystemConfig instance() {
        if (config == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("The ApplicationConfig not instance, will instance it.");
            }
            config = new SystemConfig();
        }
        return config;
    }

    /**
     * 清除已经加载的配置信息
     */
    public void clean() {
        map.clear();
    }

    /**
     * 从文件中加载指配置信息内容
     *
     * @param file 配置文件
     * @throws IOException 加载过程中发生的异常
     */
    public void load(String file) throws IOException {
        if (StringUtils.isBlank(file)) {
            throw new IOException("The file path and name is blank.");
        }
        ConfigFileMetaData meta = new ConfigFileMetaData(file);
        InputStream in;
        if (meta.isFromFile) {
            in = new FileInputStream(meta.file);
        } else {
            in = SystemConfig.class.getResourceAsStream(file);
        }
        try {
            load(in, meta.type);
        } finally {
            in.close();
        }
    }

    /**
     * 从输入流中加载配置信息内容
     *
     * @param in   输入流
     * @param type 类型
     * @throws IOException 加载过程中发生的异常
     */
    public void load(InputStream in, ConfigFileType type) throws IOException {
        Properties properties = new Properties();
        if (type == ConfigFileType.Properties) {
            properties.load(in);
        } else if (type == ConfigFileType.XML) {
            properties.loadFromXML(in);
        } else {
            throw new IOException(String.format("Not supported config file type: %s.", type.name()));
        }
        for (String key : properties.stringPropertyNames()) {
            map.put(key, properties.getProperty(key));
        }
    }

    /**
     * 获取指定配置项的值
     *
     * @param key 配置项名
     * @return 值，如果不存在，则返回null
     */
    public String getValue(String key) {
        return map.get(key);
    }

    /**
     * 设置配置项的值
     *
     * @param key   配置项名
     * @param value 值
     */
    public void setValue(String key, Object value) {
        map.put(key, String.valueOf(value));
    }

    /**
     * 配置文件类型枚举
     */
    private enum ConfigFileType {
        /**
         * PROPERTIES类型文件
         */
        Properties,
        /**
         * XML类型文件
         */
        XML
    }

    /**
     * 配置文件元数据对象
     *
     * @author : john.peng date : 2017/10/15
     */
    private class ConfigFileMetaData {
        String file;
        boolean isFromFile;
        ConfigFileType type;

        /**
         * 默认的构造函数
         *
         * @param filePath 文件路径
         * @throws IOException 初始化过程中发生的异常
         */
        ConfigFileMetaData(String filePath) throws IOException {
            super();
            String tar = filePath.toLowerCase();
            String[] flag = {"classpath:", "file:"};
            if (tar.startsWith(flag[0])) {
                isFromFile = false;
                file = filePath.substring(flag[0].length());
            } else if (tar.startsWith(flag[1])) {
                isFromFile = true;
                file = filePath.substring(flag[1].length());
            }

            if (tar.endsWith(".properties")) {
                type = ConfigFileType.Properties;
            } else if (tar.endsWith(".xml")) {
                type = ConfigFileType.XML;
            }
            throw new IOException(String.format("Not supported config file type: %s.", filePath));
        }
    }
}
