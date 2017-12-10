package org.mx.comps.file.processor.simple;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.DigestUtils;
import org.mx.StringUtils;
import org.mx.comps.file.FileServiceDescriptor;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * 一个指定了存储目录和文件名的简单文件服务描述定义类。
 *
 * @author : john.peng created on date : 2017/12/09
 */
public class FileServiceSimpleDescriptor implements FileServiceDescriptor {
    private static final Log logger = LogFactory.getLog(FileServiceSimpleDescriptor.class);

    private String root, directory, filename;

    /**
     * 默认的构造函数
     */
    public FileServiceSimpleDescriptor() {
        this(null, null);
    }

    /**
     * 默认的构造函数
     *
     * @param directory 文件的目录
     * @param filename  文件名
     */
    public FileServiceSimpleDescriptor(String directory, String filename) {
        super();
        setPath(directory, filename);
    }

    /**
     * 获取关联的文件对象
     *
     * @return 文件对象
     */
    private File getFile() {
        File file = new File(root, directory);
        return new File(file, filename);
    }

    /**
     * 设置文件操作的根路径
     *
     * @param root 根路径
     */
    protected void setRoot(String root) {
        if (StringUtils.isBlank(root)) {
            root = System.getProperty("user.dir");
        }
        try {
            this.root = new File(root).getCanonicalPath();
        } catch (IOException ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Set the root fail.", ex);
            }
        }
    }

    /**
     * 设置路径
     *
     * @param directory 目录
     * @param filename  文件名
     */
    protected void setPath(String directory, String filename) {
        if (StringUtils.isBlank(directory)) {
            directory = "./";
        }
        if (StringUtils.isBlank(filename)) {
            filename = "test.txt";
        }
        this.directory = directory;
        this.filename = filename;
    }

    /**
     * {@inheritDoc}
     *
     * @see FileServiceDescriptor#getId()
     */
    @Override
    public String getId() {
        String filePath = getPath();
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
        try {
            return getFile().getCanonicalPath().replace('\\', '/').substring(root.length() + 1);
        } catch (IOException ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Get file path fail.", ex);
            }
            return null;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see FileServiceDescriptor#getParentPath()
     */
    @Override
    public String getParentPath() {
        return getFile().getParent().replace('\\', '/').substring(root.length() + 1);
    }

    /**
     * {@inheritDoc}
     *
     * @see FileServiceDescriptor#getFilename()
     */
    @Override
    public String getFilename() {
        return getFile().getName();
    }

    /**
     * {@inheritDoc}
     *
     * @see FileServiceDescriptor#getLength()
     */
    @Override
    public long getLength() {
        File file = getFile();
        if (file.exists() && file.isFile()) {
            return file.length();
        } else {
            return -1l;
        }
    }
}
