package org.mx.comps.file.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.mx.StringUtils;
import org.mx.comps.file.FileReadProcessor;
import org.mx.comps.file.processor.ProcessorFactory;
import org.mx.service.server.websocket.DefaultWsSessionMonitor;
import org.mx.service.server.websocket.WsSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 描述： 基于Websocket的文件下载服务
 *
 * @author John.Peng
 *         Date time 2018/3/11 下午1:19
 */
@Component("fileDownloadWebsocket")
public class FileDownloadWebsocket extends DefaultWsSessionMonitor {
    public static final String FILE_DOWNLOAD_URI_PATH = "/wsdownload";
    public static final String CLOSE_FILE_FOR_ERROR = "file.error.close";
    private static final Log logger = LogFactory.getLog(FileDownloadWebsocket.class);
    @Autowired
    private Environment env = null;
    @Autowired
    private ProcessorFactory processorFactory = null;
    @Autowired
    private WsSessionManager manager = null;

    private FileReadProcessor readProcessor = null;
    private Timer downloadTimer = null;

    public FileDownloadWebsocket() {
        super(FILE_DOWNLOAD_URI_PATH);
    }

    /**
     * {@inheritDoc}
     *
     * @see DefaultWsSessionMonitor#beforeClose(String)
     */
    @Override
    public void beforeClose(String connectKey) {
        if (readProcessor != null && readProcessor.isOpened()) {
            readProcessor.close();
            readProcessor = null;
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
        if (closeFileForError && readProcessor != null) {
            readProcessor.close();
            readProcessor = null;
        }
        super.hasError(connectKey, throwable);
    }

    /**
     * {@inheritDoc}
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
                String type = json.getString("type");
                JSONObject initReq = json.getJSONObject("params");
                readProcessor = processorFactory.createReadProcessor(ProcessorFactory.ProcessorType.valueOf(type));
                readProcessor.init(initReq);
                if (logger.isDebugEnabled()) {
                    logger.debug("Initialize download process successfully.");
                }
                downloadTimer = new Timer();
                // 延时200ms开始执行。
                Session session = manager.getSession(connectKey);
                downloadTimer.schedule(new DownloadTask(session), 200);
            } else if ("cancel".equalsIgnoreCase(command)) {
                if (downloadTimer != null) {
                    downloadTimer.cancel();
                    downloadTimer.purge();
                    downloadTimer = null;
                }
                if (readProcessor != null) {
                    readProcessor.close();
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("Cancel the download process.");
                }
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
                logger.error("Send file download response fail.", ex);
            }
        }
        super.hasText(connectKey, message);
    }

    /**
     * 文件下载的任务
     */
    private class DownloadTask extends TimerTask {
        private Session session = null;

        public DownloadTask(Session session) {
            super();
            this.session = session;
        }

        /**
         * {@inheritDoc}
         *
         * @see TimerTask#run()
         */
        @Override
        public void run() {
            int len, total = 0;
            byte[] buffer = new byte[4096];
            if (logger.isDebugEnabled()) {
                logger.debug("Start send binary data to session.");
            }
            do {
                len = readProcessor.read(buffer);
                total += len;
                if (len > 0) {
                    try {
                        session.getRemote().sendBytes(ByteBuffer.wrap(buffer, 0, len));
                    } catch (IOException ex) {
                        if (logger.isErrorEnabled()) {
                            logger.error("Send binary data to session fail.", ex);
                        }
                    }
                }
            } while (len == buffer.length);
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Send binary data to session successfully, total: %d bytes.", total));
            }
            // 传输完毕，清理资源
            readProcessor.close();
            readProcessor = null;
            downloadTimer = null;
        }
    }
}
