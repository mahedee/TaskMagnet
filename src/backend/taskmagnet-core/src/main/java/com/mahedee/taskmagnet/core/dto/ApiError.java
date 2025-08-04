package com.mahedee.taskmagnet.core.dto;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standard error response structure
 */
public class ApiError {
    private String error;
    private String message;
    private String path;
    private int status;
    private LocalDateTime timestamp;
    private String traceId;
    private Map<String, Object> details;

    public ApiError() {
        this.timestamp = LocalDateTime.now();
    }

    public ApiError(String error, String message, String path, int status) {
        this();
        this.error = error;
        this.message = message;
        this.path = path;
        this.status = status;
    }

    // Static factory methods
    public static ApiError badRequest(String message, String path) {
        return new ApiError("Bad Request", message, path, 400);
    }

    public static ApiError notFound(String message, String path) {
        return new ApiError("Not Found", message, path, 404);
    }

    public static ApiError internalError(String message, String path) {
        return new ApiError("Internal Server Error", message, path, 500);
    }

    // Getters and setters
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }
}
