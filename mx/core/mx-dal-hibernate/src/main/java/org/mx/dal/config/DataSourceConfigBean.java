package org.mx.dal.config;

/**
 * 描述： 数据源配置对象接口定义
 *
 * @author john peng
 * Date time 2018/7/22 下午12:28
 */
public interface DataSourceConfigBean {
    /**
     * 获取驱动
     *
     * @return 驱动
     */
    String getDriver();

    /**
     * 获取连接字符串
     *
     * @return 连接字符串
     */
    String getUrl();

    /**
     * 获取用户
     *
     * @return 用户
     */
    String getUser();

    /**
     * 获取密码
     *
     * @return 密码
     */
    String getPassword();

    /**
     * 获取初始化大小
     *
     * @return 初始化大小
     */
    int getInitialSize();

    /**
     * 获取最大大小
     *
     * @return 最大大小
     */
    int getMaxSize();

    /**
     * 获取最大闲置时间
     *
     * @return 最大闲置时间
     */
    int getMaxIdleTime();
}
