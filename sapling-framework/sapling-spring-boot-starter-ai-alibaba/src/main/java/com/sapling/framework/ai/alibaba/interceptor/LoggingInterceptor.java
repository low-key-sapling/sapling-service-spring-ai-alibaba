package com.sapling.framework.ai.alibaba.interceptor;

import com.sapling.framework.ai.alibaba.model.ChatRequest;
import com.sapling.framework.ai.alibaba.model.ChatResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * Logging interceptor for AI requests and responses.
 * 
 * @author Sapling Team
 * @since 1.0.0
 */
@Slf4j
public class LoggingInterceptor {

    private static final int MAX_LOG_LENGTH = 200;

    /**
     * Log before request
     */
    public void beforeRequest(ChatRequest request) {
        if (log.isDebugEnabled()) {
            String message = truncate(request.getMessage(), MAX_LOG_LENGTH);
            log.debug("AI Request - message: {}, temperature: {}, maxTokens: {}",
                message, request.getTemperature(), request.getMaxTokens());
        }
    }

    /**
     * Log after response
     */
    public void afterResponse(ChatResponse response, long duration) {
        if (log.isDebugEnabled()) {
            String content = truncate(response.getContent(), MAX_LOG_LENGTH);
            Integer tokens = response.getUsage() != null ? response.getUsage().getTotalTokens() : null;
            log.debug("AI Response - content: {}, tokens: {}, duration: {}ms, finishReason: {}",
                content, tokens, duration, response.getFinishReason());
        }
    }

    /**
     * Log on error
     */
    public void onError(Exception e) {
        log.error("AI Error: {}", e.getMessage(), e);
    }

    /**
     * Truncate string for logging
     */
    private String truncate(String str, int maxLength) {
        if (StringUtils.isEmpty(str)) {
            return "";
        }
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength) + "...";
    }
}
