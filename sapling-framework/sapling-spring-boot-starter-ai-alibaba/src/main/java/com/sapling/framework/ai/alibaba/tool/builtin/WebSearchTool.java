package com.sapling.framework.ai.alibaba.tool.builtin;

import com.sapling.framework.ai.alibaba.tool.annotation.AiTool;
import com.sapling.framework.ai.alibaba.tool.annotation.ToolParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Web search tool.
 * 
 * @author Sapling Team
 * @since 1.0.0
 */
@Slf4j
@Component
public class WebSearchTool {

    @AiTool(
        name = "web_search",
        description = "在网络上搜索信息",
        category = "search"
    )
    public List<SearchResult> search(
            @ToolParam(name = "query", description = "搜索关键词") String query,
            @ToolParam(name = "limit", description = "返回结果数量", required = false) Integer limit) {
        log.info("Searching for: {}", query);
        int resultLimit = limit != null ? limit : 5;
        
        // TODO: Integrate with real search API
        List<SearchResult> results = new ArrayList<>();
        for (int i = 0; i < resultLimit; i++) {
            results.add(SearchResult.builder()
                .title("搜索结果 " + (i + 1))
                .url("https://example.com/" + i)
                .snippet("这是关于 " + query + " 的搜索结果摘要")
                .build());
        }
        return results;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchResult {
        private String title;
        private String url;
        private String snippet;
    }
}
