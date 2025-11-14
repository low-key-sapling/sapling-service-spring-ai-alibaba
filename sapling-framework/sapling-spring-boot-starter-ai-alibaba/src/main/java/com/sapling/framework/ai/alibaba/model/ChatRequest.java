package com.sapling.framework.ai.alibaba.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Request model for chat operations.
 * 
 * @author Sapling Team
 * @since 1.0.0
 */
@Data
@Builder
public class ChatRequest {

    /**
     * User message content
     */
    private String message;

    /**
     * Conversation history
     */
    private List<Message> history;

    /**
     * Available tools for function calling
     */
    private List<ToolDefinition> tools;

    /**
     * Results from previous tool calls
     */
    private List<ToolResult> toolResults;

    /**
     * Temperature for response randomness (0.0 - 2.0)
     */
    private Double temperature;

    /**
     * Maximum number of tokens to generate
     */
    private Integer maxTokens;

    /**
     * Top-p sampling parameter
     */
    private Double topP;

    /**
     * System prompt
     */
    private String systemPrompt;

    /**
     * Additional parameters
     */
    private Map<String, Object> parameters;
}
