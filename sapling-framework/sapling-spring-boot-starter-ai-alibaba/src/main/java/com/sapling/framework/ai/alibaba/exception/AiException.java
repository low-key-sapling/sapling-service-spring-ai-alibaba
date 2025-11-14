package com.sapling.framework.ai.alibaba.exception;

/**
 * Base exception for AI operations.
 * 
 * @author Sapling Team
 * @since 1.0.0
 */
public class AiException extends RuntimeException {

    private String errorCode;

    public AiException(String message) {
        super(message);
    }

    public AiException(String message, Throwable cause) {
        super(message, cause);
    }

    public AiException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public AiException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
