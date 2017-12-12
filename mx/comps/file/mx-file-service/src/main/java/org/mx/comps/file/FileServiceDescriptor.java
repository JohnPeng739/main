package org.mx.comps.file;

/**
 * 文件管理描述接口定义，用于根据各类规则产生存储路径。
 *
 * @author : john.peng created on date : 2017/12/04
 */
public interface FileServiceDescriptor {
    /**
     * 获取本文件管理描述对象关联的文件唯一ID
     *
     * @return 文件ID
     */
    String getId();

    /**
     * 获取本文件管理描述对象关联的文件访问路径。
     *
     * @return 文件访问路径
     */
    String getPath();

    /**
     * 获取本文件管理描述对象关系的文件存放的目录（相对路径）
     *
     * @return 目录
     */
    String getParentPath();

    /**
     * 获取本文件管理秒户对象管理的文件名。
     *
     * @return 文件名
     */
    String getFilename();

    /**
     * 获取文件的长度，单位为字节；如果文件不存在，则返回长度为-1。
     *
     * @return 文件长度
     */
    long getLength();
}
