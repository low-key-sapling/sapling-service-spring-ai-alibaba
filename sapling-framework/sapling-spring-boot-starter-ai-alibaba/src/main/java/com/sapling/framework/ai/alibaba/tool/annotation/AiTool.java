package com.sapling.framework.ai.alibaba.tool.annotation;

import java.lang.annotation.*;

/**
 * Annotation to mark a method as an AI tool that can be called by the AI model.
 * 
 * @author Sapling Team
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AiTool {

    /**
     * Tool name (must be unique)
     */
    String name();

    /**
     * Tool description (helps AI understand when to use this tool)
     */
    String description();

    /**
     * Tool category for organization
     */
    String category() default "general";

    /**
     * Whether this tool is enabled
     */
    boolean enabled() default true;
}
