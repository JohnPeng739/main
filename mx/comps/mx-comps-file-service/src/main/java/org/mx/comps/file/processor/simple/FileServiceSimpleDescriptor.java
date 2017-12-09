package org.mx.comps.file.processor.simple;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.DigestUtils;
import org.mx.StringUtils;
import org.mx.comps.file.FileServiceDescriptor;

import java.io.File;
import java.security.NoSuchAlgorithmException;

/**
 * 一个指定了存储目录和文件名的简单文件服务描述定义类。
 *
 * @author : john.peng created on date : 2017/12/09
 */
public class FileServiceSimpleDescriptor implements FileServiceDescriptor {
    private static final Log logger = LogFactory.getLog(FileServiceSimpleDescriptor.class);

    protected String root;
    private File file;

    /**
     * 默认的构造函数
     */
    public FileServiceSimpleDescriptor() {
        this(null, null);
    }

    /**
     * 默认的构造函数
     * @param parent 文件的目录
     * @param filename 文件名
     */
    public FileServiceSimpleDescriptor(String parent, String filename) {
        super();
        if (StringUtils.isBlank(parent)) {
            parent = "./";
        }
        if (StringUtils.isBlank(filename)) {
            filename = "test.txt";
        }
        this.file = new File(parent, filename);
    }

    /**
     * 默认的构造函数
     * @param file 文件对象
     */
    public FileServiceSimpleDescriptor(File file) {
        this();
        this.file = file;
    }

    /**
     * 设置文件对象
     * @param file 文件
     */
    protected void setFile(File file) {
        this.file = file;
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
        return file.getAbsolutePath();
    }

    /**
     * {@inheritDoc}
     *
     * @see FileServiceDescriptor#getParentPath()
     */
    @Override
    public String getParentPath() {
        return file.getParent();
    }

    /**
     * {@inheritDoc}
     *
     * @see FileServiceDescriptor#getFilename()
     */
    @Override
    public String getFilename() {
        return file.getName();
    }

    /**
     * {@inheritDoc}
     *
     * @see FileServiceDescriptor#getLength()
     */
    @Override
    public long getLength() {
        File file = new File(root, this.file.getAbsolutePath());
        if (file.exists() && file.isFile()) {
            return file.length();
        } else {
            return -1l;
        }
    }
}
