package com.livehealth.shared.exception;

import com.livehealth.shared.base.RestData;
import com.livehealth.shared.base.VsResponseUtil;
import com.livehealth.shared.constant.ErrorMessage;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.engine.path.PathImpl;
import com.livehealth.shared.base.HttpStatus;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Provider
public class GlobalExceptionHandler {

    private String resolveMessage(String key, Object[] params) {
        if (key == null) return null;
        if (params != null && params.length > 0) {
            try {
                return MessageFormat.format(key, params);
            } catch (Exception e) {
                // Ignore and return key
            }
        }
        return key;
    }

    // Error validate for param & body in Quarkus
    @ServerExceptionMapper(ConstraintViolationException.class)
    public Response handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> result = new LinkedHashMap<>();
        ex.getConstraintViolations().forEach((error) -> {
            String fieldName = ((PathImpl) error.getPropertyPath()).getLeafNode().getName();
            String errorMessage = resolveMessage(Objects.requireNonNull(error.getMessage()), null);
            result.put(fieldName, errorMessage);
        });
        return VsResponseUtil.error(HttpStatus.BAD_REQUEST, result);
    }

    @ServerExceptionMapper(Exception.class)
    public Response handlerInternalServerError(Exception ex) {
        log.error(ex.getMessage(), ex);
        String message = resolveMessage(ErrorMessage.ERR_EXCEPTION_GENERAL, null);
        return VsResponseUtil.error(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    // Exception custom
    @ServerExceptionMapper(VsException.class)
    public Response handleVsException(VsException ex) {
        String message = resolveMessage(Objects.requireNonNull(ex.getMessage()), ex.getParams());
        log.error("VsException: {}", message);
        return VsResponseUtil.error(ex.getStatus(), message);
    }

    @ServerExceptionMapper(NotFoundException.class)
    public Response handlerNotFoundException(NotFoundException ex) {
        String message = resolveMessage(ex.getMessage(), ex.getParams());
        log.error(message, ex);
        return VsResponseUtil.error(ex.getStatus(), message);
    }

    @ServerExceptionMapper(InvalidException.class)
    public Response handlerInvalidException(InvalidException ex) {
        log.error(ex.getMessage(), ex);
        String message = resolveMessage(ex.getMessage(), ex.getParams());
        return VsResponseUtil.error(ex.getStatus(), message);
    }

    @ServerExceptionMapper(InternalServerException.class)
    public Response handlerInternalServerException(InternalServerException ex) {
        String message = resolveMessage(ex.getMessage(), ex.getParams());
        log.error(message, ex);
        return VsResponseUtil.error(ex.getStatus(), message);
    }

    @ServerExceptionMapper(UploadFileException.class)
    public Response handleUploadImageException(UploadFileException ex) {
        String message = resolveMessage(ex.getMessage(), ex.getParams());
        log.error(message, ex);
        return VsResponseUtil.error(ex.getStatus(), message);
    }

    @ServerExceptionMapper(UnauthorizedException.class)
    public Response handleUnauthorizedException(UnauthorizedException ex) {
        String message = resolveMessage(ex.getMessage(), ex.getParams());
        log.error(message, ex);
        return VsResponseUtil.error(ex.getStatus(), message);
    }

    @ServerExceptionMapper(ForbiddenException.class)
    public Response handleAccessDeniedException(ForbiddenException ex) {
        String message = resolveMessage(ex.getMessage(), ex.getParams());
        log.error(message, ex);
        return VsResponseUtil.error(ex.getStatus(), message);
    }
}
