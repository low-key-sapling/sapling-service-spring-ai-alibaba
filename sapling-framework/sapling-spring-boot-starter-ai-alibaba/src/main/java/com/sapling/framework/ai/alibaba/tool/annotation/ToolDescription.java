package com.sapling.framework.ai.alibaba.tool.annotation;

import java.lang.annotation.*;

/**
 * Additional annotation to provide more detailed description for tools.
 * 
 * @author Sapling Team
 * @since 1.0.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ToolDescription {

    /**
     * Detailed description
     */
    String value();

    /**
     * Usage examples
     */
    String[] examples() default {};
}
