package com.sapling.framework.ai.alibaba.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Response model for chat operations.
 * 
 * @author Sapling Team
 * @since 1.0.0
 */
@Data
@Builder
public class ChatResponse {

    /**
     * Generated content
     */
    private String content;

    /**
     * Tool calls requested by the model
     */
    private List<ToolCall> toolCalls;

    /**
     * Token usage information
     */
    private Usage usage;

    /**
     * Finish reason (stop, length, tool_calls, etc.)
     */
    private String finishReason;

    /**
     * Request ID for tracking
     */
    private String requestId;

    /**
     * Check if response contains tool calls
     */
    public boolean hasToolCalls() {
        return toolCalls != null && !toolCalls.isEmpty();
    }
}
