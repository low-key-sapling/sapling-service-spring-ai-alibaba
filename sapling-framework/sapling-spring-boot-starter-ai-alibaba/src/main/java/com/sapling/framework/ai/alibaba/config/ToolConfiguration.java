package com.sapling.framework.ai.alibaba.config;

import com.sapling.framework.ai.alibaba.tool.builtin.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for built-in AI tools.
 * 
 * @author Sapling Team
 * @since 1.0.0
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "spring.ai.alibaba.tool", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ToolConfiguration {

    /**
     * Create WeatherTool bean for weather queries
     */
    @Bean
    @ConditionalOnMissingBean
    public WeatherTool weatherTool() {
        log.info("Creating WeatherTool bean");
        return new WeatherTool();
    }

    /**
     * Create TrainScheduleTool bean for train schedule queries
     */
    @Bean
    @ConditionalOnMissingBean
    public TrainScheduleTool trainScheduleTool() {
        log.info("Creating TrainScheduleTool bean");
        return new TrainScheduleTool();
    }

    /**
     * Create GeoLocationTool bean for geolocation services
     */
    @Bean
    @ConditionalOnMissingBean
    public GeoLocationTool geoLocationTool() {
        log.info("Creating GeoLocationTool bean");
        return new GeoLocationTool();
    }

    /**
     * Create DateTimeTool bean for date/time operations
     */
    @Bean
    @ConditionalOnMissingBean
    public DateTimeTool dateTimeTool() {
        log.info("Creating DateTimeTool bean");
        return new DateTimeTool();
    }

    /**
     * Create CalculatorTool bean for mathematical calculations
     */
    @Bean
    @ConditionalOnMissingBean
    public CalculatorTool calculatorTool() {
        log.info("Creating CalculatorTool bean");
        return new CalculatorTool();
    }

    /**
     * Create WebSearchTool bean for web search
     */
    @Bean
    @ConditionalOnMissingBean
    public WebSearchTool webSearchTool() {
        log.info("Creating WebSearchTool bean");
        return new WebSearchTool();
    }
}
