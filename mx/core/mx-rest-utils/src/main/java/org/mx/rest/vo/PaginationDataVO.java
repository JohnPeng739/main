package org.mx.rest.vo;

import org.mx.Pagination;
import org.mx.rest.error.UserInterfaceError;
import org.mx.rest.error.UserInterfaceException;

/**
 * Created by john on 2017/10/6.
 */
public class PaginationDataVO<T> extends DataVO {
    private Pagination pagination;

    public PaginationDataVO() {
    }

    public PaginationDataVO(Pagination pagination, T data) {
        super(data);
        this.pagination = pagination;
    }

    public PaginationDataVO(int errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

    public PaginationDataVO(UserInterfaceError error) {
        super(error);
    }

    public PaginationDataVO(UserInterfaceException exception) {
        super(exception);
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public Pagination getPagination() {
        return this.pagination;
    }
}
