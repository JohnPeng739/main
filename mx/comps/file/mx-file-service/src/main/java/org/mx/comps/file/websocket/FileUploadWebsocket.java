package org.mx.comps.file.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.mx.StringUtils;
import org.mx.comps.file.FileWriteProcessor;
import org.mx.comps.file.processor.ProcessorFactory;
import org.mx.service.server.websocket.DefaultWsSessionMonitor;
import org.mx.service.server.websocket.WsSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 基于Websocket实现的文件上传实现类
 *
 * @author : john.peng created on date : 2017/12/04
 */
@Component("fileUploadWebsocket")
public class FileUploadWebsocket extends DefaultWsSessionMonitor {
    public static final String FILE_UPLOAD_URI_PATH = "/wsupload";
    public static final String CLOSE_FILE_FOR_ERROR = "file.error.close";
    private static final Log logger = LogFactory.getLog(FileUploadWebsocket.class);
    @Autowired
    private Environment env = null;
    @Autowired
    private ProcessorFactory processorFactory = null;
    @Autowired
    private WsSessionManager manager = null;

    private FileWriteProcessor writeProcessor = null;

    public FileUploadWebsocket() {
        super(FILE_UPLOAD_URI_PATH);
    }

    /**
     * {@inheritDoc}
     *
     * @see DefaultWsSessionMonitor#beforeClose(String)
     */
    @Override
    public void beforeClose(String connectKey) {
        if (writeProcessor.isOpened()) {
            writeProcessor.close();
        } else {
            if (logger.isErrorEnabled()) {
                logger.error("The FileWriteProcessor has been closed.");
            }
        }
        super.beforeClose(connectKey);
    }

    /**
     * {@inheritDoc}
     *
     * @see DefaultWsSessionMonitor#hasError(String, Throwable)
     */
    @Override
    public void hasError(String connectKey, Throwable throwable) {
        boolean closeFileForError = env.getProperty(CLOSE_FILE_FOR_ERROR, Boolean.class, true);
        if (closeFileForError) {
            writeProcessor.close();
        }
        super.hasError(connectKey, throwable);
    }

    /**
     * {@inheritDoc}
     *
     * @see DefaultWsSessionMonitor#hasBinary(String, byte[])
     */
    @Override
    public void hasBinary(String connectKey, byte[] buffer) {
        if (writeProcessor.isOpened()) {
            writeProcessor.write(buffer);
        } else {
            if (logger.isErrorEnabled()) {
                logger.error("The FileWriteProcessor is not opened.");
            }
        }
        super.hasBinary(connectKey, buffer);
    }

    /**
     * 将收到的文本消息转换为JSONObject对象描述的命令对象，该对象至少包括：command字段，如：
     * <pre>
     * {
     *     command: 'init',
     *     processorType: 'simple'
     * }
     * 或
     * {
     *     command: 'start',
     *     directory: '/root',
     *     filename: 'filename.txt'
     * }
     * </pre>
     *
     * @see DefaultWsSessionMonitor#hasText(String, String)
     */
    @Override
    public void hasText(String connectKey, String message) {
        if (StringUtils.isBlank(message)) {
            if (logger.isErrorEnabled()) {
                logger.error("The command data is blank.");
            }
            return;
        }
        JSONObject json = JSON.parseObject(message);
        try {
            String command = json.getString("command");
            if ("init".equalsIgnoreCase(command)) {
                // 初始化FileWriteProcessor
                if (processorFactory == null) {
                    throw new Exception("Get ProcessorFactory fail, name: fileServiceProcessorFactory.");
                }
                String type = json.getString("processorType");
                if (StringUtils.isBlank(type)) {
                    throw new Exception("The processor's type is blank in 'init' command.");
                }
                writeProcessor = processorFactory.createWriteProcessor(ProcessorFactory.ProcessorType.valueOf(type));
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Init a %s FileWriteProcessor successfully.", type));
                }
            } else {
                writeProcessor.command(json);
            }
            json.put("result", "ok");
        } catch (Exception ex) {
            json.put("result", "error");
        }
        try {
            Session session = manager.getSession(connectKey);
            if (session != null) {
                session.getRemote().sendString(json.toJSONString());
            } else {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("The session[%s] not existed.", connectKey));
                }
            }
        } catch (IOException ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Send file upload response fail.", ex);
            }
        }
        super.hasText(connectKey, message);
    }
}
