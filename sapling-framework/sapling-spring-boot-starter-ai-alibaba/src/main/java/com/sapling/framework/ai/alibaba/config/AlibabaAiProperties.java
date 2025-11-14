package com.sapling.framework.ai.alibaba.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.List;

/**
 * Configuration properties for Alibaba AI services.
 * 
 * @author Sapling Team
 * @since 1.0.0
 */
@Data
@Validated
@ConfigurationProperties(prefix = "spring.ai.alibaba")
public class AlibabaAiProperties {

    /**
     * Whether to enable Alibaba AI integration
     */
    private Boolean enabled = true;

    /**
     * Alibaba Cloud API Key for DashScope
     */
    @NotBlank(message = "API key is required")
    private String apiKey;

    /**
     * Base URL for DashScope API
     */
    private String baseUrl = "https://dashscope.aliyuncs.com/api/v1";

    /**
     * Chat model configuration
     */
    private ChatProperties chat = new ChatProperties();

    /**
     * Embedding model configuration
     */
    private EmbeddingProperties embedding = new EmbeddingProperties();

    /**
     * Image generation model configuration
     */
    private ImageProperties image = new ImageProperties();

    /**
     * Tool calling configuration
     */
    private ToolProperties tool = new ToolProperties();


    /**
     * Cache configuration
     */
    private CacheProperties cache = new CacheProperties();

    /**
     * Metrics configuration
     */
    private MetricsProperties metrics = new MetricsProperties();

    /**
     * Chat model properties
     */
    @Data
    public static class ChatProperties {
        /**
         * Model name (e.g., qwen-turbo, qwen-plus, qwen-max)
         */
        private String model = "qwen-turbo";

        /**
         * Temperature for response randomness (0.0 - 2.0)
         */
        private Double temperature = 0.7;

        /**
         * Maximum number of tokens to generate
         */
        @Positive
        private Integer maxTokens = 2000;

        /**
         * Top-p sampling parameter
         */
        private Double topP = 0.8;

        /**
         * Enable streaming responses
         */
        private Boolean enableStream = true;

        /**
         * Request timeout in seconds
         */
        @Positive
        private Integer timeout = 60;
    }

    /**
     * Embedding model properties
     */
    @Data
    public static class EmbeddingProperties {
        /**
         * Model name for text embedding
         */
        private String model = "text-embedding-v1";

        /**
         * Dimension of embedding vectors
         */
        @Positive
        private Integer dimensions = 1536;

        /**
         * Batch size for batch embedding
         */
        @Positive
        private Integer batchSize = 25;
    }

    /**
     * Image generation model properties
     */
    @Data
    public static class ImageProperties {
        /**
         * Model name for image generation
         */
        private String model = "wanx-v1";

        /**
         * Image size (e.g., 1024*1024, 720*1280, 1280*720)
         */
        private String size = "1024*1024";

        /**
         * Number of images to generate
         */
        @Positive
        private Integer n = 1;

        /**
         * Image style
         */
        private String style = "<auto>";
    }


    /**
     * Tool calling properties
     */
    @Data
    public static class ToolProperties {
        /**
         * Enable tool calling feature
         */
        private Boolean enabled = true;

        /**
         * List of enabled tool names
         */
        private List<String> enabledTools = new ArrayList<>();

        /**
         * Maximum retries for tool execution
         */
        @Positive
        private Integer maxRetries = 3;

        /**
         * Tool execution timeout in seconds
         */
        @Positive
        private Integer timeout = 30;

        /**
         * Enable tool permission control
         */
        private Boolean enablePermissionControl = false;
    }

    /**
     * Cache properties
     */
    @Data
    public static class CacheProperties {
        /**
         * Enable caching
         */
        private Boolean enabled = false;

        /**
         * Cache TTL in seconds
         */
        @Positive
        private Integer ttl = 3600;

        /**
         * Cache key prefix
         */
        private String keyPrefix = "ai:cache:";

        /**
         * Maximum cache size
         */
        @Positive
        private Integer maxSize = 1000;
    }

    /**
     * Metrics properties
     */
    @Data
    public static class MetricsProperties {
        /**
         * Enable metrics collection
         */
        private Boolean enabled = true;

        /**
         * Metrics export interval in seconds
         */
        @Positive
        private Integer exportInterval = 60;

        /**
         * Enable detailed metrics
         */
        private Boolean enableDetailed = false;

        /**
         * Slow call threshold in milliseconds
         */
        @Positive
        private Long slowCallThreshold = 5000L;
    }
}
