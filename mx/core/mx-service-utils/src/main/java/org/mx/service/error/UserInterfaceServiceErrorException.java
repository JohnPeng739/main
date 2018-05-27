package org.mx.service.error;

import org.mx.error.UserInterfaceError;
import org.mx.error.UserInterfaceException;

/**
 * 服务错误异常定义类
 *
 * @author : john.peng created on date : 2017/12/31
 */
public class UserInterfaceServiceErrorException extends UserInterfaceException {
    /**
     * 默认的构造函数
     */
    public UserInterfaceServiceErrorException() {
        this(ServiceErrors.SERVICE_OTHER_FAIL);
    }

    /**
     * 默认的构造函数
     *
     * @param error 服务错误枚举定义
     */
    public UserInterfaceServiceErrorException(ServiceErrors error) {
        super(error.getErrorCode(), error.getErrorMessage());
    }

    public enum ServiceErrors implements UserInterfaceError {
        WS_CLIENT_INIT_FAIL("初始化Websocket客户端失败。"),

        COMM_UNSUPPORTED_ENCODING("不支持的字符串编码字符集。"),
        COMM_IO_ERROR("网络数据访问错误。"),
        COMM_SOCKET_ERROR("SOCKET通信错误。"),

        SERVICE_OTHER_FAIL("其他服务访问错误。");
        public static final int BASE_ORDINAL = 60;
        private String errorMessage;

        private ServiceErrors(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        /**
         * {@inheritDoc}
         *
         * @see UserInterfaceError#getErrorCode()
         */
        @Override
        public int getErrorCode() {
            return BASE_ORDINAL + super.ordinal();
        }

        /**
         * {@inheritDoc}
         *
         * @see UserInterfaceError#getErrorMessage()
         */
        @Override
        public String getErrorMessage() {
            return this.errorMessage;
        }
    }
}
