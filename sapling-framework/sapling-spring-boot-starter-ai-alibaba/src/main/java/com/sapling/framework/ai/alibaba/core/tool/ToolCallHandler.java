package com.sapling.framework.ai.alibaba.core.tool;

import com.sapling.framework.ai.alibaba.model.ChatResponse;
import com.sapling.framework.ai.alibaba.model.ToolCall;
import com.sapling.framework.ai.alibaba.model.ToolResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Handler for processing tool calls from AI responses.
 * 
 * @author Sapling Team
 * @since 1.0.0
 */
@Slf4j
@Component
public class ToolCallHandler {

    private final ToolExecutor toolExecutor;

    public ToolCallHandler(ToolExecutor toolExecutor) {
        this.toolExecutor = toolExecutor;
    }

    /**
     * Handle tool calls from AI response
     */
    public List<ToolResult> handleToolCalls(ChatResponse response) {
        if (response == null || !response.hasToolCalls()) {
            return new ArrayList<>();
        }
        
        List<ToolResult> results = new ArrayList<>();
        
        for (ToolCall toolCall : response.getToolCalls()) {
            log.info("Processing tool call: {} ({})", toolCall.getName(), toolCall.getId());
            
            try {
                ToolResult result = toolExecutor.execute(
                    toolCall.getId(),
                    toolCall.getName(),
                    toolCall.getParameters()
                );
                results.add(result);
                
                if (result.isSuccess()) {
                    log.info("Tool call {} completed successfully", toolCall.getName());
                } else {
                    log.warn("Tool call {} failed: {}", toolCall.getName(), result.getError());
                }
                
            } catch (Exception e) {
                log.error("Error handling tool call {}: {}", toolCall.getName(), e.getMessage(), e);
                results.add(ToolResult.failure(
                    toolCall.getId(),
                    toolCall.getName(),
                    "Error: " + e.getMessage(),
                    0L
                ));
            }
        }
        
        return results;
    }

    /**
     * Check if response requires tool calls
     */
    public boolean requiresToolCalls(ChatResponse response) {
        return response != null && response.hasToolCalls();
    }
}
