package org.mx.tools.ffee.error;

import org.mx.error.UserInterfaceError;
import org.mx.error.UserInterfaceException;

/**
 * 描述： FFEE人机交互错误异常定义
 *
 * @author John.Peng
 * Date time 2018/2/19 下午5:18
 */
public class UserInterfaceFfeeErrorException extends UserInterfaceException {
    /**
     * 默认的构造函数
     */
    UserInterfaceFfeeErrorException() {
        this(UserInterfaceFfeeErrorException.FfeeErrors.FFEE_OTHER_FAIL);
    }

    /**
     * 构造函数
     *
     * @param error 人机界面错误枚举
     */
    public UserInterfaceFfeeErrorException(UserInterfaceFfeeErrorException.FfeeErrors error) {
        super(error.getErrorCode(), error.getErrorMessage());
    }

    /**
     * Ffee(家庭账单)操作错误枚举定义
     */
    public enum FfeeErrors implements UserInterfaceError {
        FAMILY_NOT_EXISTED("指定的家庭不存在。"),

        ACCOUNT_NOT_EXISTED("指定的账户不存在。"),

        COURSE_NOT_EXISTED("指定的科目不存在。"),
        COURSE_HAS_EXISTED("指定的科目代码已经存在，不能重复。"),

        INCOME_NOT_EXISTED("指定的收入不存在。"),

        SPENDING_NOT_EXISTED("指定的支出不存在。"),

        FAMILY_MEMBER_SAVE_FAIL("保存家庭成员信息失败。"),

        PATH_DELETE_FAIL("删除指定的目录/文件失败。"),
        PATH_CREATE_FAIL("创建指定的文件失败。"),
        WRITE_FILE_FAIL("文件写入失败。"),

        QRCODE_ENCODE_FAIL("二维码编码发生错误。"),
        QRCODE_DECODE_FAIL("二维码解码发生错误。"),
        QRCODE_IO_FAIL("二维码读写发生错误。"),

        FFEE_OTHER_FAIL("未知家庭账簿错误。");

        public static final int BASE_ORDINAL = 10000;
        private String errorMessage;

        /**
         * 构造函数
         *
         * @param errorMessage 错误信息
         */
        FfeeErrors(String errorMessage) {
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