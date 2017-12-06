package org.mx.comps.file.processor.simple;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.DigestUtils;
import org.mx.StringUtils;
import org.mx.comps.file.FileReadProcessor;
import org.mx.comps.file.FileServiceDescriptor;
import org.mx.comps.file.FileServiceListener;
import org.mx.comps.file.FileWriteProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.security.NoSuchAlgorithmException;

/**
 * 一个简单的文件持久化实现类，直接根据指定的目录和文件名进行存储。
 *
 * @author : john.peng created on date : 2017/12/04
 */
@Component("simpleFilePersistProcessor")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FileWriteProcessorImpl implements FileServiceDescriptor, FileWriteProcessor, FileReadProcessor {
    public static final String FILE_ROOT = "file.simple.root";
    private static final Log logger = LogFactory.getLog(FileWriteProcessorImpl.class);
    private String filePath;
    private String filename;
    private boolean opened = false;

    private RandomAccessFile randomAccessFile = null;

    @Autowired
    private Environment env = null;

    private FileServiceListener fileServiceListener = null;

    public FileWriteProcessorImpl() {
        super();
    }

    /**
     * {@inheritDoc}
     *
     * @see FileWriteProcessor#getLength() ()
     */
    @Override
    public long getLength() {
        try {
            if (isOpened()) {
                return randomAccessFile.length();
            }
        } catch (IOException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
        }
        return 0l;
    }

    /**
     * {@inheritDoc}
     *
     * @see FileWriteProcessor#getFilename()
     */
    @Override
    public String getFilename() {
        return filename;
    }

    /**
     * {@inheritDoc}
     *
     * @see FileWriteProcessor#isOpened()
     */
    @Override
    public boolean isOpened() {
        return opened && randomAccessFile != null;
    }

    /**
     * {@inheritDoc}
     * @see FileWriteProcessor#setFileServiceListener(FileServiceListener)
     */
    @Override
    public void setFileServiceListener(FileServiceListener listener) {
        fileServiceListener = listener;
    }

    /**
     * {@inheritDoc}
     *
     * @see FileWriteProcessor#close()
     */
    @Override
    public void close() {
        if ((randomAccessFile != null && !opened) || (randomAccessFile == null && opened)) {
            if (logger.isWarnEnabled()) {
                logger.warn("The File operate state is out of sync.");
            }
        }
        if (randomAccessFile != null) {
            try {
                randomAccessFile.close();
            } catch (IOException ex) {
                if (logger.isErrorEnabled()) {
                    logger.error(ex);
                }
            }
            randomAccessFile = null;
        }
        if (opened) {
            opened = false;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see FileReadProcessor#init(HttpServletRequest)
     */
    @Override
    public void init(HttpServletRequest req) {
        if (isOpened()) {
            if (logger.isErrorEnabled()) {
                logger.error("The File is opened, please not reopen it.");
            }
            return;
        }
        String root = env.getProperty(FILE_ROOT, String.class, System.getProperty("user.dir"));
        String directory = req.getParameter("directory");
        if (StringUtils.isBlank(directory)) {
            directory = "./";
        }
        String filename = req.getParameter("filename");
        if (StringUtils.isBlank(filename)) {
            if (logger.isErrorEnabled()) {
                logger.error("Filename is blank");
            }
            return;
        }
        File file = new File(directory, filename);
        filePath = file.getAbsolutePath();
        this.filename = file.getName();
        file = new File(root, filePath);
        if (!file.exists() || !file.isFile()) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("File not exist or not a file: %s.", file.getAbsoluteFile()));
            }
            return;
        }
        try {
            randomAccessFile = new RandomAccessFile(file, "r");
            opened = true;
        } catch (IOException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see FileWriteProcessor#command(JSONObject)
     */
    @Override
    public void command(JSONObject json) {
        try {
            String command = json.getString("command");
            String root = env.getProperty(FILE_ROOT, String.class, System.getProperty("user.dir"));
            switch (command) {
                case "start":
                    String directory = json.getString("directory");
                    if (StringUtils.isBlank(directory)) {
                        directory = "./";
                    }
                    String filename = json.getString("filename");
                    if (StringUtils.isBlank(filename)) {
                        if (logger.isErrorEnabled()) {
                            logger.error(String.format("Filename is blank, command data: %s.", json.toJSONString()));
                        }
                        break;
                    }
                    long offset = json.getLongValue("offset");
                    File file = new File(directory, filename);
                    filePath = file.getAbsolutePath();
                    this.filename = file.getName();
                    file = new File(root, filePath);
                    file.getParentFile().mkdirs();
                    randomAccessFile = new RandomAccessFile(file, "rw");
                    randomAccessFile.seek(offset);
                    opened = true;
                    if (fileServiceListener != null) {
                        // start
                        fileServiceListener.started(this);
                    }
                    break;
                case "finish":
                    close();
                    if (fileServiceListener != null) {
                        // finish
                        fileServiceListener.finished(this);
                    }
                    break;
                case "cancel":
                    close();
                    // 删除文件
                    File deleteFile = new File(root, filePath);
                    if (deleteFile.exists() && deleteFile.isFile()) {
                        deleteFile.delete();
                    }
                    if (fileServiceListener != null) {
                        // cancel
                        fileServiceListener.canceled(this);
                    }
                    break;
                default:
                    if (logger.isErrorEnabled()) {
                        logger.error(String.format("Unsupported command: %s.", command));
                    }
                    break;
            }
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see FileServiceDescriptor#getId()
     */
    @Override
    public String getId() {
        try {
            return DigestUtils.md5(filePath);
        } catch (NoSuchAlgorithmException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            return filePath;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see FileServiceDescriptor#getPath()
     */
    @Override
    public String getPath() {
        return filePath;
    }

    /**
     * {@inheritDoc}
     *
     * @see FileReadProcessor#read(OutputStream)
     */
    @Override
    public void read(OutputStream out) {
        if (isOpened()) {
            byte[] buffer = new byte[8192];
            int len;
            try {
                randomAccessFile.seek(0l);
                while ((len = randomAccessFile.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                    out.flush();
                }
            } catch (IOException ex) {
                if (logger.isErrorEnabled()) {
                    logger.error("Read file's data fail.", ex);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see FileWriteProcessor#persist(InputStream)
     */
    @Override
    public void persist(InputStream in) {
        Assert.notNull(in, "The InputStream is null.");
        Assert.isTrue(isOpened(), "The Processor is not opened.");
        byte[] buffer = new byte[8192];
        int len;
        try {
            while ((len = in.read(buffer)) > 0) {
                randomAccessFile.write(buffer, 0, len);
            }
        } catch (IOException ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Write file's data fail.", ex);
            }
        }
    }
}
