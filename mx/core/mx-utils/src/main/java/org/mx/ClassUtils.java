package org.mx;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.SKIP_SUBTREE;

/**
 * 类工具，包括：包扫描等功能。
 *
 * @author : john.peng date : 2017/10/6
 *         <p>
 *         修改了在Windows环境下存在的扫描类中存在的bug。
 * @author : chunliang.li date : 2017/11/25
 */
public class ClassUtils {
    private static final Log logger = LogFactory.getLog(ClassUtils.class);

    private ClassUtils() {
        super();
    }

    /**
     * 扫描指定包中的类文件
     *
     * @param packageName 指定的包名
     * @return 包中所有的类文件名称列表
     * @see #scanPackage(String, boolean, boolean)
     */
    public static List<String> scanPackage(String packageName) {
        return scanPackage(packageName, true, true);
    }

    /**
     * 扫描指定包中的类文件
     *
     * @param packageName 指定的包名
     * @param action      扫描到后的操作
     * @see #scanPackage(String, boolean, boolean, Consumer)
     */
    public static void scanPackage(String packageName, Consumer<String> action) {
        scanPackage(packageName, true, true, action);
    }

    /**
     * 扫描指定包中的类文件
     *
     * @param packageName       指定的包名
     * @param recurse           如果设置为true，表示进行递归扫描；否则仅扫描第一层目录
     * @param ignoreInlineClass 如果设置为true，表示结果中忽略扫描到的内部类；否则结果中将包括内部类。
     * @return 包中所有的符合条件的类文件名称列表
     * @see #scanPackage(String, boolean, boolean, Consumer)
     */
    public static List<String> scanPackage(String packageName, boolean recurse, boolean ignoreInlineClass) {
        final ArrayList<String> list = new ArrayList();
        scanPackage(packageName, recurse, ignoreInlineClass, className -> list.add(className));
        return list;
    }

    /**
     * 扫描指定包中的类文件
     *
     * @param packageName       指定的包名
     * @param recurse           如果设置为true，表示进行递归扫描；否则仅扫描第一层目录
     * @param ignoreInlineClass 如果设置为true，表示结果中忽略扫描到的内部类；否则结果中将包括内部类。
     * @see #scanPackageByFile(Path, String, String, boolean, boolean, Consumer)
     * @see #scanPackageByJar(String, boolean, boolean, Consumer)
     */
    public static void scanPackage(String packageName, final boolean recurse, final boolean ignoreInlineClass,
                                   final Consumer<String> action) {
        if (StringUtils.isBlank(packageName)) {
            packageName = "";
        }

        packageName = packageName.replaceAll("\\.", "/");
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        try {
            Enumeration urls = loader.getResources(packageName);
            while (urls.hasMoreElements()) {
                URL url = (URL) urls.nextElement();
                String protocol = url.getProtocol();
                switch (protocol) {
                    case "jar":
                        scanPackageByJar(url.getPath(), recurse, ignoreInlineClass, action);
                        break;
                    case "file":
                        String path = url.getPath();
                        String root = path.substring(0, path.length() - packageName.length());
                        // update by lichunliang window得到的root前有一个"/" 20171124 start
                        if (System.getProperty("os.name").toUpperCase().contains("WINDOWS")) {
                            root = root.substring(1);
                        }
                        // update by lichunliang window得到的root前有一个"/" 20171124 end
                        scanPackageByFile(Paths.get(path), root, packageName, recurse, ignoreInlineClass, action);
                        break;
                    default:
                        if (logger.isWarnEnabled()) {
                            logger.warn(String.format("Not supported protocol: %s.", protocol));
                        }
                }
            }
        } catch (IOException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Get url[%] resource fail.", packageName), ex);
            }
        }
    }

    /**
     * 扫描文件目录中的类文件，比如：classes/
     *
     * @param path              指定的文件路径
     * @param root              传递的根目录，便于迭代处理
     * @param packageName       需要检测的包名
     * @param recurse           是否迭代扫描子目录
     * @param ignoreInlineClass 是否忽略内部类文件
     * @param action            扫描到后的操作
     */
    private static void scanPackageByFile(final Path path, final String root, String packageName,
                                          final boolean recurse, final boolean ignoreInlineClass,
                                          final Consumer<String> action) {
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    String file = dir.toString();
                    return recurse || packageName.equals(file.substring(root.length())) ? CONTINUE : SKIP_SUBTREE;
                }

                @Override
                public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                    String file = path.toString();
                    if (Files.isRegularFile(path) && file.endsWith(".class") && (!ignoreInlineClass || !file.contains("$"))) {
                        String classPath = file;
                        classPath = classPath.substring(root.length(), classPath.length() - ".class".length());
                        // list.add(classPath.replaceAll("/", "\\."));
                        // update by lichunliang 要考虑windows"\" 20171124 start
                        action.accept(classPath.replaceAll("[/\\\\]", "\\."));
                        // update by lichunliang 要考虑windows"\" 20171124 end
                    }
                    return CONTINUE;
                }
            });
        } catch (IOException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Scan package by file fail, path: %s.", path.toFile().getAbsolutePath()));
            }
        }
    }

    /**
     * 扫描jar文件中的类文件，比如：core.jar
     *
     * @param path              包路径
     * @param recurse           是否迭代
     * @param ignoreInlineClass 是否忽略内部类文件
     * @param action            扫描到后执行的操作
     */
    private static void scanPackageByJar(final String path, final boolean recurse, final boolean ignoreInlineClass,
                                         final Consumer<String> action) {
        String[] jarInfo = path.split("!");
        if (jarInfo != null && jarInfo.length == 2) {
            String jarFile = jarInfo[0].substring(jarInfo[0].indexOf("/"));
            String packagePath = jarInfo[1].substring(1);

            try (JarFile jar = new JarFile(jarFile)) {
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String entryName = entry.getName();
                    if (entryName.endsWith(".class") && (!ignoreInlineClass || !entryName.contains("$"))) {
                        entryName = entryName.substring(0, entryName.length() - ".class".length());
                        if (recurse) {
                            if (entryName.startsWith(packagePath)) {
                                action.accept(entryName.replaceAll("/", "."));
                            }
                        } else {
                            int index = entryName.lastIndexOf('/');
                            String check = entryName;
                            if (index != -1) {
                                check = entryName.substring(0, index);
                            }
                            if (check.equals(packagePath)) {
                                action.accept(entryName);
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Operate the jar file[%s] fail.", jarFile), ex);
                }
            }
        } else {
            if (logger.isWarnEnabled()) {
                logger.warn(String.format("The path【%s] is not a valid jar file path.", new Object[]{path}));
            }
        }
    }
}
