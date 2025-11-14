package com.sapling.framework.ai.alibaba.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Response model for embedding operations.
 * 
 * @author Sapling Team
 * @since 1.0.0
 */
@Data
@Builder
public class EmbeddingResponse {

    /**
     * Embedding vector
     */
    private List<Double> embedding;

    /**
     * Original text
     */
    private String text;

    /**
     * Request ID
     */
    private String requestId;

    /**
     * Token usage
     */
    private Usage usage;
}
