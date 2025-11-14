package com.sapling.framework.ai.alibaba.config;

import com.sapling.framework.ai.alibaba.core.image.ImageModel;
import com.sapling.framework.ai.alibaba.core.image.AlibabaAiImageModel;
import com.sapling.framework.ai.alibaba.metrics.MetricsCollector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Image Model.
 * 
 * @author Sapling Team
 * @since 1.0.0
 */
@Slf4j
@Configuration
public class ImageModelConfiguration {

    /**
     * Create ImageModel bean for image generation functionality
     */
    @Bean
    @ConditionalOnMissingBean
    public ImageModel imageModel(
            AlibabaAiProperties properties,
            MetricsCollector metricsCollector) {
        log.info("Creating ImageModel bean with model: {}", properties.getImage().getModel());
        return new AlibabaAiImageModel(
            properties.getApiKey(),
            properties.getBaseUrl(),
            properties.getImage(),
            metricsCollector
        );
    }
}
