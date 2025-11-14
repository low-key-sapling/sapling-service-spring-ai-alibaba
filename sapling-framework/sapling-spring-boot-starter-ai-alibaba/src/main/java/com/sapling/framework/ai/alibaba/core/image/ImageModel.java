package com.sapling.framework.ai.alibaba.core.image;

import com.sapling.framework.ai.alibaba.model.ImageRequest;
import com.sapling.framework.ai.alibaba.model.ImageResponse;

import java.util.concurrent.CompletableFuture;

/**
 * Interface for image generation model operations.
 * 
 * @author Sapling Team
 * @since 1.0.0
 */
public interface ImageModel {

    /**
     * Generate image synchronously
     * 
     * @param request image generation request
     * @return image response
     */
    ImageResponse generate(ImageRequest request);

    /**
     * Generate image asynchronously
     * 
     * @param request image generation request
     * @return future of image response
     */
    CompletableFuture<ImageResponse> generateAsync(ImageRequest request);
}
