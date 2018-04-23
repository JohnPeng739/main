package org.mx.hanlp.factory.suggest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.TypeUtils;
import org.mx.error.UserInterfaceSystemErrorException;
import org.mx.hanlp.ItemSuggester;
import org.springframework.core.env.Environment;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * 描述： CSV（半角逗号分割）的文本文件推荐数据提供器，默认第一列为条目对应的ID， 第二列为推荐内容，后续列会被忽略。<br>
 * 可以通过以下配置修改默认行为：<br>
 * prefix.fields.id=2        # 表示第二列为条目对应的ID<br>
 * prefix.fields.content=5   # 表示第五列为条目的内容
 *
 * @author John.Peng
 *         Date time 2018/4/16 下午7:03
 */
public class CsvFileProvider implements SuggestContentProvider {
    private static final Log logger = LogFactory.getLog(CsvFileProvider.class);

    private String path = null;
    private int idFiled = 1, contentField = 2;

    /**
     * {@inheritDoc}
     *
     * @see SuggestContentProvider#initEnvironment(Environment, String)
     */
    @Override
    public void initEnvironment(Environment env, String prefix) {
        path = env.getProperty(String.format("%s.path", prefix));
        if (!Files.exists(Paths.get(path))) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("The file[%s] not existed.", path));
            }
            throw new UserInterfaceSystemErrorException(UserInterfaceSystemErrorException.SystemErrors.FILE_NOT_EXISTED);
        }
        idFiled = env.getProperty(String.format("%s.fields.id", prefix), Integer.class, 1);
        if (idFiled <= 0) {
            if (logger.isWarnEnabled()) {
                logger.warn(String.format("The id field's index[%d] is invalid, will replace by 1.", idFiled));
            }
            idFiled = 1;
        }
        contentField = env.getProperty(String.format("%s.fields.content", prefix), Integer.class, 2);
        if (contentField <= 0) {
            if (logger.isWarnEnabled()) {
                logger.warn(String.format("The content field's index[%d] is invalid, will replace by 2.", contentField));
            }
            idFiled = 2;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see SuggestContentProvider#loadSuggestContent(ItemSuggester)
     */
    @Override
    public long loadSuggestContent(ItemSuggester itemSuggester) {
        try (BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(path)))) {
            String line;
            long total = 0;
            do {
                line = bf.readLine();
                if (StringUtils.isBlank(line)) {
                    break;
                }
                List<String> segs = TypeUtils.csv2List(line);
                if (idFiled > segs.size() || contentField > segs.size()) {
                    if (logger.isWarnEnabled()) {
                        logger.warn(String.format("The line has not contain the id or content field, line: %s, " +
                                "id field: %d, content field: %d.", line, idFiled, contentField));
                    }
                    continue;
                }
                String id = segs.get(idFiled - 1), content = segs.get(contentField - 1);
                itemSuggester.addSuggestItem(ItemSuggester.SuggestItem.valueOf(itemSuggester.getType(), id, content));
                total ++;
            } while (true);
            if (logger.isInfoEnabled()) {
                logger.info(String.format("Load the csv file[%s] into %s suggester successfully.", path,
                        itemSuggester.getType()));
            }
            return total;
        } catch (IOException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Read csv file[%s] fail.", path));
            }
            throw new UserInterfaceSystemErrorException(UserInterfaceSystemErrorException.SystemErrors.FILE_READ_ERROR);
        }
    }
}
