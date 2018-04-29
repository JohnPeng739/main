package org.mx.service.rest.vo;

import org.mx.dal.Pagination;
import org.mx.error.UserInterfaceError;
import org.mx.error.UserInterfaceException;

/**
 * REST返回的分页数据值对象定义
 *
 * @author : john.peng date : 2017/10/6
 * @see DataVO
 * @see UserInterfaceError
 * @see UserInterfaceException
 * @see Pagination
 */
public class PaginationDataVO<T> extends DataVO {
    private Pagination pagination;

    /**
     * 默认的构造函数
     */
    public PaginationDataVO() {
        super();
    }

    /**
     * 默认的构造函数
     *
     * @param pagination 分页对象
     * @param data       数据
     */
    @SuppressWarnings("unchecked")
    public PaginationDataVO(Pagination pagination, T data) {
        super(data);
        this.pagination = pagination;
    }

    /**
     * 默认的构造函数
     *
     * @param errorCode    错误代码
     * @param errorMessage 错误消息
     */
    public PaginationDataVO(int errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

    /**
     * 默认的构造函数
     *
     * @param error 人机界面错误
     */
    public PaginationDataVO(UserInterfaceError error) {
        super(error);
    }

    /**
     * 默认的构造函数
     *
     * @param exception 人机界面异常
     */
    public PaginationDataVO(UserInterfaceException exception) {
        super(exception);
    }

    /**
     * 获取分页对象
     *
     * @return 分页对象
     */
    public Pagination getPagination() {
        return this.pagination;
    }

    /**
     * 设置分页对象
     *
     * @param pagination 分页对象
     */
    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }
}
