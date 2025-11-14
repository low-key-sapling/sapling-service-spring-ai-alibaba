package com.sapling.framework.ai.alibaba.tool.builtin;

import com.sapling.framework.ai.alibaba.tool.annotation.AiTool;
import com.sapling.framework.ai.alibaba.tool.annotation.ToolParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Date and time tool.
 * 
 * @author Sapling Team
 * @since 1.0.0
 */
@Slf4j
@Component
public class DateTimeTool {

    @AiTool(
        name = "get_current_time",
        description = "获取当前时间",
        category = "utility"
    )
    public String getCurrentTime(
            @ToolParam(name = "format", description = "时间格式", required = false) String format) {
        String pattern = format != null ? format : "yyyy-MM-dd HH:mm:ss";
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern));
    }
}
