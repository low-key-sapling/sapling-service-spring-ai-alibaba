package com.sapling.framework.ai.alibaba.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Token usage information model.
 * 
 * @author Sapling Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usage {

    /**
     * Number of tokens in the prompt
     */
    private Integer promptTokens;

    /**
     * Number of tokens in the completion
     */
    private Integer completionTokens;

    /**
     * Total number of tokens used
     */
    private Integer totalTokens;

    /**
     * Calculate total tokens
     */
    public Integer getTotalTokens() {
        if (totalTokens != null) {
            return totalTokens;
        }
        if (promptTokens != null && completionTokens != null) {
            return promptTokens + completionTokens;
        }
        return 0;
    }
}
