package org.mx.rest.vo;

import org.mx.rest.error.UserInterfaceError;
import org.mx.rest.error.UserInterfaceException;

/**
 * Created by john on 2017/10/6.
 */
public class DataVO<T> {
    private int errorCode;
    private String errorMessage;
    private T data;

    public DataVO() {
        this.errorCode = 0;
        this.errorMessage = null;
    }

    public DataVO(T data) {
        this.errorCode = 0;
        this.errorMessage = null;
        this.errorCode = 0;
        this.errorMessage = null;
        this.data = data;
    }

    public DataVO(int errorCode, String errorMessage) {
        this.errorCode = 0;
        this.errorMessage = null;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public DataVO(UserInterfaceError error) {
        this(error.getErrorCode(), error.getErrorMessage());
    }

    public DataVO(UserInterfaceException exception) {
        this(exception.getErrorCode(), exception.getErrorMessage());
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
