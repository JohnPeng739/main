package org.mx.service.server.config;

import org.mx.service.server.rest.DemoRestResource;
import org.mx.service.server.servlet.DownloadFileServlet;
import org.mx.service.server.websocket.EchoSocket;
import org.springframework.context.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * Created by john on 2017/11/4.
 */
@Configuration
@Import(TestConfig.class)
@PropertySource({
        "classpath:connect-filter-rules-allow.properties"
})
public class TestListFilterAllowConfig {
}
