package org.mx.error;

/**
 * 人机界面错误接口定义
 *
 * @author : john.peng date : 2017/10/6
 */
public interface UserInterfaceError {
    /**
     * 获取消息代码，整数值
     *
     * @return 消息代码
     */
    int getErrorCode();

    /**
     * 获取错误消息
     *
     * @return 错误消息
     */
    String getErrorMessage();
}
