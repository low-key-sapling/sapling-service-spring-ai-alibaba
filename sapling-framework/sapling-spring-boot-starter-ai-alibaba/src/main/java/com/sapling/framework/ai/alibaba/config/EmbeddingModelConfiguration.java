package com.sapling.framework.ai.alibaba.config;

import com.sapling.framework.ai.alibaba.core.embedding.EmbeddingModel;
import com.sapling.framework.ai.alibaba.core.embedding.AlibabaAiEmbeddingModel;
import com.sapling.framework.ai.alibaba.metrics.MetricsCollector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Embedding Model.
 * 
 * @author Sapling Team
 * @since 1.0.0
 */
@Slf4j
@Configuration
public class EmbeddingModelConfiguration {

    /**
     * Create EmbeddingModel bean for text embedding functionality
     */
    @Bean
    @ConditionalOnMissingBean
    public EmbeddingModel embeddingModel(
            AlibabaAiProperties properties,
            MetricsCollector metricsCollector) {
        log.info("Creating EmbeddingModel bean with model: {}", properties.getEmbedding().getModel());
        return new AlibabaAiEmbeddingModel(
            properties.getApiKey(),
            properties.getBaseUrl(),
            properties.getEmbedding(),
            metricsCollector
        );
    }
}
