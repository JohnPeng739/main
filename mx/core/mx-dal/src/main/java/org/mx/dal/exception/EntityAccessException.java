package org.mx.dal.exception;

/**
 * Created by john on 2017/8/18.
 */
public class EntityAccessException extends Exception {
    public EntityAccessException(String message) {
        super(message);
    }

    public EntityAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
