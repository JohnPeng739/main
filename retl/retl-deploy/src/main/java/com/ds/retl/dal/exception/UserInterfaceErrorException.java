package com.ds.retl.dal.exception;

import com.ds.retl.rest.error.UserInterfaceErrors;
import org.mx.rest.error.UserInterfaceException;

/**
 * Created by john on 2017/10/8.
 */
public class UserInterfaceErrorException extends UserInterfaceException {
    public UserInterfaceErrorException(UserInterfaceErrors error) {
        super(error.getErrorCode(), error.getErrorMessage());
    }
}
