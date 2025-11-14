package com.sapling.framework.ai.alibaba.model;

import lombok.Builder;
import lombok.Data;

/**
 * Request model for image generation operations.
 * 
 * @author Sapling Team
 * @since 1.0.0
 */
@Data
@Builder
public class ImageRequest {

    /**
     * Text prompt for image generation
     */
    private String prompt;

    /**
     * Negative prompt (what to avoid)
     */
    private String negativePrompt;

    /**
     * Image size (e.g., 1024*1024, 720*1280, 1280*720)
     */
    private String size;

    /**
     * Number of images to generate
     */
    private Integer n;

    /**
     * Image style
     */
    private String style;

    /**
     * Model name
     */
    private String model;
}
