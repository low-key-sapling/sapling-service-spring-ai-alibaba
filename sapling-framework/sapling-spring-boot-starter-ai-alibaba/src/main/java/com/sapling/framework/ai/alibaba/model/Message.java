package com.sapling.framework.ai.alibaba.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Message model for conversation history.
 * 
 * @author Sapling Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    /**
     * Message role (user, assistant, system, tool)
     */
    private String role;

    /**
     * Message content
     */
    private String content;

    /**
     * Tool call ID (for tool messages)
     */
    private String toolCallId;

    /**
     * Tool name (for tool messages)
     */
    private String toolName;

    /**
     * Create a user message
     */
    public static Message user(String content) {
        return Message.builder()
            .role("user")
            .content(content)
            .build();
    }

    /**
     * Create an assistant message
     */
    public static Message assistant(String content) {
        return Message.builder()
            .role("assistant")
            .content(content)
            .build();
    }

    /**
     * Create a system message
     */
    public static Message system(String content) {
        return Message.builder()
            .role("system")
            .content(content)
            .build();
    }

    /**
     * Create a tool message
     */
    public static Message tool(String toolCallId, String toolName, String content) {
        return Message.builder()
            .role("tool")
            .toolCallId(toolCallId)
            .toolName(toolName)
            .content(content)
            .build();
    }
}
