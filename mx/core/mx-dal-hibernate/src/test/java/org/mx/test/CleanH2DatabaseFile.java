package org.mx.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.FileUtils;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * 删除并清理H2数据库文件，便于准备单元测试环境
 *
 * @author : john.peng created on date : 2017/11/28
 */
public class CleanH2DatabaseFile {
    private static final Log logger = LogFactory.getLog(CleanH2DatabaseFile.class);

    public static void cleanDataFile(String path) {
        try {
            FileUtils.deleteFile(Paths.get(System.getProperty("user.dir"), String.format("%s.mv.db", path)));
        } catch (IOException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
        }
    }
}
