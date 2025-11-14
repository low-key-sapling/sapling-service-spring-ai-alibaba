package com.sapling.framework.ai.alibaba.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Response model for image generation operations.
 * 
 * @author Sapling Team
 * @since 1.0.0
 */
@Data
@Builder
public class ImageResponse {

    /**
     * Generated image URL
     */
    private String imageUrl;

    /**
     * Multiple generated image URLs
     */
    private List<String> imageUrls;

    /**
     * Request ID
     */
    private String requestId;

    /**
     * Task ID (for async generation)
     */
    private String taskId;

    /**
     * Task status
     */
    private String status;
}
