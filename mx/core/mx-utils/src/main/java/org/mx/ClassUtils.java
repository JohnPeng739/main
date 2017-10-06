package org.mx;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by john on 2017/10/6.
 */
public class ClassUtils {
    private static final Log logger = LogFactory.getLog(ClassUtils.class);

    private ClassUtils() {
    }

    public static List<String> scanPackage(String packageName) {
        return scanPackage(packageName, true, true);
    }

    public static List<String> scanPackage(String packageName, boolean recurse, boolean ignoreInlineClass) {
        if(StringUtils.isBlank(packageName)) {
            packageName = "";
        }

        packageName = packageName.replaceAll("\\.", "/");
        ArrayList<String> list = new ArrayList();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        try {
            Enumeration urls = loader.getResources(packageName);
            while(urls.hasMoreElements()) {
                URL url = (URL)urls.nextElement();
                String protocol = url.getProtocol();
                switch(protocol) {
                    case "jar":
                        scanPackageByJar(url.getPath(), recurse, ignoreInlineClass, list);
                        break;
                    case "file":
                        String path = url.getPath();
                        String root = path.substring(0, path.length() - packageName.length());
                        scanPackageByFile(new File(path), root, recurse, ignoreInlineClass, list);
                        break;
                    default:
                        if(logger.isWarnEnabled()) {
                            logger.warn(String.format("Not supported protocol: %s.", protocol));
                        }
                }
            }
        } catch (IOException ex) {
            if(logger.isErrorEnabled()) {
                logger.error(String.format("Get url[%] resource fail.", packageName), ex);
            }
        }
        return list;
    }

    private static void scanPackageByFile(File path, String root, boolean recurse, boolean ignoreInlineClass, List<String> list) {
        if(path != null) {
            if(path.isFile() && path.getName().endsWith(".class") && (!ignoreInlineClass || path.getName().indexOf("$") < 0)) {
                String classPath = path.getAbsolutePath();
                classPath = classPath.substring(root.length(), classPath.length() - ".class".length());
                list.add(classPath.replaceAll("/", "\\."));
            } else {
                if(path.isDirectory() && recurse) {
                    File[] children = path.listFiles();
                    if(children == null || children.length <= 0) {
                        return;
                    }
                    for(File child : children) {
                        scanPackageByFile(child, root, recurse, ignoreInlineClass, list);
                    }
                }

            }
        }
    }

    private static void scanPackageByJar(String path, boolean recurse, boolean ignoreInlineClass, List<String> list) {
        String[] jarInfo = path.split("!");
        if(jarInfo != null && jarInfo.length == 2) {
            String jarFile = jarInfo[0].substring(jarInfo[0].indexOf("/"));
            String packagePath = jarInfo[1].substring(1);

            try (JarFile jar = new JarFile(jarFile)) {
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String entryName = entry.getName();
                    if (entryName.endsWith(".class")) {
                        entryName = entryName.substring(0, entryName.length() - ".class".length());
                        if (recurse) {
                            if (entryName.startsWith(packagePath)) {
                                list.add(entryName.replaceAll("/", "."));
                            }
                        } else {
                            int index = entryName.lastIndexOf('/');
                            String check = entryName;
                            if (index != -1) {
                                check = entryName.substring(0, index);
                            }
                            if (check.equals(packagePath)) {
                                list.add(entryName);
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
