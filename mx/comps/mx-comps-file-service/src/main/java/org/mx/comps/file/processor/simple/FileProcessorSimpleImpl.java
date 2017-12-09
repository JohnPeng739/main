package org.mx.comps.file.processor.simple;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.comps.file.FileReadProcessor;
import org.mx.comps.file.FileWriteProcessor;
import org.mx.comps.file.processor.RandomAccessFileProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
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
    public static final String FILE_HASHED = "file.simple.hased";
    private static final Log logger = LogFactory.getLog(FileProcessorSimpleImpl.class);

    @Autowired
    private Environment env = null;

    /**
     * 默认的构造函数
     */
    public FileProcessorSimpleImpl() {
        super();
    }

    private String createHashPath(String originPath) {
        // 根据哈希规则将路径分为二级目录，可以总共管理26*26*26*26=456976个子目录
        // 每层目录根据文件逻辑路径哈希，总共26*26个目录，文件名根据命名的UUID规则生成。
        int hashValue = originPath.hashCode();
        long originValue = 0l;
        if (hashValue <= 0) {
            originValue = hashValue + Integer.MAX_VALUE;
        } else {
            originValue = hashValue;
        }
        long dirHash = originValue % (26 * 26 * 26 * 26);
        long dirHash1 = dirHash / (26 * 26);
        long dirHash2 = dirHash - dirHash1 * (26 * 26);
        String hashPath = String.format("%03d/%03d/%s", dirHash1, dirHash2,
                UUID.nameUUIDFromBytes(originPath.getBytes()));
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Create a hash path, origin: %s, hash: %s.", originPath, hashPath));
        }
        return hashPath;
    }

    /**
     * 初始化随机存取文件对象
     *
     * @param mode   文件操作模式
     * @param offset 操作偏移量
     */
    protected void initRandomAccessFile(File file, String mode, long offset) {
        Assert.notNull(file, "File is null.");
        boolean hashed = env.getProperty(FILE_HASHED, Boolean.class, true);
        if (hashed) {
            String hashPath = createHashPath(file.getAbsolutePath());
            file = new File(hashPath);
        }
        super.fileServiceDescriptor = new FileServiceSimpleDescriptor(file);

        String root = env.getProperty(FILE_ROOT, String.class, System.getProperty("user.dir"));
        ((FileServiceSimpleDescriptor)super.fileServiceDescriptor).root = root;
        file = new File(root, file.getAbsolutePath());
        try {
            if (!file.getParentFile().exists()) {
                // 如果父级目录不存在，则创建父级目录。
                file.getParentFile().mkdirs();
            }
            super.randomAccessFile = new RandomAccessFile(file, mode);
            if (offset > 0) {
                // 如果指定了偏移量，则预先设置操作偏移量。
                super.randomAccessFile.seek(offset);
            }
            super.opened = true;
        } catch (IOException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
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
        initRandomAccessFile(file, "r", -1l);
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
                    initRandomAccessFile(file, "rw", offset);
                    if (super.fileServiceListener != null) {
                        // start
                        super.fileServiceListener.started(super.fileServiceDescriptor);
                    }
                    break;
                case "finish":
                    close();
                    if (super.fileServiceListener != null) {
                        // finish
                        super.fileServiceListener.finished(super.fileServiceDescriptor);
                    }
                    break;
                case "cancel":
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
}
