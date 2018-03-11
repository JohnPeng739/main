package org.mx.comps.file;

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
     * 根据请求对象的参数初始化处理器
     *
     * @param initReq 初始化请求对象，比如HTTP请求等
     */
    void init(Object initReq);

    /**
     * 将打开的文件内容按照限定缓冲读入到二进制数组中，如果返回长度小于数组长度，表示文件已经到末尾。
     *
     * @param buffer 二进制数据数组
     * @return 本次读取数据的长度
     */
    int read(byte[] buffer);

    /**
     * 将打开的文件内容读入到输出流中
     *
     * @param out 输出流
     */
    void read(OutputStream out);
}
