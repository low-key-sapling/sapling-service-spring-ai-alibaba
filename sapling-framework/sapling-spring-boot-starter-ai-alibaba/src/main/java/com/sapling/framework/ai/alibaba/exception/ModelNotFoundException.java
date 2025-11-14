package com.sapling.framework.ai.alibaba.exception;

/**
 * Exception for model not found.
 * 
 * @author Sapling Team
 * @since 1.0.0
 */
public class ModelNotFoundException extends AiException {

    public ModelNotFoundException(String model) {
        super("MODEL_NOT_FOUND", "Model not found: " + model);
    }
}
