package org.mx.comps.file.processor.category;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.comps.file.processor.AbstractFileServiceDescriptor;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * 采用分级分类方式描述的文件描述符
 * <p>
 * 默认采用分级分类作为目录名
 *
 * @author : john.peng created on date : 2017/12/11
 */
public class FileServiceCategoryDescriptor extends AbstractFileServiceDescriptor {
    private static final Log logger = LogFactory.getLog(FileServiceCategoryDescriptor.class);

    private String filename;
    private List<String> categories;

    public FileServiceCategoryDescriptor(List<String> categories, String filename) {
        super();
        this.setPath(categories, filename);
    }

    /**
     * 设置路径
     *
     * @param categories 分级分类列表
     * @param filename   文件名
     */
    protected void setPath(List<String> categories, String filename) {
        if (categories == null || categories.size() <= 0) {
            categories = Arrays.asList("category");
        }
        if (StringUtils.isBlank(filename)) {
            filename = "test.txt";
        }
        this.categories = categories;
        this.filename = filename;
    }

    /**
     * {@inheritDoc}
     *
     * @see AbstractFileServiceDescriptor#getFile()
     */
    @Override
    protected File getFile() {
        String directory = StringUtils.merge(categories, "/");
        File file = new File(super.root, directory);
        return new File(file, filename);
    }
}
