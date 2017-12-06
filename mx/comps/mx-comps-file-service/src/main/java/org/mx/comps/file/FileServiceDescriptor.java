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
}
