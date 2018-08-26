package org.mx.service.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.error.UserInterfaceError;
import org.mx.error.UserInterfaceException;
import org.mx.service.error.UserInterfaceServiceErrorException;
import org.mx.service.rest.vo.DataVO;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * 描述： 处理UserInterface的映射器
 *
 * @author john peng
 * Date time 2018/8/26 下午6:05
 */
@Provider
public class UserInterfaceExceptionMapper implements ExceptionMapper<Exception> {
    private static final Log logger = LogFactory.getLog(UserInterfaceExceptionMapper.class);

    /**
     * {@inheritDoc}
     *
     * @see ExceptionMapper#toResponse(Throwable)
     */
    @SuppressWarnings("unchecked")
    @Override
    public Response toResponse(Exception ex) {
        if (ex instanceof UserInterfaceException) {
            if (logger.isDebugEnabled()) {
                logger.debug("Any captured exception.", ex);
            }
            return Response.ok(new DataVO((UserInterfaceError) ex), MediaType.APPLICATION_JSON).build();
        } else {
            if (logger.isErrorEnabled()) {
                logger.error("Any not captured exception", ex);
            }
            return Response.ok(new DataVO((UserInterfaceError) new UserInterfaceException(
                    UserInterfaceServiceErrorException.ServiceErrors.SERVICE_OTHER_FAIL.getErrorCode(),
                    UserInterfaceServiceErrorException.ServiceErrors.SERVICE_OTHER_FAIL.getErrorMessage(),
                    ex.getMessage(), ex
            ))).build();
        }
    }
}
