package com.sapling.framework.ai.alibaba.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Tool call model representing a function call request from AI.
 * 
 * @author Sapling Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToolCall {

    /**
     * Unique identifier for this tool call
     */
    private String id;

    /**
     * Tool/function name to call
     */
    private String name;

    /**
     * Parameters for the tool call
     */
    private Map<String, Object> parameters;

    /**
     * Tool call type (usually "function")
     */
    private String type;
}
