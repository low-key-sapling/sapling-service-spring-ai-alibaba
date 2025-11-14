package com.sapling.framework.ai.alibaba.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Tool execution result model.
 * 
 * @author Sapling Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToolResult {

    /**
     * Tool call ID this result corresponds to
     */
    private String toolCallId;

    /**
     * Tool name
     */
    private String toolName;

    /**
     * Whether the tool execution was successful
     */
    private boolean success;

    /**
     * Result data from tool execution
     */
    private Object result;

    /**
     * Error message if execution failed
     */
    private String error;

    /**
     * Execution time in milliseconds
     */
    private Long executionTime;

    /**
     * Create a successful tool result
     */
    public static ToolResult success(String toolCallId, String toolName, Object result, Long executionTime) {
        return ToolResult.builder()
            .toolCallId(toolCallId)
            .toolName(toolName)
            .success(true)
            .result(result)
            .executionTime(executionTime)
            .build();
    }

    /**
     * Create a failed tool result
     */
    public static ToolResult failure(String toolCallId, String toolName, String error, Long executionTime) {
        return ToolResult.builder()
            .toolCallId(toolCallId)
            .toolName(toolName)
            .success(false)
            .error(error)
            .executionTime(executionTime)
            .build();
    }
}
