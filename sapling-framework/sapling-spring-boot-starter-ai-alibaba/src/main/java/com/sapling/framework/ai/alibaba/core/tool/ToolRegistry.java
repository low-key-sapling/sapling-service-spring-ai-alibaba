package com.sapling.framework.ai.alibaba.core.tool;

import com.sapling.framework.ai.alibaba.config.AlibabaAiProperties;
import com.sapling.framework.ai.alibaba.model.ToolDefinition;
import com.sapling.framework.ai.alibaba.model.ToolParameter;
import com.sapling.framework.ai.alibaba.tool.annotation.AiTool;
import com.sapling.framework.ai.alibaba.tool.annotation.ToolParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry for managing AI tools.
 * 
 * @author Sapling Team
 * @since 1.0.0
 */
@Slf4j
@Component
public class ToolRegistry {

    private final Map<String, ToolDefinition> tools = new ConcurrentHashMap<>();
    private final ApplicationContext applicationContext;
    private final AlibabaAiProperties.ToolProperties toolProperties;

    public ToolRegistry(ApplicationContext applicationContext, AlibabaAiProperties.ToolProperties toolProperties) {
        this.applicationContext = applicationContext;
        this.toolProperties = toolProperties;
    }

    @PostConstruct
    public void scanTools() {
        log.info("Scanning for AI tools...");
        
        // Get all beans
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        int toolCount = 0;
        
        for (String beanName : beanNames) {
            try {
                Object bean = applicationContext.getBean(beanName);
                Class<?> beanClass = bean.getClass();
                
                // Scan methods for @AiTool annotation
                for (Method method : beanClass.getDeclaredMethods()) {
                    AiTool aiTool = method.getAnnotation(AiTool.class);
                    if (aiTool != null && aiTool.enabled()) {
                        registerTool(bean, method, aiTool);
                        toolCount++;
                    }
                }
            } catch (Exception e) {
                log.warn("Error scanning bean {}: {}", beanName, e.getMessage());
            }
        }
        
        log.info("Registered {} AI tools", toolCount);
    }

    /**
     * Register a tool
     */
    private void registerTool(Object bean, Method method, AiTool aiTool) {
        String toolName = aiTool.name();
        
        // Check if tool is in enabled list (if list is not empty)
        if (!toolProperties.getEnabledTools().isEmpty() && 
            !toolProperties.getEnabledTools().contains(toolName)) {
            log.debug("Tool {} is not in enabled list, skipping", toolName);
            return;
        }
        
        // Extract parameters
        List<ToolParameter> parameters = extractParameters(method);
        
        // Create tool definition
        ToolDefinition definition = ToolDefinition.builder()
            .name(toolName)
            .description(aiTool.description())
            .category(aiTool.category())
            .parameters(parameters)
            .bean(bean)
            .method(method)
            .enabled(true)
            .build();
        
        tools.put(toolName, definition);
        log.info("Registered tool: {} ({})", toolName, aiTool.category());
    }

    /**
     * Extract parameters from method
     */
    private List<ToolParameter> extractParameters(Method method) {
        List<ToolParameter> parameters = new ArrayList<>();
        
        for (Parameter parameter : method.getParameters()) {
            ToolParam toolParam = parameter.getAnnotation(ToolParam.class);
            if (toolParam != null) {
                ToolParameter param = ToolParameter.builder()
                    .name(toolParam.name())
                    .description(toolParam.description())
                    .type(getParameterType(parameter.getType()))
                    .required(toolParam.required())
                    .defaultValue(toolParam.defaultValue().isEmpty() ? null : toolParam.defaultValue())
                    .build();
                parameters.add(param);
            }
        }
        
        return parameters;
    }

    /**
     * Get parameter type string
     */
    private String getParameterType(Class<?> type) {
        if (type == String.class) {
            return "string";
        } else if (type == Integer.class || type == int.class || 
                   type == Long.class || type == long.class) {
            return "number";
        } else if (type == Boolean.class || type == boolean.class) {
            return "boolean";
        } else if (type == Double.class || type == double.class || 
                   type == Float.class || type == float.class) {
            return "number";
        } else if (type.isArray() || Collection.class.isAssignableFrom(type)) {
            return "array";
        } else {
            return "object";
        }
    }

    /**
     * Get tool by name
     */
    public ToolDefinition getTool(String name) {
        return tools.get(name);
    }

    /**
     * Get all tools
     */
    public List<ToolDefinition> getAllTools() {
        return new ArrayList<>(tools.values());
    }

    /**
     * Get tools by category
     */
    public List<ToolDefinition> getToolsByCategory(String category) {
        List<ToolDefinition> result = new ArrayList<>();
        for (ToolDefinition tool : tools.values()) {
            if (category.equals(tool.getCategory())) {
                result.add(tool);
            }
        }
        return result;
    }

    /**
     * Check if tool exists
     */
    public boolean hasTool(String name) {
        return tools.containsKey(name);
    }

    /**
     * Get tool count
     */
    public int getToolCount() {
        return tools.size();
    }
}
