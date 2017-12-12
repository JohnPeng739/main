package org.mx.comps.file.processor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.DigestUtils;
import org.mx.StringUtils;
import org.mx.comps.file.FileServiceDescriptor;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * 抽象的文件描述类定义。
 *
 * @author : john.peng created on date : 2017/12/09
 */
public abstract class AbstractFileServiceDescriptor implements FileServiceDescriptor {
    private static final Log logger = LogFactory.getLog(AbstractFileServiceDescriptor.class);

    protected String root;

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
            return getFile().getCanonicalPath().replace('\\', '/')
                    .substring(root.length() + 1);
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
        return getFile().getParent().replace('\\', '/')
                .substring(root.length() + 1);
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

    /**
     * 设置文件操作的根路径
     *
     * @param root 根路径
     */
    public void setRoot(String root) {
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
     * 获取关联的文件对象
     *
     * @return 文件对象
     */
    protected abstract File getFile();
}
