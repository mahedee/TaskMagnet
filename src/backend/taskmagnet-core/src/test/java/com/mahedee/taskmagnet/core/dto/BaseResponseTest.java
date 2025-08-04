package com.mahedee.taskmagnet.core.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;

/**
 * Unit tests for BaseResponse
 */
class BaseResponseTest {
    
    @Test
    void testSuccessResponse() {
        // Given
        String testData = "Test Data";
        
        // When
        BaseResponse<String> response = BaseResponse.success(testData);
        
        // Then
        assertTrue(response.isSuccess());
        assertEquals("Success", response.getMessage());
        assertEquals(testData, response.getData());
        assertNotNull(response.getTimestamp());
    }
    
    @Test
    void testSuccessResponseWithMessage() {
        // Given
        String testData = "Test Data";
        String customMessage = "Custom Success Message";
        
        // When
        BaseResponse<String> response = BaseResponse.success(testData, customMessage);
        
        // Then
        assertTrue(response.isSuccess());
        assertEquals(customMessage, response.getMessage());
        assertEquals(testData, response.getData());
        assertNotNull(response.getTimestamp());
    }
    
    @Test
    void testErrorResponse() {
        // Given
        String errorMessage = "Error occurred";
        
        // When
        BaseResponse<String> response = BaseResponse.error(errorMessage);
        
        // Then
        assertFalse(response.isSuccess());
        assertEquals(errorMessage, response.getMessage());
        assertNull(response.getData());
        assertNotNull(response.getTimestamp());
    }
    
    @Test
    void testErrorResponseWithTraceId() {
        // Given
        String errorMessage = "Error occurred";
        String traceId = "trace-123";
        
        // When
        BaseResponse<String> response = BaseResponse.error(errorMessage, traceId);
        
        // Then
        assertFalse(response.isSuccess());
        assertEquals(errorMessage, response.getMessage());
        assertEquals(traceId, response.getTraceId());
        assertNull(response.getData());
        assertNotNull(response.getTimestamp());
    }
}
