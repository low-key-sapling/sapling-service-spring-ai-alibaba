package com.sapling.framework.ai.alibaba.core.embedding;

import com.alibaba.dashscope.embeddings.TextEmbedding;
import com.alibaba.dashscope.embeddings.TextEmbeddingParam;
import com.alibaba.dashscope.embeddings.TextEmbeddingResult;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.sapling.framework.ai.alibaba.config.AlibabaAiProperties;
import com.sapling.framework.ai.alibaba.exception.AiException;
import com.sapling.framework.ai.alibaba.exception.ApiKeyInvalidException;
import com.sapling.framework.ai.alibaba.metrics.MetricsCollector;
import com.sapling.framework.ai.alibaba.model.EmbeddingResponse;
import com.sapling.framework.ai.alibaba.model.Usage;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Alibaba AI implementation of EmbeddingModel.
 * 
 * @author Sapling Team
 * @since 1.0.0
 */
@Slf4j
public class AlibabaAiEmbeddingModel implements EmbeddingModel {

    private final String apiKey;
    private final String baseUrl;
    private final AlibabaAiProperties.EmbeddingProperties embeddingProperties;
    private final MetricsCollector metricsCollector;
    private final TextEmbedding textEmbedding;

    public AlibabaAiEmbeddingModel(
            String apiKey,
            String baseUrl,
            AlibabaAiProperties.EmbeddingProperties embeddingProperties,
            MetricsCollector metricsCollector) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.embeddingProperties = embeddingProperties;
        this.metricsCollector = metricsCollector;
        this.textEmbedding = new TextEmbedding();
    }

    @Override
    public EmbeddingResponse embed(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Text cannot be null or empty");
        }
        
        long startTime = System.currentTimeMillis();
        
        try {
            // Build parameters
            TextEmbeddingParam param = TextEmbeddingParam.builder()
                .apiKey(apiKey)
                .model(embeddingProperties.getModel())
                .texts(Collections.singletonList(text))
                .build();
            
            // Call API
            TextEmbeddingResult result = textEmbedding.call(param);
            
            // Record metrics
            long duration = System.currentTimeMillis() - startTime;
            if (result.getUsage() != null) {
                metricsCollector.recordCall(duration, result.getUsage().getTotalTokens());
            }
            
            // Convert response
            return convertResponse(result, text, 0);
            
        } catch (NoApiKeyException e) {
            metricsCollector.recordError();
            throw new ApiKeyInvalidException();
        } catch (ApiException e) {
            metricsCollector.recordError();
            throw new AiException("Embedding call failed: " + e.getMessage(), e);
        } catch (Exception e) {
            metricsCollector.recordError();
            throw new AiException("Unexpected error during embedding", e);
        }
    }


    @Override
    public List<EmbeddingResponse> embedBatch(List<String> texts) {
        if (texts == null || texts.isEmpty()) {
            throw new IllegalArgumentException("Texts list cannot be null or empty");
        }
        
        // Filter out null or empty texts
        List<String> validTexts = texts.stream()
            .filter(text -> text != null && !text.trim().isEmpty())
            .collect(Collectors.toList());
        
        if (validTexts.isEmpty()) {
            throw new IllegalArgumentException("No valid texts to embed");
        }
        
        // Process in batches
        List<EmbeddingResponse> allResponses = new ArrayList<>();
        int batchSize = embeddingProperties.getBatchSize();
        
        for (int i = 0; i < validTexts.size(); i += batchSize) {
            int end = Math.min(i + batchSize, validTexts.size());
            List<String> batch = validTexts.subList(i, end);
            
            List<EmbeddingResponse> batchResponses = processBatch(batch);
            allResponses.addAll(batchResponses);
        }
        
        return allResponses;
    }

    /**
     * Process a batch of texts
     */
    private List<EmbeddingResponse> processBatch(List<String> texts) {
        long startTime = System.currentTimeMillis();
        
        try {
            // Build parameters
            TextEmbeddingParam param = TextEmbeddingParam.builder()
                .apiKey(apiKey)
                .model(embeddingProperties.getModel())
                .texts(texts)
                .build();
            
            // Call API
            TextEmbeddingResult result = textEmbedding.call(param);
            
            // Record metrics
            long duration = System.currentTimeMillis() - startTime;
            if (result.getUsage() != null) {
                metricsCollector.recordCall(duration, result.getUsage().getTotalTokens());
            }
            
            // Convert responses
            List<EmbeddingResponse> responses = new ArrayList<>();
            if (result.getOutput() != null && result.getOutput().getEmbeddings() != null) {
                for (int i = 0; i < result.getOutput().getEmbeddings().size(); i++) {
                    responses.add(convertResponse(result, texts.get(i), i));
                }
            }
            
            return responses;
            
        } catch (NoApiKeyException e) {
            metricsCollector.recordError();
            throw new ApiKeyInvalidException();
        } catch (ApiException e) {
            metricsCollector.recordError();
            throw new AiException("Batch embedding call failed: " + e.getMessage(), e);
        } catch (Exception e) {
            metricsCollector.recordError();
            throw new AiException("Unexpected error during batch embedding", e);
        }
    }

    /**
     * Convert TextEmbeddingResult to EmbeddingResponse
     */
    private EmbeddingResponse convertResponse(TextEmbeddingResult result, String text, int index) {
        if (result == null || result.getOutput() == null || 
            result.getOutput().getEmbeddings() == null ||
            result.getOutput().getEmbeddings().size() <= index) {
            throw new AiException("Invalid embedding response");
        }
        
        List<Double> embedding = result.getOutput().getEmbeddings().get(index).getEmbedding();
        
        // Build usage
        Usage usage = null;
        if (result.getUsage() != null) {
            usage = Usage.builder()
                .totalTokens(result.getUsage().getTotalTokens())
                .build();
        }
        
        return EmbeddingResponse.builder()
            .embedding(embedding)
            .text(text)
            .requestId(result.getRequestId())
            .usage(usage)
            .build();
    }
}
