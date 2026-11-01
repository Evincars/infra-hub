package com.infrahub.watcher.shared.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

/**
 * Rendered by Spring MVC as an RFC 9457 {@code application/problem+json} 404 response,
 * no custom {@code @ControllerAdvice} needed.
 */
public class ResourceNotFoundException extends ErrorResponseException {

    public ResourceNotFoundException(String resource, Object id) {
        super(HttpStatus.NOT_FOUND,
                ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "%s with id %s not found".formatted(resource, id)),
                null);
    }
}
