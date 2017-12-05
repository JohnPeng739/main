package org.mx.comps.file.websocket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.mx.comps.file.FilePersistProcessor;
import org.mx.service.server.websocket.BaseWebsocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.InputStream;

/**
 * 基于Websocket实现的文件上传实现类
 *
 * @author : john.peng created on date : 2017/12/04
 */
@Component("fileUploadWebsocket")
public class FileUploadWebsocket extends BaseWebsocket {
    public static final String FILE_UPLOAD_URI_PATH = "/wsupload";
    public static final String CLOSE_FILE_FOR_ERROR = "file.error.close";
    private static final Log logger = LogFactory.getLog(FileUploadWebsocket.class);
    @Autowired
    private Environment env = null;

    @Autowired
    private ApplicationContext context = null;

    private FilePersistProcessor persistProcessor = null;

    public FileUploadWebsocket() {
        super(FILE_UPLOAD_URI_PATH);
    }

    private void initBean() {
        if (persistProcessor == null) {
            Assert.notNull(context, "The ApplicationContext");
            String persistType = env.getProperty("file.processor", String.class, "simple");
            switch (persistType) {
                case "simple":
                    persistProcessor = context.getBean("simpleFilePersistProcessor", FilePersistProcessor.class);
                    break;
                default:
                    if (logger.isErrorEnabled()) {
                        logger.error(String.format("Unsupported file persist processor type: %s.", persistType));
                    }
                    break;
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see BaseWebsocket#onConnection(Session)
     */
    @Override
    public void onConnection(Session session) {
        super.onConnection(session);

        initBean();
        Assert.notNull(persistProcessor, "The FilePersistProcessor not be initialized.");
    }

    /**
     * {@inheritDoc}
     *
     * @see BaseWebsocket#onClose(Session, int, String)
     */
    @Override
    public void onClose(Session session, int statusCode, String reason) {
        initBean();
        if (persistProcessor.isOpened()) {
            persistProcessor.close();
        } else {
            if (logger.isErrorEnabled()) {
                logger.error("The FilePersistProcessor has been closed.");
            }
        }
        super.onClose(session, statusCode, reason);
    }

    /**
     * {@inheritDoc}
     *
     * @see BaseWebsocket#onError(Session, Throwable)
     */
    @Override
    public void onError(Session session, Throwable throwable) {
        initBean();
        boolean closeFileForError = env.getProperty(CLOSE_FILE_FOR_ERROR, Boolean.class, true);
        if (closeFileForError) {
            persistProcessor.close();
        }
        super.onError(session, throwable);
    }

    /**
     * {@inheritDoc}
     *
     * @see BaseWebsocket#onBinaryMessage(Session, InputStream)
     */
    @Override
    public void onBinaryMessage(Session session, InputStream in) {
        initBean();
        if (persistProcessor.isOpened()) {
            persistProcessor.persist(in);
        } else {
            if (logger.isErrorEnabled()) {
                logger.error("The FilePersistProcessor is not opened.");
            }
        }
        super.onBinaryMessage(session, in);
    }

    /**
     * {@inheritDoc}
     *
     * @see BaseWebsocket#onTextMessage(Session, String)
     */
    @Override
    public void onTextMessage(Session session, String message) {
        initBean();
        if (persistProcessor.isOpened()) {
            persistProcessor.command(message);
        } else {
            if (logger.isErrorEnabled()) {
                logger.error("The FilePersistProcessor is not opened.");
            }
        }
        super.onTextMessage(session, message);
    }
}
