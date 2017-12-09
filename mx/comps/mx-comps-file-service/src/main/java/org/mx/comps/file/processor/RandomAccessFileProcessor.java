package org.mx.comps.file.processor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.comps.file.FileReadProcessor;
import org.mx.comps.file.FileServiceDescriptor;
import org.mx.comps.file.FileServiceListener;
import org.mx.comps.file.FileWriteProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

/**
 * 一个简单的文件持久化实现类，直接根据指定的目录和文件名进行存储。
 *
 * @author : john.peng created on date : 2017/12/04
 */
@Component("simpleFilePersistProcessor")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class RandomAccessFileProcessor implements FileWriteProcessor, FileReadProcessor {
    private static final Log logger = LogFactory.getLog(RandomAccessFileProcessor.class);

    protected boolean opened = false;
    protected RandomAccessFile randomAccessFile = null;

    protected FileServiceListener fileServiceListener = null;

    protected FileServiceDescriptor fileServiceDescriptor = null;

    /**
     * 默认的构造函数
     */
    public RandomAccessFileProcessor() {
        super();
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
     *
     * @see FileWriteProcessor#setFileServiceListener(FileServiceListener)
     */
    @Override
    public void setFileServiceListener(FileServiceListener listener) {
        fileServiceListener = listener;
    }

    /**
     * {@inheritDoc}
     *
     * @see FileReadProcessor#getFileServiceDescriptor()
     * @see FileWriteProcessor#getFileServiceDescriptor()
     */
    @Override
    public FileServiceDescriptor getFileServiceDescriptor() {
        return fileServiceDescriptor;
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
     * @see FileWriteProcessor#write(InputStream)
     */
    @Override
    public void write(InputStream in) {
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
