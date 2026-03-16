package com.mahedee.taskmagnet.domain.exception;

/**
 * Thrown when a requested resource cannot be found.
 */
public class ResourceNotFoundException extends DomainException {

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue));
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
