package com.sapling.framework.ai.alibaba.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Request model for embedding operations.
 * 
 * @author Sapling Team
 * @since 1.0.0
 */
@Data
@Builder
public class EmbeddingRequest {

    /**
     * Text to embed
     */
    private String text;

    /**
     * Multiple texts to embed
     */
    private List<String> texts;

    /**
     * Model name
     */
    private String model;
}
