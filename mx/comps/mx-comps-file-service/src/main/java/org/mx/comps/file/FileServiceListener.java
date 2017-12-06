package org.mx.comps.file;

/**
 * 文件服务生命周期监听接口，提供：start、finish、cancel回调接口。
 *
 * @author : john.peng created on date : 2017/12/05
 */
public interface FileServiceListener {
    /**
     * 文件服务已经准备好，顺利开始了。
     *
     * @param descriptor 文件描述符
     */
    void started(FileServiceDescriptor descriptor);

    /**
     * 文件服务已经完成传输，并顺利关闭了。
     *
     * @param descriptor 文件描述符
     */
    void finished(FileServiceDescriptor descriptor);

    /**
     * 文件服务已经中断，并顺利关闭了。
     *
     * @param descriptor 文件描述符
     */
    void canceled(FileServiceDescriptor descriptor);
}
