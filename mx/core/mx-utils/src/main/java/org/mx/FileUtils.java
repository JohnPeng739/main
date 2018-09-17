package org.mx;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;

import static java.nio.file.FileVisitResult.CONTINUE;

/**
 * 文件处理工具类
 *
 * @author : john.peng date : 2017/10/15
 */
public class FileUtils {
    private static final Log logger = LogFactory.getLog(FileUtils.class);

    private FileUtils() {
        super();
    }

    public static String getType(String name) {
        return name.substring(name.lastIndexOf(".") + 1);
    }

    /**
     * 删除指定的文件或目录，如果是目录，将会删除子目录及其包含的文件。
     *
     * @param filePath 待删除的文件或目录对象
     * @throws IOException 删除过程中发生的异常
     * @see #deleteFile(File)
     */
    public static void deleteFile(String filePath) throws IOException {
        deleteFile(Paths.get(filePath));
    }

    /**
     * 删除指定的文件或目录，如果是目录，将会删除子目录及其包含的文件。
     *
     * @param file 待删除的文件或目录
     * @throws IOException 删除过程中发生的异常
     */
    public static void deleteFile(Path file) throws IOException {
        Files.walkFileTree(file, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException ex) throws IOException {
                if (ex == null) {
                    Files.delete(dir);
                    return CONTINUE;
                } else {
                    // directory iteration failed
                    if (logger.isErrorEnabled()) {
                        logger.error(String.format("Visit directory fail, path: %s.", dir.getFileName().toString()), ex);
                    }
                    throw ex;
                }
            }
        });
    }

    /**
     * 删除指定的文件或目录，如果是目录，将会删除子目录及其包含的文件。
     *
     * @param file 待删除的文件或目录对象
     * @deprecated 升级成Paths实现，未来版本可能删除此方法
     */
    public static void deleteFile(File file) {
        if (file == null) {
            if (logger.isWarnEnabled()) {
                logger.warn("The file object is null.");
            }
            return;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null) {
                if (logger.isWarnEnabled()) {
                    logger.warn(String.format("There has not any children in the path: %s.", file.getAbsolutePath()));
                }
                return;
            }
            if (files.length > 0) {
                for (File child : files) {
                    // 迭代处理子目录
                    deleteFile(child);
                    // 子目录处理完毕后，删除父目录
                    if (file.delete()) {
                        if (logger.isDebugEnabled()) {
                            logger.debug(String.format("Delete path successfully, path: %s.", file.getAbsolutePath()));
                        }
                    }
                }
            } else {
                // 空目录，直接删除
                if (file.delete()) {
                    if (logger.isDebugEnabled()) {
                        logger.debug(String.format("Delete path successfully, path: %s.", file.getAbsolutePath()));
                    }
                }
            }
        } else {
            // 文件，直接删除
            if (file.delete()) {
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Delete path successfully, path: %s.", file.getAbsolutePath()));
                }
            }
        }
    }

    /**
     * 将输入流中的数据保存到指定的目录中，并按照保存日期建立子目录，文件名采用UUID自动生成。
     *
     * @param filePath 目录
     * @param is       输入流
     * @return 保存的文件路径
     * @throws IOException 保存过程中发生的异常
     */
    public static String saveFile(String filePath, InputStream is) throws IOException {
        String date = DateUtils.get8TimeNow(), fileName = DigestUtils.uuid().replaceAll("-", "");
        Path path = Paths.get(filePath, date, fileName);
        Path parent = path.getParent();
        if (!Files.exists(parent)) {
            Files.createDirectories(parent);
        }
        Files.copy(is, path, StandardCopyOption.REPLACE_EXISTING);
        return path.toFile().getAbsolutePath();
    }

    /**
     * 将输入流中的数据保存到指定目录中的文件中
     *
     * @param filePath 目录
     * @param fileName 文件名
     * @param is       输入流
     * @return 保存的文件路径
     * @throws IOException 保存过程中发生的异常
     */
    public static String saveFile(String filePath, String fileName, InputStream is) throws IOException {
        Path path = Paths.get(fileName, fileName);
        Path parent = path.getParent();
        if (!Files.exists(parent)) {
            Files.createDirectories(parent);
        }
        Files.copy(is, path, StandardCopyOption.REPLACE_EXISTING);
        return path.toFile().getAbsolutePath();
    }

    /**
     * 复制指定的文件或目录
     *
     * @param source 源
     * @param target 目标
     * @throws IOException 复制过程中发生的异常
     */
    public static void copyFile(String source, String target) throws IOException {
        if (StringUtils.isBlank(source) || StringUtils.isBlank(target) || source.equals(target)) {
            throw new IllegalArgumentException(String.format("source: %s, target: %s. ", source, target));
        }
        Path path = Paths.get(target);
        Path parent = path.getParent();
        if (!Files.exists(parent)) {
            Files.createDirectories(parent);
        }
        copyFile(Paths.get(source), path);
    }

    /**
     * 复制指定的的文件或目录
     *
     * @param source 源
     * @param target 目标
     * @throws IOException 复制过程中发生的异常
     */
    public static void copyFile(Path source, Path target) throws IOException {
        Files.walkFileTree(source, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE,
                new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                            throws IOException {
                        Path targetdir = target.resolve(source.relativize(dir));
                        try {
                            Files.copy(dir, targetdir);
                        } catch (FileAlreadyExistsException ex) {
                            if (!Files.isDirectory(targetdir))
                                throw ex;
                        }
                        return CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                            throws IOException {
                        Files.copy(file, target.resolve(source.relativize(file)));
                        return CONTINUE;
                    }
                });
    }
}
