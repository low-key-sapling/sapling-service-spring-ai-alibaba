package com.sapling.framework.ai.alibaba.metrics;

import com.sapling.framework.ai.alibaba.config.AlibabaAiProperties;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * Collector for AI service metrics.
 * 
 * @author Sapling Team
 * @since 1.0.0
 */
@Slf4j
public class MetricsCollector {

    private final AlibabaAiProperties.MetricsProperties properties;
    
    private final AtomicLong totalCalls = new AtomicLong(0);
    private final AtomicLong successCalls = new AtomicLong(0);
    private final AtomicLong failedCalls = new AtomicLong(0);
    private final AtomicLong totalTokens = new AtomicLong(0);
    private final LongAdder totalDuration = new LongAdder();
    
    private final Map<String, AtomicLong> toolCallCounts = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> toolSuccessCounts = new ConcurrentHashMap<>();
    private final Map<String, LongAdder> toolDurations = new ConcurrentHashMap<>();

    public MetricsCollector(AlibabaAiProperties.MetricsProperties properties) {
        this.properties = properties;
    }

    /**
     * Record a successful AI call
     */
    public void recordCall(long duration, int tokens) {
        totalCalls.incrementAndGet();
        successCalls.incrementAndGet();
        totalDuration.add(duration);
        totalTokens.addAndGet(tokens);
        
        if (properties.getEnableDetailed() && duration > properties.getSlowCallThreshold()) {
            log.warn("Slow AI call detected: {}ms", duration);
        }
    }

    /**
     * Record a failed AI call
     */
    public void recordError() {
        totalCalls.incrementAndGet();
        failedCalls.incrementAndGet();
    }

    /**
     * Record a tool call
     */
    public void recordToolCall(String toolName, long duration, boolean success) {
        toolCallCounts.computeIfAbsent(toolName, k -> new AtomicLong(0)).incrementAndGet();
        toolDurations.computeIfAbsent(toolName, k -> new LongAdder()).add(duration);
        
        if (success) {
            toolSuccessCounts.computeIfAbsent(toolName, k -> new AtomicLong(0)).incrementAndGet();
        }
        
        if (properties.getEnableDetailed() && duration > properties.getSlowCallThreshold()) {
            log.warn("Slow tool call detected: {} - {}ms", toolName, duration);
        }
    }

    /**
     * Get current metrics
     */
    public AiMetrics getMetrics() {
        long calls = totalCalls.get();
        double successRate = calls > 0 ? (double) successCalls.get() / calls : 0.0;
        long avgDuration = calls > 0 ? totalDuration.sum() / calls : 0;
        
        return AiMetrics.builder()
            .totalCalls(calls)
            .successCalls(successCalls.get())
            .failedCalls(failedCalls.get())
            .successRate(successRate)
            .totalTokens(totalTokens.get())
            .avgDuration(avgDuration)
            .toolMetrics(buildToolMetrics())
            .build();
    }

    /**
     * Build tool metrics map
     */
    private Map<String, ToolMetrics> buildToolMetrics() {
        Map<String, ToolMetrics> metrics = new ConcurrentHashMap<>();
        
        toolCallCounts.forEach((toolName, count) -> {
            long calls = count.get();
            long successCount = toolSuccessCounts.getOrDefault(toolName, new AtomicLong(0)).get();
            long totalDur = toolDurations.getOrDefault(toolName, new LongAdder()).sum();
            long avgDur = calls > 0 ? totalDur / calls : 0;
            double successRate = calls > 0 ? (double) successCount / calls : 0.0;
            
            metrics.put(toolName, ToolMetrics.builder()
                .toolName(toolName)
                .totalCalls(calls)
                .successCalls(successCount)
                .successRate(successRate)
                .avgDuration(avgDur)
                .build());
        });
        
        return metrics;
    }

    /**
     * Reset all metrics
     */
    public void reset() {
        totalCalls.set(0);
        successCalls.set(0);
        failedCalls.set(0);
        totalTokens.set(0);
        totalDuration.reset();
        toolCallCounts.clear();
        toolSuccessCounts.clear();
        toolDurations.clear();
    }
}
