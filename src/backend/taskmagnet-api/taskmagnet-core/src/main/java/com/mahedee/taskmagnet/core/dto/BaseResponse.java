package com.mahedee.taskmagnet.core.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

/**
 * Standard API Response Wrapper
 * Provides consistent response format across all modules
 * 
 * @param <T> The type of data being returned
 */
public class BaseResponse<T> {
    
    private boolean success;
    private String message;
    private T data;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    
    private String traceId;
    
    public BaseResponse() {
        this.timestamp = LocalDateTime.now();
    }
    
    public BaseResponse(boolean success, String message, T data, LocalDateTime timestamp, String traceId) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = timestamp;
        this.traceId = traceId;
    }
    
    /**
     * Create a successful response with data
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(true, "Success", data, LocalDateTime.now(), null);
    }
    
    /**
     * Create a successful response with data and custom message
     */
    public static <T> BaseResponse<T> success(T data, String message) {
        return new BaseResponse<>(true, message, data, LocalDateTime.now(), null);
    }
    
    /**
     * Create an error response with message
     */
    public static <T> BaseResponse<T> error(String message) {
        return new BaseResponse<>(false, message, null, LocalDateTime.now(), null);
    }
    
    /**
     * Create an error response with message and data
     */
    public static <T> BaseResponse<T> error(String message, T data) {
        return new BaseResponse<>(false, message, data, LocalDateTime.now(), null);
    }
    
    // Getters and setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
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
}
