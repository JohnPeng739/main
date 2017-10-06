package org.mx;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {
	private FileUtils() {
		super();
	}

	public static String getType(String name) {
		return name.substring(name.lastIndexOf(".") + 1);
	}

	/**
	 * 将输入流中的数据保存到指定的目录中，并按照保存日期建立子目录，文件名采用UUID自动生成。
	 * 
	 * @param filePath
	 *            目录
	 * @param is
	 *            输入流
	 * @return 保存的文件路径
	 * @throws IOException
	 *             保存过程中发生的异常
	 */
	public static String saveFile(String filePath, InputStream is) throws IOException {
		String date = DateUtils.get8TimeNow(), fileName = DigestUtils.uuid().replaceAll("-", "");
		File parent = new File(filePath, date);
		return saveFile(parent.getAbsolutePath(), fileName, is);
	}

	/**
	 * 将输入流中的数据保存到指定目录中的文件中
	 * 
	 * @param filePath
	 *            目录
	 * @param fileName
	 *            文件名
	 * @param is
	 *            输入流
	 * @return 保存的文件路径
	 * @throws IOException
	 *             保存过程中发生的异常
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
