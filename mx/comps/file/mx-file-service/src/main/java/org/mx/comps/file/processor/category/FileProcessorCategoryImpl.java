package org.mx.comps.file.processor.category;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.comps.file.FileReadProcessor;
import org.mx.comps.file.FileServiceDescriptor;
import org.mx.comps.file.FileWriteProcessor;
import org.mx.comps.file.processor.AbstractFileServiceDescriptor;
import org.mx.comps.file.processor.RandomAccessFileProcessor;
import org.mx.comps.file.processor.simple.FileServiceSimpleDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

/**
 * 一个按照分级分类实现的文件持久化实现类。
 *
 * @author : john.peng created on date : 2017/12/11
 */
@Component("categoryFilePersistProcessor")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FileProcessorCategoryImpl extends RandomAccessFileProcessor {
    public static final String CATEGORY_FILE_ROOT = "file.category.root";
    public static final String CATEGORY_FILE_HASHED = "file.category.hash";
    public static final String CATEGORY_DIR_LEVELS = "file.category.hash.levels";
    public static final String CATEGORY_NUM_PER_LEVEL = "file.category.hash.numPerLevel";
    private static final Log logger = LogFactory.getLog(FileProcessorCategoryImpl.class);

    @Autowired
    private Environment env = null;

    /**
     * 默认的构造函数
     */
    public FileProcessorCategoryImpl() {
        super();
    }

    /**
     * 初始化随机存取文件对象
     *
     * @param categories 文件分级分类
     * @param filename   文件名
     * @param mode       文件操作模式
     * @param offset     操作偏移量
     */
    protected void initRandomAccessFile(List<String> categories, String filename, String mode, long offset) {
        boolean hashed = env.getProperty(CATEGORY_FILE_HASHED, Boolean.class, true);
        FileServiceDescriptor fileServiceDescriptor = new FileServiceCategoryDescriptor(categories, filename);
        String root = env.getProperty(CATEGORY_FILE_ROOT, String.class, System.getProperty("user.dir"));
        ((AbstractFileServiceDescriptor) fileServiceDescriptor).setRoot(root);
        if (hashed) {
            int categoryLevels = env.getProperty(CATEGORY_DIR_LEVELS, Integer.class, 3);
            int categoryNumPerLevel = env.getProperty(CATEGORY_NUM_PER_LEVEL, Integer.class, 1000);
            fileServiceDescriptor = ((FileServiceSimpleDescriptor) fileServiceDescriptor).createHashDescriptor(
                    fileServiceDescriptor, root, categoryLevels, categoryNumPerLevel);
        }
        super.fileServiceDescriptor = fileServiceDescriptor;
        super.initRandomAccessFile(root, mode, offset);
    }

    /**
     * {@inheritDoc}
     *
     * @see FileReadProcessor#init(Object)
     */
    @Override
    public void init(Object initReq) {
        if (isOpened()) {
            if (logger.isErrorEnabled()) {
                logger.error("The File is opened, please not reopen it.");
            }
            return;
        }
        List<String> categories = null;
        String filename = null;
        if (initReq instanceof HttpServletRequest) {
            HttpServletRequest req = (HttpServletRequest) initReq;
            if (req.getMethod().equalsIgnoreCase("get")) {
                String str = req.getParameter("categories");
                categories = Arrays.asList(StringUtils.split(str));
                filename = req.getParameter("filename");
            } else if (req.getMethod().equalsIgnoreCase("post")) {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()));
                    String str = reader.readLine();
                    if (logger.isDebugEnabled()) {
                        logger.debug(String.format("Post data: %s.", str));
                    }
                    JSONObject json = JSON.parseObject(str);
                    categories = json.getObject("categories", List.class);
                    filename = json.getString("filename");
                } catch (IOException ex) {
                    if (logger.isErrorEnabled()) {
                        logger.error(ex);
                    }
                }
            } else {
                throw new UnsupportedOperationException(String.format("Unsupported method: %s.", req.getMethod()));
            }
        } else {
            // TODO 可能根据需要添加新的初始化参数对象
            throw new UnsupportedOperationException(String.format("Unsupported init parameter object: %s.",
                    initReq.getClass().getName()));
        }
        if (categories == null || categories.isEmpty()) {
            categories = Arrays.asList("category");
        }
        if (StringUtils.isBlank(filename)) {
            throw new IllegalArgumentException("Filename is blank.");
        }
        initRandomAccessFile(categories, filename, "r", -1l);
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
                    List<String> categories = json.getObject("categories", List.class);
                    if (categories == null || categories.isEmpty()) {
                        categories = Arrays.asList("category");
                    }
                    String filename = json.getString("filename");
                    if (StringUtils.isBlank(filename)) {
                        if (logger.isErrorEnabled()) {
                            logger.error(String.format("Filename is blank, command data: %s.", json.toJSONString()));
                        }
                        break;
                    }
                    long offset = json.getLongValue("offset");
                    initRandomAccessFile(categories, filename, "rw", offset);
                    if (super.fileServiceListener != null) {
                        // start
                        super.fileServiceListener.started(super.fileServiceDescriptor);
                    }
                    break;
                case "finish":
                    super.finishCmd();
                    break;
                case "cancel":
                    String root = env.getProperty(CATEGORY_FILE_ROOT, String.class, System.getProperty("user.dir"));
                    super.cancelCmd(root);
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
