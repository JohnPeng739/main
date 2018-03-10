package org.mx.service.client.rest;

/**
 * Rest服务调用过程中发生的异常
 *
 * @author : john.peng created on date : 2017/11/4
 */
public class RestInvokeException extends Exception {
    private String responseError = null;

    public RestInvokeException(String error) {
        super();
        this.responseError = error;
    }

    public String getResponseError() {
        return responseError;
    }
}
