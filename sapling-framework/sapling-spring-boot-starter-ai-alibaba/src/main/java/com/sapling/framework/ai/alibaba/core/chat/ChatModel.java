package com.sapling.framework.ai.alibaba.core.chat;

import com.sapling.framework.ai.alibaba.model.ChatRequest;
import com.sapling.framework.ai.alibaba.model.ChatResponse;
import reactor.core.publisher.Flux;

/**
 * Interface for chat model operations.
 * 
 * @author Sapling Team
 * @since 1.0.0
 */
public interface ChatModel {

    /**
     * Synchronous chat call
     * 
     * @param request chat request
     * @return chat response
     */
    ChatResponse call(ChatRequest request);

    /**
     * Streaming chat call
     * 
     * @param request chat request
     * @return flux of chat responses
     */
    Flux<ChatResponse> stream(ChatRequest request);
}
