package org.mx.comps.file.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * 文件管理服务（上传、下载）的配置类。
 *
 * @author : john.peng created on date : 2017/12/04
 */
@Configuration
@PropertySource({"classpath:file-persist.properties"})
@ComponentScan({
        "org.mx.comps.file.servlet",
        "org.mx.comps.file.websocket",
        "org.mx.comps.file.processor.simple"
})
public class FileServiceConfig {
}
