package com.sapling.framework.ai.alibaba.tool.builtin;

import com.sapling.framework.ai.alibaba.tool.annotation.AiTool;
import com.sapling.framework.ai.alibaba.tool.annotation.ToolParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * Calculator tool for mathematical calculations.
 * 
 * @author Sapling Team
 * @since 1.0.0
 */
@Slf4j
@Component
public class CalculatorTool {

    private final ScriptEngine engine;

    public CalculatorTool() {
        this.engine = new ScriptEngineManager().getEngineByName("JavaScript");
    }

    @AiTool(
        name = "calculate",
        description = "执行数学计算，支持基本的算术运算",
        category = "utility"
    )
    public Double calculate(
            @ToolParam(name = "expression", description = "数学表达式，如：2+2, 10*5") String expression) {
        log.info("Calculating expression: {}", expression);
        
        try {
            Object result = engine.eval(expression);
            return ((Number) result).doubleValue();
        } catch (Exception e) {
            log.error("Calculation error: {}", e.getMessage());
            throw new RuntimeException("Invalid expression: " + expression);
        }
    }
}
