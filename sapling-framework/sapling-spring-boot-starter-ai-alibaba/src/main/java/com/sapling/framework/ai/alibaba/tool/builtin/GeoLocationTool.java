package com.sapling.framework.ai.alibaba.tool.builtin;

import com.sapling.framework.ai.alibaba.tool.annotation.AiTool;
import com.sapling.framework.ai.alibaba.tool.annotation.ToolParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Geolocation tool.
 * 
 * @author Sapling Team
 * @since 1.0.0
 */
@Slf4j
@Component
public class GeoLocationTool {

    @AiTool(
        name = "geocode_address",
        description = "将地址转换为经纬度坐标",
        category = "location"
    )
    public GeoCoordinate geocodeAddress(
            @ToolParam(name = "address", description = "地址") String address) {
        log.info("Geocoding address: {}", address);
        // TODO: Integrate with real geocoding API
        return GeoCoordinate.builder()
            .latitude(39.9042)
            .longitude(116.4074)
            .address(address)
            .build();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeoCoordinate {
        private Double latitude;
        private Double longitude;
        private String address;
    }
}
