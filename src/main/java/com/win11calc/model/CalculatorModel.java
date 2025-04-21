package com.win11calc.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 计算器模型类，负责处理计算逻辑
 */
public class CalculatorModel {
    private String currentInput = "0";
    private String expression = "";
    private double memory = 0;
    private double result = 0;
    private boolean isNewInput = true;
    private boolean errorState = false;
    private List<String> history = new ArrayList<>();
    private CalculatorOperation currentOperation = null;
    private double leftOperand = 0;

    private List<String> rpnExpression = new ArrayList<>();
    
    /**
     * 获取当前输入
     * @return 当前输入的字符串
     */
    public String getCurrentInput() {
        return currentInput;
    }
    
    /**
     * 获取表达式
     * @return 当前表达式
     */
    public String getExpression() {
        return expression;
    }

    /**
     * 获取逆波兰表达式
     * @return 逆波兰表达式列表
     */
    public List<String> getRpnExpression() {
        return rpnExpression;
    }
    
    /**
     * 检查是否处于错误状态
     * @return 是否有错误
     */
    public boolean isErrorState() {
        return errorState;
    }
    
    /**
     * 获取计算历史记录
     * @return 历史记录列表
     */
    public List<String> getHistory() {
        return new ArrayList<>(history);
    }
    
    /**
     * 添加数字到当前输入
     * @param digit 数字字符
     */
    public void addDigit(String digit) {
        if (errorState) {
            clear();
        }
        
        if (isNewInput) {
            currentInput = digit;
            isNewInput = false;
        } else {
            // 避免前导多个0
            if (currentInput.equals("0") && !digit.equals(".")) {
                currentInput = digit;
            } else {
                currentInput += digit;
            }
        }
    }
    
    /**
     * 添加小数点
     */
    public void addDecimalPoint() {
        if (errorState) {
            clear();
        }
        
        if (isNewInput) {
            currentInput = "0.";
            isNewInput = false;
        } else if (!currentInput.contains(".")) {
            currentInput += ".";
        }
    }
    
    /**
     * 改变数值正负号
     */
    public void toggleSign() {
        if (!errorState) {
            if (currentInput.startsWith("-")) {
                currentInput = currentInput.substring(1);
            } else if (!currentInput.equals("0")) {
                currentInput = "-" + currentInput;
            }
        }
    }
    
    /**
     * 设置操作
     * @param operation 计算操作
     */
    public void setOperation(CalculatorOperation operation) {
        if (errorState) {
            return;
        }
        
        try {
            if (currentOperation != null) {
                calculateResult();
            } else {
                leftOperand = Double.parseDouble(currentInput);
                result = leftOperand;
            }
            
            currentOperation = operation;
            expression = formatNumber(result) + " " + operation.getSymbol() + " ";
            isNewInput = true;
        } catch (Exception e) {
            setErrorState("操作错误");
        }
    }
    
    /**
     * 计算结果
     */
    public void calculateResult() {
        if (errorState) {
            return;
        }
        
        try {
            double rightOperand = Double.parseDouble(currentInput);
            
            if (currentOperation != null) {
                switch (currentOperation) {
                    case ADD:
                        result = leftOperand + rightOperand;
                        break;
                    case SUBTRACT:
                        result = leftOperand - rightOperand;
                        break;
                    case MULTIPLY:
                        result = leftOperand * rightOperand;
                        break;
                    case DIVIDE:
                        if (rightOperand == 0) {
                            setErrorState("除以零错误");
                            return;
                        }
                        result = leftOperand / rightOperand;
                        break;
                }
                
                // 添加到历史记录
                String historyEntry = formatNumber(leftOperand) + " " + 
                                    currentOperation.getSymbol() + " " + 
                                    formatNumber(rightOperand) + " = " + 
                                    formatNumber(result);
                history.add(historyEntry);
                
                // 更新显示
                expression = historyEntry;
                currentInput = formatNumber(result);
                leftOperand = result;
                currentOperation = null;
            }
            
            isNewInput = true;
        } catch (Exception e) {
            setErrorState("计算错误");
        }
    }
    
    /**
     * 处理百分比操作
     */
    public void calculatePercent() {
        if (!errorState) {
            try {
                double value = Double.parseDouble(currentInput);
                
                if (currentOperation == null) {
                    // 没有操作，直接计算百分比
                    value = value / 100;
                } else {
                    // 作为操作数的百分比
                    value = leftOperand * (value / 100);
                }
                
                currentInput = formatNumber(value);
            } catch (Exception e) {
                setErrorState("百分比计算错误");
            }
        }
    }
    
    /**
     * 计算平方根
     */
    public void calculateSquareRoot() {
        if (!errorState) {
            try {
                double value = Double.parseDouble(currentInput);
                
                if (value < 0) {
                    setErrorState("负数不能开平方");
                    return;
                }
                
                double sqrtResult = Math.sqrt(value);
                expression = "√(" + formatNumber(value) + ") = " + formatNumber(sqrtResult);
                currentInput = formatNumber(sqrtResult);
                history.add(expression);
                isNewInput = true;
            } catch (Exception e) {
                setErrorState("计算错误");
            }
        }
    }
    
    /**
     * 计算平方
     */
    public void calculateSquare() {
        if (!errorState) {
            try {
                double value = Double.parseDouble(currentInput);
                double squareResult = value * value;
                expression = "sqr(" + formatNumber(value) + ") = " + formatNumber(squareResult);
                currentInput = formatNumber(squareResult);
                history.add(expression);
                isNewInput = true;
            } catch (Exception e) {
                setErrorState("计算错误");
            }
        }
    }
    
    /**
     * 计算倒数
     */
    public void calculateReciprocal() {
        if (!errorState) {
            try {
                double value = Double.parseDouble(currentInput);
                
                if (value == 0) {
                    setErrorState("除以零错误");
                    return;
                }
                
                double reciprocalResult = 1 / value;
                expression = "1/(" + formatNumber(value) + ") = " + formatNumber(reciprocalResult);
                currentInput = formatNumber(reciprocalResult);
                history.add(expression);
                isNewInput = true;
            } catch (Exception e) {
                setErrorState("计算错误");
            }
        }
    }
    
    /**
     * 计算三角函数
     * @param type 三角函数类型 (sin, cos, tan)
     */
    public void calculateTrigFunction(String type) {
        if (!errorState) {
            try {
                double value = Double.parseDouble(currentInput);
                double radians = Math.toRadians(value);
                double result = 0;
                
                switch (type.toLowerCase()) {
                    case "sin":
                        result = Math.sin(radians);
                        break;
                    case "cos":
                        result = Math.cos(radians);
                        break;
                    case "tan":
                        result = Math.tan(radians);
                        break;
                }
                
                expression = type + "(" + formatNumber(value) + ") = " + formatNumber(result);
                currentInput = formatNumber(result);
                history.add(expression);
                isNewInput = true;
            } catch (Exception e) {
                setErrorState("计算错误");
            }
        }
    }
    
    /**
     * 清除当前输入 (CE)
     */
    public void clearEntry() {
        currentInput = "0";
        isNewInput = true;
        errorState = false;
    }
    
    /**
     * 清除所有 (C)
     */
    public void clear() {
        currentInput = "0";
        expression = "";
        leftOperand = 0;
        result = 0;
        currentOperation = null;
        isNewInput = true;
        errorState = false;
    }
    
    /**
     * 退格
     */
    public void backspace() {
        if (!errorState && !isNewInput) {
            if (currentInput.length() > 1) {
                currentInput = currentInput.substring(0, currentInput.length() - 1);
            } else {
                currentInput = "0";
                isNewInput = true;
            }
        }
    }
    
    /**
     * 内存存储 (MS)
     */
    public void memoryStore() {
        if (!errorState) {
            try {
                memory = Double.parseDouble(currentInput);
            } catch (Exception e) {
                // 忽略错误
            }
        }
    }
    
    /**
     * 内存加 (M+)
     */
    public void memoryAdd() {
        if (!errorState) {
            try {
                memory += Double.parseDouble(currentInput);
            } catch (Exception e) {
                // 忽略错误
            }
        }
    }
    
    /**
     * 内存减 (M-)
     */
    public void memorySubtract() {
        if (!errorState) {
            try {
                memory -= Double.parseDouble(currentInput);
            } catch (Exception e) {
                // 忽略错误
            }
        }
    }
    
    /**
     * 内存调用 (MR)
     */
    public void memoryRecall() {
        if (!errorState) {
            currentInput = formatNumber(memory);
            isNewInput = true;
        }
    }
    
    /**
     * 内存清除 (MC)
     */
    public void memoryClear() {
        memory = 0;
    }
    
    /**
     * 设置错误状态
     * @param errorMessage 错误信息
     */
    private void setErrorState(String errorMessage) {
        errorState = true;
        currentInput = errorMessage;
        expression = "错误";
    }
    
    /**
     * 格式化数字显示
     * @param number 需要格式化的数字
     * @return 格式化后的字符串
     */
    private String formatNumber(double number) {
        // 移除不必要的小数部分
        if (number == (long) number) {
            return String.format("%d", (long) number);
        } else {
            return String.valueOf(number);
        }
    }
} 