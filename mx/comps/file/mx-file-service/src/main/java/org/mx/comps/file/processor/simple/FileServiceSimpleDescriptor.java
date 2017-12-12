package org.mx.comps.file.processor.simple;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.comps.file.FileServiceDescriptor;
import org.mx.comps.file.processor.AbstractFileServiceDescriptor;

import java.io.File;
import java.util.Arrays;
import java.util.UUID;

/**
 * 一个指定了存储目录和文件名的简单文件服务描述定义类。
 *
 * @author : john.peng created on date : 2017/12/09
 */
public class FileServiceSimpleDescriptor extends AbstractFileServiceDescriptor {
    private static final Log logger = LogFactory.getLog(FileServiceSimpleDescriptor.class);

    private String directory, filename;

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
     * 根据一个现有的文件描述对象创建一个新的哈希表述的文件描述对象
     *
     * @param descriptor  原有的文件描述对象
     * @param root        文件存储根目录
     * @param levels      哈希目录级数
     * @param numPerLevel 每级目录哈希数量
     * @return 哈希后的文件描述符
     */
    public FileServiceSimpleDescriptor createHashDescriptor(FileServiceDescriptor descriptor, String root, int levels, int numPerLevel) {
        String originPath = descriptor.getPath();
        int hashValue = originPath.hashCode();
        long originValue;
        if (hashValue <= 0) {
            originValue = hashValue + Integer.MAX_VALUE;
        } else {
            originValue = hashValue;
        }
        long dirHash = originValue % (long) ((Math.pow(numPerLevel, levels)));
        long[] dirHashs = new long[levels];
        Arrays.fill(dirHashs, 0);
        for (int index = 0; index < levels && dirHash > 0; index++) {
            long hash = dirHash % numPerLevel;
            dirHashs[levels - 1 - index] = hash;
            dirHash = dirHash / numPerLevel;
        }
        String[] dirHashStrs = new String[dirHashs.length];
        String format = String.format("%%0%dd", String.valueOf(numPerLevel).length());
        for (int index = 0; index < dirHashs.length; index++) {
            dirHashStrs[index] = String.format(format, dirHashs[index]);
        }
        directory = String.format("%s", StringUtils.merge(dirHashStrs, "/"));
        filename = UUID.nameUUIDFromBytes(originPath.getBytes()).toString();
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Create a hash path, origin: %s, hash: %s/%s.",
                    originPath, directory, filename));
        }
        FileServiceSimpleDescriptor fileServiceSimpleDescriptor = new FileServiceSimpleDescriptor(directory, filename);
        fileServiceSimpleDescriptor.setRoot(root);
        return fileServiceSimpleDescriptor;
    }

    /**
     * {@inheritDoc}
     *
     * @see AbstractFileServiceDescriptor#getFile()
     */
    @Override
    protected File getFile() {
        File file = new File(super.root, directory);
        return new File(file, filename);
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
}
