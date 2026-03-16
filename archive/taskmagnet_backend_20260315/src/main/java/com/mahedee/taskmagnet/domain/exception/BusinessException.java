package com.mahedee.taskmagnet.domain.exception;

/**
 * Thrown when a business rule or invariant is violated.
 */
public class BusinessException extends DomainException {
    public BusinessException(String message) {
        super(message);
    }
}
