package com.sapling.framework.ai.alibaba.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Tool parameter definition model.
 * 
 * @author Sapling Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToolParameter {

    /**
     * Parameter name
     */
    private String name;

    /**
     * Parameter description
     */
    private String description;

    /**
     * Parameter type (string, number, boolean, object, array)
     */
    private String type;

    /**
     * Whether this parameter is required
     */
    private boolean required;

    /**
     * Default value
     */
    private Object defaultValue;

    /**
     * Enum values (if applicable)
     */
    private String[] enumValues;
}
