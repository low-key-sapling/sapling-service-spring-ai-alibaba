package com.sapling.framework.ai.alibaba.core.image;

import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesis;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisParam;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisResult;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.sapling.framework.ai.alibaba.config.AlibabaAiProperties;
import com.sapling.framework.ai.alibaba.exception.AiException;
import com.sapling.framework.ai.alibaba.exception.ApiKeyInvalidException;
import com.sapling.framework.ai.alibaba.metrics.MetricsCollector;
import com.sapling.framework.ai.alibaba.model.ImageRequest;
import com.sapling.framework.ai.alibaba.model.ImageResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Alibaba AI implementation of ImageModel.
 * 
 * @author Sapling Team
 * @since 1.0.0
 */
@Slf4j
public class AlibabaAiImageModel implements ImageModel {

    private final String apiKey;
    private final String baseUrl;
    private final AlibabaAiProperties.ImageProperties imageProperties;
    private final MetricsCollector metricsCollector;
    private final ImageSynthesis imageSynthesis;

    public AlibabaAiImageModel(
            String apiKey,
            String baseUrl,
            AlibabaAiProperties.ImageProperties imageProperties,
            MetricsCollector metricsCollector) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.imageProperties = imageProperties;
        this.metricsCollector = metricsCollector;
        this.imageSynthesis = new ImageSynthesis();
    }

    @Override
    public ImageResponse generate(ImageRequest request) {
        if (request.getPrompt() == null || request.getPrompt().trim().isEmpty()) {
            throw new IllegalArgumentException("Prompt cannot be null or empty");
        }
        
        long startTime = System.currentTimeMillis();
        
        try {
            // Build parameters
            ImageSynthesisParam param = buildImageSynthesisParam(request);
            
            // Call API
            ImageSynthesisResult result = imageSynthesis.call(param);
            
            // Record metrics
            long duration = System.currentTimeMillis() - startTime;
            metricsCollector.recordCall(duration, 0);
            
            // Convert response
            return convertResponse(result);
            
        } catch (NoApiKeyException e) {
            metricsCollector.recordError();
            throw new ApiKeyInvalidException();
        } catch (ApiException e) {
            metricsCollector.recordError();
            throw new AiException("Image generation failed: " + e.getMessage(), e);
        } catch (Exception e) {
            metricsCollector.recordError();
            throw new AiException("Unexpected error during image generation", e);
        }
    }


    @Override
    public CompletableFuture<ImageResponse> generateAsync(ImageRequest request) {
        return CompletableFuture.supplyAsync(() -> generate(request));
    }

    /**
     * Build ImageSynthesisParam from ImageRequest
     */
    private ImageSynthesisParam buildImageSynthesisParam(ImageRequest request) {
        ImageSynthesisParam.ImageSynthesisParamBuilder<?, ?> builder = ImageSynthesisParam.builder()
            .apiKey(apiKey)
            .model(request.getModel() != null ? request.getModel() : imageProperties.getModel())
            .prompt(request.getPrompt());
        
        // Set optional parameters
        String size = request.getSize() != null ? request.getSize() : imageProperties.getSize();
        if (size != null) {
            builder.size(size);
        }
        
        Integer n = request.getN() != null ? request.getN() : imageProperties.getN();
        if (n != null) {
            builder.n(n);
        }
        
        String style = request.getStyle() != null ? request.getStyle() : imageProperties.getStyle();
        if (style != null && !style.equals("<auto>")) {
            builder.style(style);
        }
        
        if (request.getNegativePrompt() != null) {
            builder.negativePrompt(request.getNegativePrompt());
        }
        
        return builder.build();
    }

    /**
     * Convert ImageSynthesisResult to ImageResponse
     */
    private ImageResponse convertResponse(ImageSynthesisResult result) {
        if (result == null || result.getOutput() == null) {
            throw new AiException("Empty response from image generation API");
        }
        
        List<String> imageUrls = new ArrayList<>();
        if (result.getOutput().getResults() != null) {
            result.getOutput().getResults().forEach(item -> {
                if (item.get("url") != null) {
                    imageUrls.add(item.get("url"));
                }
            });
        }
        
        String imageUrl = imageUrls.isEmpty() ? null : imageUrls.get(0);
        
        return ImageResponse.builder()
            .imageUrl(imageUrl)
            .imageUrls(imageUrls)
            .requestId(result.getRequestId())
            .taskId(result.getOutput().getTaskId())
            .status(result.getOutput().getTaskStatus())
            .build();
    }
}
