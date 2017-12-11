package org.mx.comps.file.processor.simple;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.comps.file.FileReadProcessor;
import org.mx.comps.file.FileServiceDescriptor;
import org.mx.comps.file.FileWriteProcessor;
import org.mx.comps.file.processor.RandomAccessFileProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.UUID;

/**
 * 一个简单的文件持久化实现类，直接根据指定的目录和文件名进行存储。
 *
 * @author : john.peng created on date : 2017/12/04
 */
@Component("simpleFilePersistProcessor")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FileProcessorSimpleImpl extends RandomAccessFileProcessor {
    public static final String FILE_ROOT = "file.simple.root";
    public static final String FILE_HASHED = "file.simple.hash";
    public static final String DIR_LEVELS = "file.simple.hash.levels";
    public static final String NUM_PER_LEVEL = "file.simple.hash.numPerLevel";
    private static final Log logger = LogFactory.getLog(FileProcessorSimpleImpl.class);

    @Autowired
    private Environment env = null;

    /**
     * 默认的构造函数
     */
    public FileProcessorSimpleImpl() {
        super();
    }

    /**
     * 初始化随机存取文件对象
     *
     * @param mode   文件操作模式
     * @param offset 操作偏移量
     */
    protected void initRandomAccessFile(String directory, String filename, String mode, long offset) {
        boolean hashed = env.getProperty(FILE_HASHED, Boolean.class, true);
        FileServiceDescriptor fileServiceDescriptor = new FileServiceSimpleDescriptor(directory, filename);
        String root = env.getProperty(FILE_ROOT, String.class, System.getProperty("user.dir"));
        ((FileServiceSimpleDescriptor) fileServiceDescriptor).setRoot(root);
        if (hashed) {
            int levels = env.getProperty(DIR_LEVELS, Integer.class, 3);
            int numPerLevel = env.getProperty(NUM_PER_LEVEL, Integer.class, 1000);
            fileServiceDescriptor = ((FileServiceSimpleDescriptor)fileServiceDescriptor).createHashDescriptor(
                    fileServiceDescriptor, levels, numPerLevel);
            ((FileServiceSimpleDescriptor) fileServiceDescriptor).setRoot(root);
        }
        super.fileServiceDescriptor = fileServiceDescriptor;
        super.initRandomAccessFile(root, mode, offset);
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
        String directory = null, filename = null;
        if (req.getMethod().equalsIgnoreCase("get")) {
            directory = req.getParameter("directory");
            filename = req.getParameter("filename");
        } else if(req.getMethod().equalsIgnoreCase("post")) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()));
                String str = reader.readLine();
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Post data: %s.", str));
                }
                JSONObject json = JSON.parseObject(str);
                directory = json.getString("directory");
                filename = json.getString("filename");
            } catch (IOException ex) {
                if (logger.isErrorEnabled()) {
                    logger.error(ex);
                }
            }
        } else {
            throw new UnsupportedOperationException(String.format("Unsupported method: %s.", req.getMethod()));
        }
        if (StringUtils.isBlank(directory)) {
            directory = "./";
        }
        if (StringUtils.isBlank(filename)) {
            throw new IllegalArgumentException("Filename is blank.");
        }
        initRandomAccessFile(directory, filename, "r", -1l);
    }

    /**
     * {@inheritDoc}
     *
     * @see FileWriteProcessor#command(JSONObject)
     */
    @Override
    public void command(JSONObject json) throws Exception {
        String command = json.getString("command");
        try {
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
                    initRandomAccessFile(directory, filename, "rw", offset);
                    if (super.fileServiceListener != null) {
                        // start
                        super.fileServiceListener.started(super.fileServiceDescriptor);
                    }
                    break;
                case "finish":
                    if (super.isOpened()) {
                        close();
                        if (super.fileServiceListener != null) {
                            // finish
                            super.fileServiceListener.finished(super.fileServiceDescriptor);
                        }
                    } else {
                        throw new Exception("The session is closed.");
                    }
                    break;
                case "cancel":
                    if (super.isOpened()) {
                        close();
                        // 删除文件
                        String root = env.getProperty(FILE_ROOT, String.class, System.getProperty("user.dir"));
                        File deleteFile = new File(root, super.fileServiceDescriptor.getPath());
                        if (deleteFile.exists() && deleteFile.isFile()) {
                            deleteFile.delete();
                        }
                        if (super.fileServiceListener != null) {
                            // cancel
                            super.fileServiceListener.canceled(super.fileServiceDescriptor);
                        }
                    } else {
                        throw new Exception("The session is closed.");
                    }
                    break;
                default:
                    throw new Exception(String.format("Unsupported command: %s.", command));
            }
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("invoke command[%s] fail.", command), ex);
            }
            if (super.fileServiceListener != null) {
                super.fileServiceListener.errored(super.fileServiceDescriptor, ex.getMessage());
            }
            throw ex;
        }
    }
}
