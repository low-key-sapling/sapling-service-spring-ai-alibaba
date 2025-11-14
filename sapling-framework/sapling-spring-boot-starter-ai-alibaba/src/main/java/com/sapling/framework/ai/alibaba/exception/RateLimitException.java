package com.sapling.framework.ai.alibaba.exception;

/**
 * Exception for rate limit exceeded.
 * 
 * @author Sapling Team
 * @since 1.0.0
 */
public class RateLimitException extends AiException {

    public RateLimitException() {
        super("RATE_LIMIT_EXCEEDED", "Rate limit exceeded");
    }

    public RateLimitException(String message) {
        super("RATE_LIMIT_EXCEEDED", message);
    }
}
