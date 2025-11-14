package com.sapling.framework.ai.alibaba.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Tool definition model for function calling.
 * 
 * @author Sapling Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToolDefinition {

    /**
     * Tool name
     */
    private String name;

    /**
     * Tool description
     */
    private String description;

    /**
     * Tool category
     */
    private String category;

    /**
     * Tool parameters
     */
    private List<ToolParameter> parameters;

    /**
     * Bean instance containing the tool method
     */
    private Object bean;

    /**
     * Method to invoke for this tool
     */
    private Method method;

    /**
     * Whether this tool is enabled
     */
    private boolean enabled;
}
