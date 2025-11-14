package com.sapling.framework.ai.alibaba.exception;

/**
 * Exception for invalid API key.
 * 
 * @author Sapling Team
 * @since 1.0.0
 */
public class ApiKeyInvalidException extends AiException {

    public ApiKeyInvalidException() {
        super("INVALID_API_KEY", "Invalid or missing API key");
    }

    public ApiKeyInvalidException(String message) {
        super("INVALID_API_KEY", message);
    }
}
