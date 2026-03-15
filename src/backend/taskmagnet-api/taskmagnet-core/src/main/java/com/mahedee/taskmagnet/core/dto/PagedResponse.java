package com.mahedee.taskmagnet.core.dto;

import java.util.List;

/**
 * Paginated Response Wrapper for TaskMagnet API
 * 
 * This class extends BaseResponse to provide pagination metadata alongside data content.
 * It's designed to handle large datasets by breaking them into manageable pages,
 * improving both performance and user experience.
 * 
 * Pagination Features:
 * - Zero-based page indexing (first page is 0)
 * - Configurable page size for flexibility
 * - Total element count for progress indication
 * - Navigation flags for UI state management
 * - Consistent response format across all paginated endpoints
 * 
 * Navigation Properties:
 * - first: Indicates if this is the first page
 * - last: Indicates if this is the last page
 * - hasNext: True if there are more pages after current
 * - hasPrevious: True if there are pages before current
 * 
 * Use Cases:
 * - User lists with hundreds of records
 * - Task lists with filtering and sorting
 * - Project listings with search capabilities
 * - Comment threads with chronological ordering
 * - Audit logs with date-based navigation
 * 
 * @param <T> The type of data items contained in the page
 * @author TaskMagnet Core Team
 * @version 3.0.0
 * @since 1.0.0
 */
public class PagedResponse<T> extends BaseResponse<List<T>> {
    
    /**
     * Current page number (zero-based)
     * First page is 0, second page is 1, etc.
     */
    private int page;
    
    /**
     * Number of items per page
     * Configured by client request or system default
     */
    private int size;
    
    /**
     * Total number of items across all pages
     * Used for calculating total pages and progress indication
     */
    private long totalElements;
    
    /**
     * Total number of pages available
     * Calculated based on totalElements and page size
     */
    private int totalPages;
    
    /**
     * Flag indicating if this is the first page
     * True when page number is 0
     */
    private boolean first;
    
    /**
     * Flag indicating if this is the last page
     * True when no more pages are available
     */
    private boolean last;
    
    /**
     * Flag indicating if there are more pages after current
     * Useful for "Load More" or "Next" button states
     */
    private boolean hasNext;
    
    /**
     * Flag indicating if there are pages before current
     * Useful for "Previous" button states
     */
    private boolean hasPrevious;

    /**
     * Default constructor for serialization frameworks
     * Initializes parent BaseResponse with current timestamp
     */
    public PagedResponse() {
        super();
    }

    /**
     * Main constructor for creating paginated responses
     * 
     * Creates a paginated response with content and metadata. Automatically
     * calculates navigation flags and total pages based on provided parameters.
     * 
     * Calculation Logic:
     * - totalPages = ceil(totalElements / size)
     * - first = (page == 0)
     * - last = (page >= totalPages - 1)
     * - hasNext = !last
     * - hasPrevious = !first
     * 
     * @param content List of items for the current page
     * @param page Current page number (zero-based)
     * @param size Number of items per page
     * @param totalElements Total number of items across all pages
     */
    public PagedResponse(List<T> content, int page, int size, long totalElements) {
        super();
        this.setData(content);
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        
        // Calculate total pages (handle edge case where size is 0)
        this.totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 0;
        
        // Calculate navigation flags
        this.first = (page == 0);
        this.last = (page >= totalPages - 1) || (totalPages == 0);
        this.hasNext = !last && totalPages > 0;
        this.hasPrevious = !first && page > 0;
    }

    /**
     * Static factory method for creating successful paginated responses
     * 
     * Convenience method that creates a PagedResponse with success status
     * and default success message. This is the recommended way to create
     * paginated responses in service layers.
     * 
     * @param <T> Type of the data items
     * @param content List of items for the current page
     * @param page Current page number (zero-based)
     * @param size Number of items per page
     * @param totalElements Total number of items across all pages
     * @return Configured PagedResponse with success status
     */
    public static <T> PagedResponse<T> of(List<T> content, int page, int size, long totalElements) {
        PagedResponse<T> response = new PagedResponse<>(content, page, size, totalElements);
        response.setSuccess(true);
        response.setMessage("Success");
        return response;
    }

    /**
     * Static factory method for creating paginated responses with custom message
     * 
     * Creates a PagedResponse with success status and custom message.
     * Useful for providing context-specific feedback to clients.
     * 
     * @param <T> Type of the data items
     * @param content List of items for the current page
     * @param page Current page number (zero-based)
     * @param size Number of items per page
     * @param totalElements Total number of items across all pages
     * @param message Custom success message
     * @return Configured PagedResponse with success status and custom message
     */
    public static <T> PagedResponse<T> of(List<T> content, int page, int size, long totalElements, String message) {
        PagedResponse<T> response = new PagedResponse<>(content, page, size, totalElements);
        response.setSuccess(true);
        response.setMessage(message);
        return response;
    }

    // Getters and setters with comprehensive documentation

    /**
     * Gets the current page number (zero-based)
     * @return Current page number, starting from 0
     */
    public int getPage() {
        return page;
    }

    /**
     * Sets the current page number
     * @param page Page number (should be zero-based)
     */
    public void setPage(int page) {
        this.page = page;
    }

    /**
     * Gets the number of items per page
     * @return Items per page count
     */
    public int getSize() {
        return size;
    }

    /**
     * Sets the number of items per page
     * @param size Items per page count (should be positive)
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * Gets the total number of items across all pages
     * @return Total items count
     */
    public long getTotalElements() {
        return totalElements;
    }

    /**
     * Sets the total number of items across all pages
     * @param totalElements Total items count
     */
    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    /**
     * Gets the total number of pages available
     * @return Total pages count
     */
    public int getTotalPages() {
        return totalPages;
    }

    /**
     * Sets the total number of pages available
     * @param totalPages Total pages count
     */
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    /**
     * Checks if this is the first page
     * @return true if this is the first page (page 0)
     */
    public boolean isFirst() {
        return first;
    }

    /**
     * Sets the first page flag
     * @param first true if this is the first page
     */
    public void setFirst(boolean first) {
        this.first = first;
    }

    /**
     * Checks if this is the last page
     * @return true if this is the last page
     */
    public boolean isLast() {
        return last;
    }

    /**
     * Sets the last page flag
     * @param last true if this is the last page
     */
    public void setLast(boolean last) {
        this.last = last;
    }

    /**
     * Checks if there are more pages after the current one
     * @return true if there are more pages available
     */
    public boolean isHasNext() {
        return hasNext;
    }

    /**
     * Sets the has next page flag
     * @param hasNext true if there are more pages available
     */
    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    /**
     * Checks if there are pages before the current one
     * @return true if there are previous pages available
     */
    public boolean isHasPrevious() {
        return hasPrevious;
    }

    /**
     * Sets the has previous page flag
     * @param hasPrevious true if there are previous pages available
     */
    public void setHasPrevious(boolean hasPrevious) {
        this.hasPrevious = hasPrevious;
    }
}
