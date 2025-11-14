package com.sapling.framework.ai.alibaba.exception;

/**
 * Exception for tool execution errors.
 * 
 * @author Sapling Team
 * @since 1.0.0
 */
public class ToolExecutionException extends AiException {

    public ToolExecutionException(String message) {
        super("TOOL_EXECUTION_ERROR", message);
    }

    public ToolExecutionException(String message, Throwable cause) {
        super("TOOL_EXECUTION_ERROR", message, cause);
    }
}
