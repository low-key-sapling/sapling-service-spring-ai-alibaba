package com.sapling.framework.ai.alibaba.tool.annotation;

import java.lang.annotation.*;

/**
 * Annotation to describe a parameter of an AI tool method.
 * 
 * @author Sapling Team
 * @since 1.0.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ToolParam {

    /**
     * Parameter name
     */
    String name();

    /**
     * Parameter description (helps AI understand what this parameter is for)
     */
    String description();

    /**
     * Whether this parameter is required
     */
    boolean required() default true;

    /**
     * Default value (as string)
     */
    String defaultValue() default "";
}
