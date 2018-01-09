package org.mx.comps.file.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.mx.StringUtils;
import org.mx.comps.file.FileWriteProcessor;
import org.mx.comps.file.processor.ProcessorFactory;
import org.mx.service.server.websocket.BaseWebsocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

/**
 * 基于Websocket实现的文件上传实现类
 *
 * @author : john.peng created on date : 2017/12/04
 */
@Component("fileUploadWebsocket")
@WebSocket
public class FileUploadWebsocket extends BaseWebsocket {
    public static final String FILE_UPLOAD_URI_PATH = "/wsupload";
    public static final String CLOSE_FILE_FOR_ERROR = "file.error.close";
    private static final Log logger = LogFactory.getLog(FileUploadWebsocket.class);
    @Autowired
    private Environment env = null;

    @Autowired
    private ApplicationContext context = null;

    private FileWriteProcessor writeProcessor = null;

    public FileUploadWebsocket() {
        super(FILE_UPLOAD_URI_PATH, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see BaseWebsocket#afterConnect(Session)
     */
    @Override
    public void afterConnect(Session session) {
        super.afterConnect(session);
    }

    /**
     * {@inheritDoc}
     *
     * @see BaseWebsocket#beforeClose(Session, int, String)
     */
    @Override
    public void beforeClose(Session session, int statusCode, String reason) {
        if (writeProcessor.isOpened()) {
            writeProcessor.close();
        } else {
            if (logger.isErrorEnabled()) {
                logger.error("The FileWriteProcessor has been closed.");
            }
        }
        super.beforeClose(session, statusCode, reason);
    }

    /**
     * {@inheritDoc}
     *
     * @see BaseWebsocket#afterError(Session, Throwable)
     */
    @Override
    public void afterError(Session session, Throwable throwable) {
        boolean closeFileForError = env.getProperty(CLOSE_FILE_FOR_ERROR, Boolean.class, true);
        if (closeFileForError) {
            writeProcessor.close();
        }
        super.afterError(session, throwable);
    }

    /**
     * {@inheritDoc}
     *
     * @see BaseWebsocket#receiveBinary(Session, InputStream)
     */
    @Override
    public void receiveBinary(Session session, InputStream in) {
        if (writeProcessor.isOpened()) {
            writeProcessor.write(in);
        } else {
            if (logger.isErrorEnabled()) {
                logger.error("The FileWriteProcessor is not opened.");
            }
        }
        super.receiveBinary(session, in);
    }

    /**
     * 将收到的文本消息转换为JSONObject对象描述的命令对象，该对象至少包括：command字段，如：
     * {
     * command: 'init',
     * processorType: 'simple'
     * }
     * 或
     * {
     * command: 'start',
     * directory: '/root',
     * filename: 'filename.txt'
     * }
     *
     * @see BaseWebsocket#receiveText(Session, String)
     */
    @Override
    public void receiveText(Session session, String message) {
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
                ProcessorFactory factory = context.getBean("fileServiceProcessorFactory", ProcessorFactory.class);
                if (factory == null) {
                    throw new Exception("Get ProcessorFactory fail, name: fileServiceProcessorFactory.");
                }
                String type = json.getString("processorType");
                if (StringUtils.isBlank(type)) {
                    throw new Exception("The processor's type is blank in 'init' command.");
                }
                writeProcessor = factory.createWriteProcessor(ProcessorFactory.ProcessorType.valueOf(type));
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
            session.getRemote().sendString(json.toJSONString());
        } catch (IOException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
        }
        super.receiveText(session, message);
    }
}
