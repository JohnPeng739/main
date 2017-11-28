package org.mx;

import java.io.*;

/**
 * 文件处理工具类
 *
 * @author : john.peng date : 2017/10/15
 */
public class FileUtils {
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
        deleteFile(new File(filePath));
    }

    /**
     * 删除指定的文件或目录，如果是目录，将会删除子目录及其包含的文件。
     *
     * @param file 待删除的文件或目录对象
     * @throws IOException 删除过程中发生的异常
     */
    public static void deleteFile(File file) throws IOException {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files.length > 0) {
                for (File child : files) {
                    // 迭代处理子目录
                    deleteFile(child);
                    // 子目录处理完毕后，删除父目录
                    file.delete();
                }
            } else {
                // 空目录，直接删除
                file.delete();
            }
        } else {
            // 文件，直接删除
            file.delete();
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
        File parent = new File(filePath, date);
        return saveFile(parent.getAbsolutePath(), fileName, is);
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
        File file = new File(filePath, fileName);
        File parent = file.getParentFile();
        if (parent.exists() && !parent.isDirectory()) {
            parent.delete();
        }
        if (!parent.exists()) {
            parent.mkdirs();
        }
        try (OutputStream os = new FileOutputStream(file)) {
            byte[] buff = new byte[2048];
            int len = 0;
            do {
                len = is.read(buff, 0, 2048);
                if (len > 0) {
                    os.write(buff, 0, len);
                }
            } while (len == 2048);
        }
        return file.getAbsolutePath();
    }
}
