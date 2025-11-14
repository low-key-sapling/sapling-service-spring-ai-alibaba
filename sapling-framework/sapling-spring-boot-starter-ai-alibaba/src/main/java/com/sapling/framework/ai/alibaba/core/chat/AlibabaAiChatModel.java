package com.sapling.framework.ai.alibaba.core.chat;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.sapling.framework.ai.alibaba.config.AlibabaAiProperties;
import com.sapling.framework.ai.alibaba.exception.AiException;
import com.sapling.framework.ai.alibaba.exception.ApiKeyInvalidException;
import com.sapling.framework.ai.alibaba.interceptor.LoggingInterceptor;
import com.sapling.framework.ai.alibaba.metrics.MetricsCollector;
import com.sapling.framework.ai.alibaba.model.ChatRequest;
import com.sapling.framework.ai.alibaba.model.ChatResponse;
import com.sapling.framework.ai.alibaba.model.Usage;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Alibaba AI implementation of ChatModel.
 * 
 * @author Sapling Team
 * @since 1.0.0
 */
@Slf4j
public class AlibabaAiChatModel implements ChatModel {

    private final String apiKey;
    private final String baseUrl;
    private final AlibabaAiProperties.ChatProperties chatProperties;
    private final MetricsCollector metricsCollector;
    private final LoggingInterceptor loggingInterceptor;
    private final Generation generation;

    public AlibabaAiChatModel(
            String apiKey,
            String baseUrl,
            AlibabaAiProperties.ChatProperties chatProperties,
            MetricsCollector metricsCollector,
            LoggingInterceptor loggingInterceptor) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.chatProperties = chatProperties;
        this.metricsCollector = metricsCollector;
        this.loggingInterceptor = loggingInterceptor;
        this.generation = new Generation();
    }


    @Override
    public ChatResponse call(ChatRequest request) {
        long startTime = System.currentTimeMillis();
        
        try {
            // Log request
            loggingInterceptor.beforeRequest(request);
            
            // Build generation parameters
            GenerationParam param = buildGenerationParam(request, false);
            
            // Call DashScope API
            GenerationResult result = generation.call(param);
            
            // Convert response
            ChatResponse response = convertResponse(result);
            
            // Calculate duration
            long duration = System.currentTimeMillis() - startTime;
            
            // Record metrics
            if (response.getUsage() != null) {
                metricsCollector.recordCall(duration, response.getUsage().getTotalTokens());
            }
            
            // Log response
            loggingInterceptor.afterResponse(response, duration);
            
            // Check for slow calls
            if (duration > chatProperties.getTimeout() * 1000L) {
                log.warn("Slow chat call detected: {}ms", duration);
            }
            
            return response;
            
        } catch (NoApiKeyException e) {
            metricsCollector.recordError();
            loggingInterceptor.onError(e);
            throw new ApiKeyInvalidException();
        } catch (ApiException e) {
            metricsCollector.recordError();
            loggingInterceptor.onError(e);
            throw new AiException("Chat model call failed: " + e.getMessage(), e);
        } catch (Exception e) {
            metricsCollector.recordError();
            loggingInterceptor.onError(e);
            throw new AiException("Unexpected error during chat call", e);
        }
    }

    @Override
    public Flux<ChatResponse> stream(ChatRequest request) {
        // For now, return a simple Flux that wraps the synchronous call
        // TODO: Implement true streaming when DashScope SDK streaming API is available
        return Flux.defer(() -> {
            try {
                ChatResponse response = call(request);
                return Flux.just(response);
            } catch (Exception e) {
                return Flux.error(e);
            }
        });
    }


    /**
     * Build GenerationParam from ChatRequest
     */
    private GenerationParam buildGenerationParam(ChatRequest request, boolean streaming) {
        // Build messages
        List<Message> messages = new ArrayList<>();
        
        // Add system message if present
        if (request.getSystemPrompt() != null) {
            messages.add(Message.builder()
                .role(Role.SYSTEM.getValue())
                .content(request.getSystemPrompt())
                .build());
        }
        
        // Add history messages
        if (request.getHistory() != null && !request.getHistory().isEmpty()) {
            messages.addAll(request.getHistory().stream()
                .map(this::convertToApiMessage)
                .collect(Collectors.toList()));
        }
        
        // Add current user message
        if (request.getMessage() != null) {
            messages.add(Message.builder()
                .role(Role.USER.getValue())
                .content(request.getMessage())
                .build());
        }
        
        // Build param
        GenerationParam.GenerationParamBuilder<?, ?> builder = GenerationParam.builder()
            .apiKey(apiKey)
            .model(chatProperties.getModel())
            .messages(messages)
            .resultFormat(GenerationParam.ResultFormat.MESSAGE)
            .incrementalOutput(streaming);
        
        // Set optional parameters
        Double temperature = request.getTemperature() != null ? 
            request.getTemperature() : chatProperties.getTemperature();
        if (temperature != null) {
            builder.temperature(temperature.floatValue());
        }
        
        Integer maxTokens = request.getMaxTokens() != null ? 
            request.getMaxTokens() : chatProperties.getMaxTokens();
        if (maxTokens != null) {
            builder.maxTokens(maxTokens);
        }
        
        Double topP = request.getTopP() != null ? 
            request.getTopP() : chatProperties.getTopP();
        if (topP != null) {
            builder.topP(topP);
        }
        
        return builder.build();
    }

    /**
     * Convert internal Message to DashScope Message
     */
    private Message convertToApiMessage(com.sapling.framework.ai.alibaba.model.Message msg) {
        return Message.builder()
            .role(msg.getRole())
            .content(msg.getContent())
            .build();
    }

    /**
     * Convert GenerationResult to ChatResponse
     */
    private ChatResponse convertResponse(GenerationResult result) {
        if (result == null || result.getOutput() == null) {
            throw new AiException("Empty response from API");
        }
        
        String content = "";
        if (result.getOutput().getChoices() != null && !result.getOutput().getChoices().isEmpty()) {
            Message message = result.getOutput().getChoices().get(0).getMessage();
            if (message != null) {
                content = message.getContent();
            }
        }
        
        // Build usage
        Usage usage = null;
        if (result.getUsage() != null) {
            usage = Usage.builder()
                .promptTokens(result.getUsage().getInputTokens())
                .completionTokens(result.getUsage().getOutputTokens())
                .totalTokens(result.getUsage().getTotalTokens())
                .build();
        }
        
        return ChatResponse.builder()
            .content(content)
            .usage(usage)
            .finishReason(result.getOutput().getFinishReason())
            .requestId(result.getRequestId())
            .build();
    }
}
