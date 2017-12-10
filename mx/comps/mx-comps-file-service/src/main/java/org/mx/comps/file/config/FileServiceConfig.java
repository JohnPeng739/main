package org.mx.comps.file.config;

import org.mx.comps.file.servlet.FileDownloadServlet;
import org.mx.comps.file.websocket.FileUploadWebsocket;
import org.mx.service.server.config.ServerConfig;
import org.springframework.context.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 文件管理服务（上传、下载）的配置类。
 *
 * @author : john.peng created on date : 2017/12/04
 */
@Configuration
@Import({ServerConfig.class})
@PropertySource({"classpath:file-processor.properties"})
@ComponentScan({
        "org.mx.comps.file.processor",
        "org.mx.comps.file.servlet",
        "org.mx.comps.file.websocket",
        "org.mx.comps.file.processor.simple"
})
public class FileServiceConfig {
    @Bean(name = "servletClassesFile")
    public List<Class<?>> servletClassesTest() {
        return Arrays.asList(FileDownloadServlet.class);
    }

    @Bean(name = "websocketClassesFile")
    public List<Class<?>> websocketClassesTest() {
        return Arrays.asList(FileUploadWebsocket.class);
    }
}
