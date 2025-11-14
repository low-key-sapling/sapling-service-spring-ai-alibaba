package com.sapling.framework.ai.alibaba.core.tool;

import com.sapling.framework.ai.alibaba.exception.ToolExecutionException;
import com.sapling.framework.ai.alibaba.metrics.MetricsCollector;
import com.sapling.framework.ai.alibaba.model.ToolDefinition;
import com.sapling.framework.ai.alibaba.model.ToolParameter;
import com.sapling.framework.ai.alibaba.model.ToolResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

/**
 * Executor for AI tools.
 * 
 * @author Sapling Team
 * @since 1.0.0
 */
@Slf4j
@Component
public class ToolExecutor {

    private final ToolRegistry toolRegistry;
    private final MetricsCollector metricsCollector;

    public ToolExecutor(ToolRegistry toolRegistry, MetricsCollector metricsCollector) {
        this.toolRegistry = toolRegistry;
        this.metricsCollector = metricsCollector;
    }

    /**
     * Execute a tool
     */
    public ToolResult execute(String toolCallId, String toolName, Map<String, Object> parameters) {
        long startTime = System.currentTimeMillis();
        
        try {
            // Get tool definition
            ToolDefinition tool = toolRegistry.getTool(toolName);
            if (tool == null) {
                throw new ToolExecutionException("Tool not found: " + toolName);
            }
            
            log.debug("Executing tool: {} with parameters: {}", toolName, parameters);
            
            // Validate parameters
            validateParameters(tool, parameters);
            
            // Prepare method arguments
            Object[] args = prepareArguments(tool, parameters);
            
            // Execute method
            Object result = tool.getMethod().invoke(tool.getBean(), args);
            
            // Calculate execution time
            long executionTime = System.currentTimeMillis() - startTime;
            
            // Record metrics
            metricsCollector.recordToolCall(toolName, executionTime, true);
            
            log.debug("Tool {} executed successfully in {}ms", toolName, executionTime);
            
            return ToolResult.success(toolCallId, toolName, result, executionTime);
            
        } catch (ToolExecutionException e) {
            long executionTime = System.currentTimeMillis() - startTime;
            metricsCollector.recordToolCall(toolName, executionTime, false);
            log.error("Tool execution failed: {}", e.getMessage());
            return ToolResult.failure(toolCallId, toolName, e.getMessage(), executionTime);
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            metricsCollector.recordToolCall(toolName, executionTime, false);
            log.error("Unexpected error executing tool {}: {}", toolName, e.getMessage(), e);
            return ToolResult.failure(toolCallId, toolName, 
                "Unexpected error: " + e.getMessage(), executionTime);
        }
    }

    /**
     * Validate parameters
     */
    private void validateParameters(ToolDefinition tool, Map<String, Object> parameters) {
        for (ToolParameter param : tool.getParameters()) {
            if (param.isRequired() && !parameters.containsKey(param.getName())) {
                throw new ToolExecutionException(
                    "Required parameter missing: " + param.getName());
            }
        }
    }

    /**
     * Prepare method arguments
     */
    private Object[] prepareArguments(ToolDefinition tool, Map<String, Object> parameters) {
        Method method = tool.getMethod();
        Parameter[] methodParams = method.getParameters();
        Object[] args = new Object[methodParams.length];
        
        for (int i = 0; i < methodParams.length; i++) {
            Parameter methodParam = methodParams[i];
            ToolParameter toolParam = tool.getParameters().get(i);
            
            Object value = parameters.get(toolParam.getName());
            
            // Use default value if not provided
            if (value == null && toolParam.getDefaultValue() != null) {
                value = convertValue(toolParam.getDefaultValue(), methodParam.getType());
            }
            
            // Convert value to correct type
            if (value != null) {
                args[i] = convertValue(value, methodParam.getType());
            } else {
                args[i] = null;
            }
        }
        
        return args;
    }

    /**
     * Convert value to target type
     */
    private Object convertValue(Object value, Class<?> targetType) {
        if (value == null) {
            return null;
        }
        
        // If already correct type
        if (targetType.isInstance(value)) {
            return value;
        }
        
        // Convert string to target type
        String strValue = value.toString();
        
        try {
            if (targetType == String.class) {
                return strValue;
            } else if (targetType == Integer.class || targetType == int.class) {
                return Integer.parseInt(strValue);
            } else if (targetType == Long.class || targetType == long.class) {
                return Long.parseLong(strValue);
            } else if (targetType == Double.class || targetType == double.class) {
                return Double.parseDouble(strValue);
            } else if (targetType == Float.class || targetType == float.class) {
                return Float.parseFloat(strValue);
            } else if (targetType == Boolean.class || targetType == boolean.class) {
                return Boolean.parseBoolean(strValue);
            } else {
                // For complex types, return as is
                return value;
            }
        } catch (Exception e) {
            throw new ToolExecutionException(
                "Failed to convert parameter value: " + e.getMessage());
        }
    }
}
