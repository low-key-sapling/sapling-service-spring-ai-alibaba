package com.sapling.framework.ai.alibaba.service;

import com.sapling.framework.ai.alibaba.cache.AiCacheManager;
import com.sapling.framework.ai.alibaba.core.chat.ChatModel;
import com.sapling.framework.ai.alibaba.core.embedding.EmbeddingModel;
import com.sapling.framework.ai.alibaba.core.image.ImageModel;
import com.sapling.framework.ai.alibaba.core.tool.ToolCallHandler;
import com.sapling.framework.ai.alibaba.core.tool.ToolExecutor;
import com.sapling.framework.ai.alibaba.core.tool.ToolRegistry;
import com.sapling.framework.ai.alibaba.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Unified service for Alibaba AI operations.
 * 
 * @author Sapling Team
 * @since 1.0.0
 */
@Slf4j
@Service
public class AlibabaAiService {

    private final ChatModel chatModel;
    private final EmbeddingModel embeddingModel;
    private final ImageModel imageModel;
    private final ToolRegistry toolRegistry;
    private final ToolExecutor toolExecutor;
    private final ToolCallHandler toolCallHandler;
    private final AiCacheManager aiCacheManager;

    public AlibabaAiService(
            ChatModel chatModel,
            EmbeddingModel embeddingModel,
            ImageModel imageModel,
            ToolRegistry toolRegistry,
            ToolExecutor toolExecutor,
            AiCacheManager aiCacheManager) {
        this.chatModel = chatModel;
        this.embeddingModel = embeddingModel;
        this.imageModel = imageModel;
        this.toolRegistry = toolRegistry;
        this.toolExecutor = toolExecutor;
        this.toolCallHandler = new ToolCallHandler(toolExecutor);
        this.aiCacheManager = aiCacheManager;
    }

    /**
     * Simple chat - single message
     */
    public String chat(String message) {
        ChatRequest request = ChatRequest.builder()
            .message(message)
            .build();
        ChatResponse response = chatModel.call(request);
        return response.getContent();
    }

    /**
     * Chat with history - multi-turn conversation
     */
    public String chat(List<Message> history, String message) {
        ChatRequest request = ChatRequest.builder()
            .history(history)
            .message(message)
            .build();
        ChatResponse response = chatModel.call(request);
        return response.getContent();
    }

    /**
     * Streaming chat
     */
    public Flux<String> chatStream(String message) {
        ChatRequest request = ChatRequest.builder()
            .message(message)
            .build();
        return chatModel.stream(request)
            .map(ChatResponse::getContent)
            .filter(Objects::nonNull);
    }


    /**
     * Chat with tools - function calling
     */
    public String chatWithTools(String message, List<String> enabledTools) {
        // Get tool definitions
        List<ToolDefinition> tools = enabledTools.stream()
            .map(toolRegistry::getTool)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        
        if (tools.isEmpty()) {
            log.warn("No valid tools found, falling back to regular chat");
            return chat(message);
        }
        
        // Build request with tools
        ChatRequest request = ChatRequest.builder()
            .message(message)
            .tools(tools)
            .build();
        
        // Call chat model
        ChatResponse response = chatModel.call(request);
        
        // Handle tool calls if present
        if (toolCallHandler.requiresToolCalls(response)) {
            return handleToolCallsAndRespond(message, response);
        }
        
        return response.getContent();
    }

    /**
     * Handle tool calls and get final response
     */
    private String handleToolCallsAndRespond(String originalMessage, ChatResponse response) {
        // Execute tool calls
        List<ToolResult> toolResults = toolCallHandler.handleToolCalls(response);
        
        // Build follow-up request with tool results
        ChatRequest followUpRequest = ChatRequest.builder()
            .message(originalMessage)
            .toolResults(toolResults)
            .build();
        
        // Get final response
        ChatResponse finalResponse = chatModel.call(followUpRequest);
        return finalResponse.getContent();
    }

    /**
     * Text embedding
     */
    public List<Double> embed(String text) {
        String cacheKey = "embed:" + text.hashCode();
        return aiCacheManager.get(cacheKey, () -> {
            EmbeddingResponse response = embeddingModel.embed(text);
            return response.getEmbedding();
        });
    }

    /**
     * Batch text embedding
     */
    public List<List<Double>> embedBatch(List<String> texts) {
        List<EmbeddingResponse> responses = embeddingModel.embedBatch(texts);
        return responses.stream()
            .map(EmbeddingResponse::getEmbedding)
            .collect(Collectors.toList());
    }

    /**
     * Generate image
     */
    public String generateImage(String prompt) {
        ImageRequest request = ImageRequest.builder()
            .prompt(prompt)
            .build();
        ImageResponse response = imageModel.generate(request);
        return response.getImageUrl();
    }

    /**
     * Generate image with options
     */
    public ImageResponse generateImage(ImageRequest request) {
        return imageModel.generate(request);
    }

    /**
     * Get available tools
     */
    public List<ToolDefinition> getAvailableTools() {
        return toolRegistry.getAllTools();
    }

    /**
     * Get tools by category
     */
    public List<ToolDefinition> getToolsByCategory(String category) {
        return toolRegistry.getToolsByCategory(category);
    }
}
