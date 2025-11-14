package com.sapling.framework.ai.alibaba.config;

import com.sapling.framework.ai.alibaba.core.chat.ChatModel;
import com.sapling.framework.ai.alibaba.core.chat.AlibabaAiChatModel;
import com.sapling.framework.ai.alibaba.metrics.MetricsCollector;
import com.sapling.framework.ai.alibaba.interceptor.LoggingInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Chat Model.
 * 
 * @author Sapling Team
 * @since 1.0.0
 */
@Slf4j
@Configuration
public class ChatModelConfiguration {

    /**
     * Create ChatModel bean for AI chat functionality
     */
    @Bean
    @ConditionalOnMissingBean
    public ChatModel chatModel(
            AlibabaAiProperties properties,
            MetricsCollector metricsCollector,
            LoggingInterceptor loggingInterceptor) {
        log.info("Creating ChatModel bean with model: {}", properties.getChat().getModel());
        return new AlibabaAiChatModel(
            properties.getApiKey(),
            properties.getBaseUrl(),
            properties.getChat(),
            metricsCollector,
            loggingInterceptor
        );
    }
}
