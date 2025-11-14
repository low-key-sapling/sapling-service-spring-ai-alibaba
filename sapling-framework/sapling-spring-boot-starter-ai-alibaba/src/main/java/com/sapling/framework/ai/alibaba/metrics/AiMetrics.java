package com.sapling.framework.ai.alibaba.metrics;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * AI service metrics data.
 * 
 * @author Sapling Team
 * @since 1.0.0
 */
@Data
@Builder
public class AiMetrics {

    /**
     * Total number of AI calls
     */
    private long totalCalls;

    /**
     * Number of successful calls
     */
    private long successCalls;

    /**
     * Number of failed calls
     */
    private long failedCalls;

    /**
     * Success rate (0.0 - 1.0)
     */
    private double successRate;

    /**
     * Total tokens used
     */
    private long totalTokens;

    /**
     * Average call duration in milliseconds
     */
    private long avgDuration;

    /**
     * Tool-specific metrics
     */
    private Map<String, ToolMetrics> toolMetrics;
}
