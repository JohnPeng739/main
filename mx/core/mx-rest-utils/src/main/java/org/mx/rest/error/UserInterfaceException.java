package org.mx.rest.error;

/**
 * Created by john on 2017/10/6.
 */
public class UserInterfaceException extends Exception implements UserInterfaceError {
    private int errorCode = 0;
    private String errorMessage = null;

    public UserInterfaceException(int errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public UserInterfaceException(int errorCode, String errorMessage, String exceptionMessage) {
        super(exceptionMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public UserInterfaceException(int errorCode, String errorMessage, Throwable throwable) {
        super(errorMessage, throwable);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public UserInterfaceException(int errorCode, String errorMessage, String exceptionMessage, Throwable throwable) {
        super(exceptionMessage, throwable);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }
}

