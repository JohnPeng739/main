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
     * 获取初始化大小，默认为1。
     *
     * @return 初始化大小
     */
    int getInitialSize();

    /**
     * 获取最小闲置大小，默认为3。设置为负数不限制。
     *
     * @return 最小闲置大小
     */
    int getMinIdle();

    /**
     * 获取最大闲置大小，默认为5。设置为负数不限制。
     *
     * @return 最大闲置大小
     */
    int getMaxIdle();

    /**
     * 获取最大大小，默认为50。设置为负数不限制。
     *
     * @return 最大大小
     */
    int getMaxSize();

    /**
     * 获取最大等待时间，默认为3000ms。
     *
     * @return 最大等待时间
     */
    int getMaxWaitMillis();

    /**
     * 获取是否在创建连接时检测连接的有效性，默认为true。
     *
     * @return 返回true表示在创建连接时检测连接的有效性
     */
    boolean isTestOnCreate();

    /**
     * 获取是否在获取连接时检测连接的有效性，默认为true。
     *
     * @return 返回true表示在获取连接时检测连接的有效性
     */
    boolean isTestOnBorrow();

    /**
     * 获取是否在归还pool时检测连接的有效性，默认为false。
     *
     * @return 返回true表示在归还pool时检测连接的有效性
     */
    boolean isTestOnReturn();

    /**
     * 获取是否在空闲时检测连接的有效性，默认为true。
     *
     * @return 返回true表示在空闲时检测连接的有效性
     */
    boolean isTestWhileIdle();

    /**
     * 获取连接验证SQL查询，默认为"select 1"。
     *
     * @return 验证SQL查询
     */
    String getValidationQuery();
}
