package com.sapling.framework.ai.alibaba.core.embedding;

import com.sapling.framework.ai.alibaba.model.EmbeddingResponse;

import java.util.List;

/**
 * Interface for embedding model operations.
 * 
 * @author Sapling Team
 * @since 1.0.0
 */
public interface EmbeddingModel {

    /**
     * Embed single text
     * 
     * @param text text to embed
     * @return embedding response
     */
    EmbeddingResponse embed(String text);

    /**
     * Embed multiple texts in batch
     * 
     * @param texts texts to embed
     * @return list of embedding responses
     */
    List<EmbeddingResponse> embedBatch(List<String> texts);
}
