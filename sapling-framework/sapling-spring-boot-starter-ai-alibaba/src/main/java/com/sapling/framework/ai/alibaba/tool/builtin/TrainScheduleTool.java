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
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Train schedule query tool.
 * 
 * @author Sapling Team
 * @since 1.0.0
 */
@Slf4j
@Component
public class TrainScheduleTool {

    @AiTool(
        name = "query_train_schedule",
        description = "查询火车车次的时刻表信息",
        category = "transportation"
    )
    public TrainSchedule queryTrainSchedule(
            @ToolParam(name = "trainNumber", description = "车次号，如：G1") String trainNumber,
            @ToolParam(name = "date", description = "出发日期，格式：YYYY-MM-DD", required = false) String date) {
        log.info("Querying train schedule for: {}, date: {}", trainNumber, date);
        
        // TODO: Integrate with real train API
        return TrainSchedule.builder()
            .trainNumber(trainNumber)
            .departure("北京南")
            .arrival("上海虹桥")
            .departureTime(LocalTime.of(8, 0))
            .arrivalTime(LocalTime.of(12, 30))
            .duration("4小时30分")
            .build();
    }

    @AiTool(
        name = "search_trains",
        description = "搜索两地之间的火车车次",
        category = "transportation"
    )
    public List<TrainInfo> searchTrains(
            @ToolParam(name = "from", description = "出发城市") String from,
            @ToolParam(name = "to", description = "到达城市") String to,
            @ToolParam(name = "date", description = "出发日期", required = false) String date) {
        log.info("Searching trains from {} to {}, date: {}", from, to, date);
        
        // TODO: Integrate with real train API
        List<TrainInfo> trains = new ArrayList<>();
        trains.add(TrainInfo.builder()
            .trainNumber("G1")
            .type("高铁")
            .departure(from)
            .arrival(to)
            .departureTime(LocalTime.of(8, 0))
            .arrivalTime(LocalTime.of(12, 30))
            .price(553)
            .build());
        
        return trains;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrainSchedule {
        private String trainNumber;
        private String departure;
        private String arrival;
        private LocalTime departureTime;
        private LocalTime arrivalTime;
        private String duration;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrainInfo {
        private String trainNumber;
        private String type;
        private String departure;
        private String arrival;
        private LocalTime departureTime;
        private LocalTime arrivalTime;
        private Integer price;
    }
}
