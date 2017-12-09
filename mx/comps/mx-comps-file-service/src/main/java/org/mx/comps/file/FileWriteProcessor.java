package org.mx.comps.file;

import com.alibaba.fastjson.JSONObject;

import java.io.InputStream;

/**
 * 文件存储处理接口定义
 *
 * @author : john.peng created on date : 2017/12/04
 */
public interface FileWriteProcessor {
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
     * 设置处理器处理进程监听器
     *
     * @param listener 监听器
     */
    void setFileServiceListener(FileServiceListener listener);

    /**
     * 关闭文件
     */
    void close();

    /**
     * 执行一条从WebSocket中收到的命令，该文本命令已经被转换为一个JSONObject对象
     *
     * @param json 文本命令
     */
    void command(JSONObject json);

    /**
     * 将输入流中的数据写入到打开的文件中
     *
     * @param in 输入流
     */
    void write(InputStream in);
}
