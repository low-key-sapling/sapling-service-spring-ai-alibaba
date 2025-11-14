package com.sapling.framework.ai.alibaba.config;

import com.sapling.framework.ai.alibaba.core.chat.ChatModel;
import com.sapling.framework.ai.alibaba.core.chat.AlibabaAiChatModel;
import com.sapling.framework.ai.alibaba.core.embedding.EmbeddingModel;
import com.sapling.framework.ai.alibaba.core.embedding.AlibabaAiEmbeddingModel;
import com.sapling.framework.ai.alibaba.core.image.ImageModel;
import com.sapling.framework.ai.alibaba.core.image.AlibabaAiImageModel;
import com.sapling.framework.ai.alibaba.core.tool.ToolRegistry;
import com.sapling.framework.ai.alibaba.core.tool.ToolExecutor;
import com.sapling.framework.ai.alibaba.service.AlibabaAiService;
import com.sapling.framework.ai.alibaba.metrics.MetricsCollector;
import com.sapling.framework.ai.alibaba.cache.AiCacheManager;
import com.sapling.framework.ai.alibaba.interceptor.LoggingInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * Auto-configuration for Alibaba AI integration.
 * 
 * <p>This configuration class automatically configures all necessary beans
 * for Alibaba AI services when the module is present on the classpath.
 * 
 * @author Sapling Team
 * @since 1.0.0
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(AlibabaAiProperties.class)
@ConditionalOnClass(name = "com.alibaba.dashscope.aigc.generation.Generation")
@ConditionalOnProperty(prefix = "spring.ai.alibaba", name = "enabled", havingValue = "true", matchIfMissing = true)
@Import({
    ChatModelConfiguration.class,
    EmbeddingModelConfiguration.class,
    ImageModelConfiguration.class,
    ToolConfiguration.class
})
public class AlibabaAiAutoConfiguration {

    public AlibabaAiAutoConfiguration() {
        log.info("Initializing Alibaba AI Auto Configuration");
    }

    /**
     * Create MetricsCollector bean for collecting AI service metrics
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "spring.ai.alibaba.metrics", name = "enabled", havingValue = "true", matchIfMissing = true)
    public MetricsCollector metricsCollector(AlibabaAiProperties properties) {
        log.info("Creating MetricsCollector bean");
        return new MetricsCollector(properties.getMetrics());
    }


    /**
     * Create AiCacheManager bean for caching AI responses
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "spring.ai.alibaba.cache", name = "enabled", havingValue = "true")
    public AiCacheManager aiCacheManager(AlibabaAiProperties properties) {
        log.info("Creating AiCacheManager bean");
        return new AiCacheManager(properties.getCache());
    }

    /**
     * Create LoggingInterceptor bean for logging AI requests and responses
     */
    @Bean
    @ConditionalOnMissingBean
    public LoggingInterceptor loggingInterceptor() {
        log.info("Creating LoggingInterceptor bean");
        return new LoggingInterceptor();
    }

    /**
     * Create ToolRegistry bean for managing AI tools
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "spring.ai.alibaba.tool", name = "enabled", havingValue = "true", matchIfMissing = true)
    public ToolRegistry toolRegistry(ApplicationContext applicationContext, AlibabaAiProperties properties) {
        log.info("Creating ToolRegistry bean");
        return new ToolRegistry(applicationContext, properties.getTool());
    }

    /**
     * Create ToolExecutor bean for executing AI tools
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "spring.ai.alibaba.tool", name = "enabled", havingValue = "true", matchIfMissing = true)
    public ToolExecutor toolExecutor(ToolRegistry toolRegistry, MetricsCollector metricsCollector) {
        log.info("Creating ToolExecutor bean");
        return new ToolExecutor(toolRegistry, metricsCollector);
    }

    /**
     * Create unified AlibabaAiService bean
     */
    @Bean
    @ConditionalOnMissingBean
    public AlibabaAiService alibabaAiService(
            ChatModel chatModel,
            EmbeddingModel embeddingModel,
            ImageModel imageModel,
            ToolRegistry toolRegistry,
            ToolExecutor toolExecutor,
            AiCacheManager aiCacheManager) {
        log.info("Creating AlibabaAiService bean");
        return new AlibabaAiService(
            chatModel,
            embeddingModel,
            imageModel,
            toolRegistry,
            toolExecutor,
            aiCacheManager
        );
    }
}
