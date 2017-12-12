package org.mx.comps.file.processor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.comps.file.FileReadProcessor;
import org.mx.comps.file.FileWriteProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * 处理器工厂类
 *
 * @author : john.peng created on date : 2017/12/05
 */
@Component("fileServiceProcessorFactory")
public class ProcessorFactory {
    private static final Log logger = LogFactory.getLog(ProcessorFactory.class);

    private static ProcessorFactory factory = null;

    @Autowired
    private ApplicationContext context = null;

    /**
     * 创建一个读文件的处理器的工厂方法
     *
     * @param type 处理器类型
     * @return 创建好的处理器对象，如果返回null，表示类型暂不支持。
     */
    public FileReadProcessor createReadProcessor(ProcessorType type) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Will create a %s FileReadProcessor.", type));
        }
        switch (type) {
            case simple:
                return context.getBean("simpleFilePersistProcessor", FileReadProcessor.class);
            default:
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Unsupported type: %s.", type));
                }
                return null;
        }
    }

    /**
     * 创建一个写文件的处理器的工厂方法
     *
     * @param type 处理器类型
     * @return 创建好的处理器对象，如果返回null，表示类型暂不支持。
     */
    public FileWriteProcessor createWriteProcessor(ProcessorType type) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Will create a %s FileReadProcessor.", type));
        }
        switch (type) {
            case simple:
                return context.getBean("simpleFilePersistProcessor", FileWriteProcessor.class);
            default:
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Unsupported type: %s.", type));
                }
                return null;
        }
    }

    /**
     * 支持的处理器类型
     */
    public enum ProcessorType {
        /**
         * 简单文件处理类型，提供目录和文件名
         */
        simple
    }
}
