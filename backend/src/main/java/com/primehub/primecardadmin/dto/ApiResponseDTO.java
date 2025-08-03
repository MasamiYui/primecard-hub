package com.primehub.primecardadmin.dto;

public class ApiResponseDTO<T> {
    private boolean success;
    private String message;
    private T data;
    private String errorCode;
    private final java.time.Instant timestamp;
    
    public ApiResponseDTO() {
        this.timestamp = java.time.Instant.now();
    }
    
    public ApiResponseDTO(boolean success, String message, T data, String errorCode) {
        this();
        this.success = success;
        this.message = message;
        this.data = data;
        this.errorCode = errorCode;
    }
    
    // Getters and Setters
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
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public java.time.Instant getTimestamp() {
        return timestamp;
    }
    
    // Static factory methods
    public static <T> ApiResponseDTO<T> success(T data) {
        return new ApiResponseDTO<>(true, "操作成功", data, null);
    }
    
    public static <T> ApiResponseDTO<T> success(String message, T data) {
        return new ApiResponseDTO<>(true, message, data, null);
    }
    
    public static <T> ApiResponseDTO<T> success(String message) {
        return new ApiResponseDTO<>(true, message, null, null);
    }
    
    public static <T> ApiResponseDTO<T> error(String message) {
        return new ApiResponseDTO<>(false, message, null, null);
    }
    
    public static <T> ApiResponseDTO<T> error(String errorCode, String message) {
        return new ApiResponseDTO<>(false, message, null, errorCode);
    }
}