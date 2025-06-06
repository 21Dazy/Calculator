package com.dazycalc.model;

/**
 * 计算器操作枚举类
 */
public enum CalculatorOperation {
    ADD("+"),
    SUBTRACT("-"),
    MULTIPLY("×"),
    DIVIDE("÷"),
    MODULO("%"),
    POWER("^");
    
    private final String symbol;
    
    CalculatorOperation(String symbol) {
        this.symbol = symbol;
    }
    
    /**
     * 获取操作符号
     * @return 操作符号
     */
    public String getSymbol() {
        return symbol;
    }
} 