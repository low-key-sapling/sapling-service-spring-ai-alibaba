package com.sapling.framework.ai.alibaba.tool.builtin;

import com.sapling.framework.ai.alibaba.tool.annotation.AiTool;
import com.sapling.framework.ai.alibaba.tool.annotation.ToolParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Weather query tool.
 * 
 * @author Sapling Team
 * @since 1.0.0
 */
@Slf4j
@Component
public class WeatherTool {

    @AiTool(
        name = "get_weather",
        description = "获取指定城市的实时天气信息，包括温度、天气状况、湿度等",
        category = "weather"
    )
    public WeatherInfo getWeather(
            @ToolParam(name = "city", description = "城市名称，如：北京、上海") String city) {
        log.info("Getting weather for city: {}", city);
        
        // TODO: Integrate with real weather API
        // This is a mock implementation
        return WeatherInfo.builder()
            .city(city)
            .temperature(25)
            .condition("晴天")
            .humidity(60)
            .windSpeed(15)
            .date(LocalDate.now())
            .build();
    }

    @AiTool(
        name = "get_weather_forecast",
        description = "获取指定城市未来几天的天气预报",
        category = "weather"
    )
    public List<WeatherForecast> getWeatherForecast(
            @ToolParam(name = "city", description = "城市名称") String city,
            @ToolParam(name = "days", description = "预报天数，默认7天", required = false) Integer days) {
        log.info("Getting weather forecast for city: {}, days: {}", city, days);
        
        int forecastDays = days != null ? days : 7;
        List<WeatherForecast> forecasts = new ArrayList<>();
        
        // TODO: Integrate with real weather API
        // This is a mock implementation
        for (int i = 0; i < forecastDays; i++) {
            forecasts.add(WeatherForecast.builder()
                .date(LocalDate.now().plusDays(i))
                .highTemp(25 + i)
                .lowTemp(15 + i)
                .condition(i % 2 == 0 ? "晴天" : "多云")
                .build());
        }
        
        return forecasts;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeatherInfo {
        private String city;
        private Integer temperature;
        private String condition;
        private Integer humidity;
        private Integer windSpeed;
        private LocalDate date;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeatherForecast {
        private LocalDate date;
        private Integer highTemp;
        private Integer lowTemp;
        private String condition;
    }
}
