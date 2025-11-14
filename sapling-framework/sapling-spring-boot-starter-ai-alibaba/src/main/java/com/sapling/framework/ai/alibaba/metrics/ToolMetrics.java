package com.sapling.framework.ai.alibaba.metrics;

import lombok.Builder;
import lombok.Data;

/**
 * Tool-specific metrics data.
 * 
 * @author Sapling Team
 * @since 1.0.0
 */
@Data
@Builder
public class ToolMetrics {

    /**
     * Tool name
     */
    private String toolName;

    /**
     * Total number of tool calls
     */
    private long totalCalls;

    /**
     * Number of successful calls
     */
    private long successCalls;

    /**
     * Success rate (0.0 - 1.0)
     */
    private double successRate;

    /**
     * Average execution duration in milliseconds
     */
    private long avgDuration;
}
