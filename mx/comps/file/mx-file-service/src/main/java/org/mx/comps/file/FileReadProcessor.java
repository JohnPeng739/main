package org.mx.comps.file;

import javax.servlet.http.HttpServletRequest;
import java.io.OutputStream;

/**
 * 文件存储处理接口定义
 *
 * @author : john.peng created on date : 2017/12/04
 */
public interface FileReadProcessor {
    /**
     * 或当前处理器关联的文件服务描述符
     *
     * @return 文件服务描述符
     */
    FileServiceDescriptor getFileServiceDescriptor();

    /**
     * 判断处理器是否已经被打开
     *
     * @return 返回true表示已经被打开，否则未打开。
     */
    boolean isOpened();

    /**
     * 关闭文件
     */
    void close();

    /**
     * 根据HTTP请求中的参数初始化处理器
     *
     * @param req HTTP请求
     */
    void init(HttpServletRequest req);

    /**
     * 将打开的文件内容读入到输出流中
     *
     * @param out 输出流
     */
    void read(OutputStream out);
}