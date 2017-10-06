package org.mx.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;

public class SystemConfig {
	private static final Log LOG = LogFactory.getLog(SystemConfig.class);
	private static SystemConfig config = null;

	private enum ConfigFileType {
		Properties, XML
	}

	public static SystemConfig instance() {
		if (config == null) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("The ApplicationConfig not instance, will instance it.");
			}
			config = new SystemConfig();
		}
		return config;
	}

	private ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();

	private class ConfigFileMetaData {
		String file = null;
		boolean isFromFile = true;
		ConfigFileType type = null;
	
		ConfigFileMetaData(String filePath) throws IOException {
			super();
			String tar = filePath.toLowerCase();
			String[] flag = { "classpath:", "file:" };
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

	private SystemConfig() {
		super();
	}

	public void clean() {
		map.clear();
	}

	public void load(String file) throws IOException {
		if (StringUtils.isBlank(file)) {
			throw new IOException("The file path and name is blank.");
		}
		ConfigFileMetaData meta = new ConfigFileMetaData(file);
		InputStream in = null;
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

	public String getValue(String key) {
		return map.get(key);
	}

	public void setValue(String key, Object value) {
		map.put(key, String.valueOf(value));
	}
}
