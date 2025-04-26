package com.win11calc.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * 计算器模型类，负责处理计算逻辑
 */
public class CalculatorModel {
    private String currentInput = "0";
    private String displayExpression = ""; // 用于显示的表达式
    private String currentResult = "0"; // 当前计算结果
    private double memory = 0;
    private boolean isNewInput = true;
    private boolean errorState = false;
    private List<String> history = new ArrayList<>();
    private boolean replaceExpressionWithResult = false; // 等号按下后，用结果替换表达式

    private List<String> tokens = new ArrayList<>(); // 存储表达式的token
    
    // 函数输入支持
    private boolean inFunctionInput = false; // 是否在输入函数参数
    private String currentFunction = ""; // 当前正在输入参数的函数
    private String functionArgument = ""; // 函数参数

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
        return displayExpression;
    }

    /**
     * 获取当前计算结果
     * @return 当前计算结果
     */
    public String getCurrentResult() {
        return currentResult;
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
        
        if (replaceExpressionWithResult) {
            // 当等号被按下后，如果输入新数字，应该清除之前的所有计算
            clear();
            replaceExpressionWithResult = false;
        }
        
        // 如果在函数输入模式中，将数字添加到函数参数中
        if (inFunctionInput) {
            if (functionArgument.equals("0")) {
                functionArgument = digit;
            } else {
                functionArgument += digit;
            }
            // 更新表达式显示
            updateFunctionExpression();
            return;
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
        
        // 更新表达式
        updateExpression();
        
        // 尝试实时计算结果
        calculateInRealTime();
    }
    
    /**
     * 添加小数点
     */
    public void addDecimalPoint() {
        if (errorState) {
            clear();
        }
        
        if (replaceExpressionWithResult) {
            // 如果显示结果状态，清除之前的运算
            clear();
            replaceExpressionWithResult = false;
        }
        
        // 如果在函数输入模式中，添加小数点到函数参数
        if (inFunctionInput) {
            if (!functionArgument.contains(".")) {
                if (functionArgument.isEmpty()) {
                    functionArgument = "0.";
                } else {
                    functionArgument += ".";
                }
                // 更新表达式显示
                updateFunctionExpression();
            }
            return;
        }
        
        if (isNewInput) {
            currentInput = "0.";
            isNewInput = false;
        } else if (!currentInput.contains(".")) {
            currentInput += ".";
        }
        
        // 更新表达式
        updateExpression();
    }
    
    /**
     * 改变数值正负号
     */
    public void toggleSign() {
        if (!errorState) {
            // 如果在函数输入模式中，切换函数参数的正负号
            if (inFunctionInput) {
                if (functionArgument.startsWith("-")) {
                    functionArgument = functionArgument.substring(1);
                } else if (!functionArgument.equals("0") && !functionArgument.isEmpty()) {
                    functionArgument = "-" + functionArgument;
                }
                // 更新表达式显示
                updateFunctionExpression();
                return;
            }
            
            if (currentInput.startsWith("-")) {
                currentInput = currentInput.substring(1);
            } else if (!currentInput.equals("0")) {
                currentInput = "-" + currentInput;
            }
            
            // 更新表达式
            updateExpression();
            
            // 尝试实时计算结果
            calculateInRealTime();
        }
    }
    
    /**
     * 设置操作
     * @param operation 计算操作
     */
    public void setOperation(CalculatorOperation operation) {
        System.err.println("tokens: " + tokens);
        System.err.println("isNewInput " + isNewInput);
        if (errorState) {
            return;
        }
        
        // 如果当前正在输入函数参数，先完成函数计算
        if (inFunctionInput) {
            completeFunctionInput();
        }
        
        try {
            // 如果处于替换状态，取消替换状态
            if (replaceExpressionWithResult) {
                displayExpression = currentInput;
                replaceExpressionWithResult = false;
            }
            
            // 确保当前输入被添加到tokens中
            if (!isNewInput) {
                tokens.add(currentInput);
                isNewInput = true;
            } else if (tokens.isEmpty()) {
                // 如果没有当前输入但tokens为空，添加0
                tokens.add("0");
            } else if (isOperator(tokens.get(tokens.size() - 1))) {
                // 如果最后一个token是操作符，用新操作符替换它
                tokens.set(tokens.size() - 1, operation.getSymbol());
                updateExpressionFromTokens();
                return;
            }
            
            // 添加操作符到tokens
            tokens.add(operation.getSymbol());
            
            // 更新表达式
            updateExpressionFromTokens();
            
            // 尝试实时计算结果，但不让它覆盖当前的表达式
            double tmpResult = 0;
            try {
                // 创建一个临时tokens列表进行计算
                List<String> tempTokens = new ArrayList<>(tokens);
                
                // 移除最后一个操作符用于计算
                if (!tempTokens.isEmpty() && isOperator(tempTokens.get(tempTokens.size() - 1))) {
                    tempTokens.remove(tempTokens.size() - 1);
                }
                
                // 确保表达式有效后再计算
                if (isValidExpression(tempTokens)) {
                    // 将中缀表达式转换为后缀表达式
                    List<String> rpnTokens = convertToRPN(tempTokens);
                    
                    // 计算逆波兰表达式
                    tmpResult = evaluateRPN(rpnTokens);
                    
                    // 更新当前结果
                    currentResult = formatNumber(tmpResult);
                }
            } catch (Exception e) {
                // 计算错误不影响当前操作
                currentResult = "";
            }
        } catch (Exception e) {
            setErrorState("操作错误: " + e.getMessage());
        }
    }
    
    /**
     * 添加左括号
     */
    public void addLeftParenthesis() {
        if (errorState) {
            clear();
        }
        
        if (replaceExpressionWithResult) {
            // 如果处于结果显示状态，清除
            clear();
            replaceExpressionWithResult = false;
        }
        
        // 如果当前正在输入数字，先将数字加入tokens
        if (!isNewInput) {
            tokens.add(currentInput);
            isNewInput = true;
        }
        
        // 添加左括号
        tokens.add("(");
        
        // 更新表达式
        updateExpressionFromTokens();
    }
    
    /**
     * 添加右括号
     */
    public void addRightParenthesis() {
        if (errorState) {
            return;
        }
        
        // 计算左右括号数量，确保有未匹配的左括号
        int leftCount = 0;
        int rightCount = 0;
        for (String token : tokens) {
            if (token.equals("(")) leftCount++;
            if (token.equals(")")) rightCount++;
        }
        
        if (leftCount <= rightCount) {
            // 如果没有未匹配的左括号，忽略此操作
            return;
        }
        
        // 如果当前正在输入数字，先将数字加入tokens
        if (!isNewInput) {
            tokens.add(currentInput);
            isNewInput = true;
        }
        
        // 添加右括号
        tokens.add(")");
        
        // 更新表达式
        updateExpressionFromTokens();
        
        // 尝试实时计算结果
        calculateInRealTime();
    }
    
    /**
     * 实时计算表达式的结果
     */
    private void calculateInRealTime() {
        if (tokens.isEmpty()) {
            currentResult = currentInput;
            return;
        }
        
        // 创建一个临时tokens列表进行计算
        List<String> tempTokens = new ArrayList<>(tokens);
        
        // 如果当前有未添加的输入，添加到临时列表
        if (!isNewInput && !tokens.isEmpty() && !isOperator(tokens.get(tokens.size() - 1))) {
            tempTokens.add(currentInput);
        }
        
        try {
            // 确保表达式有效：括号匹配且不以操作符结尾
            if (isValidExpression(tempTokens)) {
                // 将中缀表达式转换为后缀表达式
                List<String> rpnTokens = convertToRPN(tempTokens);
                
                // 计算逆波兰表达式
                double result = evaluateRPN(rpnTokens);
                
                // 更新当前结果
                currentResult = formatNumber(result);
            } else {
                // 表达式不完整，不计算结果
                currentResult = "";
            }
        } catch (Exception e) {
            // 计算过程中出现错误，不显示结果
            currentResult = "";
        }
    }
    
    /**
     * 检查表达式是否有效
     */
    private boolean isValidExpression(List<String> exprTokens) {
        if (exprTokens.isEmpty()) return false;
        
        // 检查左右括号是否匹配
        int parenCount = 0;
        for (String token : exprTokens) {
            if (token.equals("(")) parenCount++;
            else if (token.equals(")")) {
                parenCount--;
                if (parenCount < 0) return false; // 右括号多于左括号
            }
        }
        if (parenCount != 0) return false; // 括号不匹配
        
        // 检查是否以操作符结尾
        String lastToken = exprTokens.get(exprTokens.size() - 1);
        if (isOperator(lastToken)) return false;
        
        return true;
    }
    
    /**
     * 计算结果 - 使用逆波兰表达式计算
     */
    public void calculateResult() {
        if (errorState) {
            return;
        }
        
        // 如果当前在输入函数参数，先完成函数计算
        if (inFunctionInput) {
            completeFunctionInput();
            return;
        }
        
        try {
            // 如果当前有输入但未添加到tokens，添加它
            if (!isNewInput) {
                tokens.add(currentInput);
                isNewInput = true;
                updateExpressionFromTokens();
            }
            
            // 如果tokens为空，直接返回
            if (tokens.isEmpty()) {
                return;
            }
            
            // 检查表达式有效性
            if (!isValidExpression(tokens)) {
                setErrorState("表达式不完整");
                return;
            }
            
            // 保存原始表达式用于历史记录
            String originalExpression = displayExpression;
            
            try {
                // 将中缀表达式转换为后缀表达式（逆波兰表达式）
                List<String> rpnTokens = convertToRPN(tokens);
                
                // 计算逆波兰表达式
                double result = evaluateRPN(rpnTokens);
                
                // 检查结果是否有效
                if (Double.isInfinite(result) || Double.isNaN(result)) {
                    setErrorState("计算结果无效");
                    return;
                }
                
                // 格式化结果
                String formattedResult = formatNumber(result);
                
                // 添加到历史记录
                String historyEntry = originalExpression + " = " + formattedResult;
                history.add(historyEntry);
                
                // 更新显示
                currentInput = formattedResult;
                currentResult = formattedResult;
                
                // 标记为等号已按下，下次输入数字时应该清除表达式
                replaceExpressionWithResult = true;
                
                // 清空tokens，为下次计算做准备，但保留当前结果
                tokens.clear();
                tokens.add(formattedResult);
                
                // 更新表达式为结果
                displayExpression = formattedResult;
            } catch (Exception e) {
                setErrorState("计算错误: " + e.getMessage());
            }
        } catch (Exception e) {
            setErrorState("计算错误: " + e.getMessage());
        }
    }
    
    /**
     * 从tokens更新表达式
     */
    private void updateExpressionFromTokens() {
        StringBuilder sb = new StringBuilder();
        for (String token : tokens) {
            if (isOperator(token)) {
                sb.append(" ").append(token).append(" ");
            } else {
                sb.append(token);
            }
        }
        displayExpression = sb.toString().trim();
        System.err.println("更新表达式: " + displayExpression);
    }
    
    /**
     * 更新表达式
     */
    private void updateExpression() {
        if (tokens.isEmpty()) {
            displayExpression = currentInput;
        } else {
            // 构建除当前输入外的表达式部分
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < tokens.size(); i++) {
                String token = tokens.get(i);
                if (isOperator(token)) {
                    sb.append(" ").append(token).append(" ");
                } else {
                    sb.append(token);
                }
            }
            
            // 如果最后一个token是操作符，添加当前输入
            if (!isNewInput && !tokens.isEmpty() && isOperator(tokens.get(tokens.size() - 1))) {
                sb.append(currentInput);
            }
            
            displayExpression = sb.toString().trim();
        }
    }
    
    /**
     * 将中缀表达式转换为逆波兰表达式
     * @param tokens 中缀表达式token列表
     * @return 逆波兰表达式token列表
     */
    private List<String> convertToRPN(List<String> tokens) {
        // 确保输入有效
        for (String token : tokens) {
            // 检查所有token是否为操作符、括号或可解析为数字
            if (!isOperator(token) && !token.equals("(") && !token.equals(")")) {
                try {
                    Double.parseDouble(token);
                } catch (NumberFormatException e) {
                    // 如果既不是操作符、括号也不是数字，则抛出异常
                    throw new RuntimeException("无效的表达式: 包含非法字符 '" + token + "'");
                }
            }
        }
        
        List<String> output = new ArrayList<>();
        Stack<String> operatorStack = new Stack<>();
        
        for (String token : tokens) {
            if (isOperator(token)) {
                while (!operatorStack.isEmpty() && 
                       isOperator(operatorStack.peek()) && 
                       getPrecedence(operatorStack.peek()) >= getPrecedence(token)) {
                    output.add(operatorStack.pop());
                }
                operatorStack.push(token);
            } else if (token.equals("(")) {
                operatorStack.push(token);
            } else if (token.equals(")")) {
                while (!operatorStack.isEmpty() && !operatorStack.peek().equals("(")) {
                    output.add(operatorStack.pop());
                }
                if (!operatorStack.isEmpty() && operatorStack.peek().equals("(")) {
                    operatorStack.pop(); // 弹出左括号
                } else {
                    throw new RuntimeException("括号不匹配");
                }
            } else {
                // 数字直接添加到输出
                output.add(token);
            }
        }
        
        // 将剩余的操作符添加到输出
        while (!operatorStack.isEmpty()) {
            if (operatorStack.peek().equals("(")) {
                throw new RuntimeException("括号不匹配");
            }
            output.add(operatorStack.pop());
        }
        
        return output;
    }
    
    /**
     * 判断是否为操作符
     * @param token 待检查的token
     * @return 是否为操作符
     */
    private boolean isOperator(String token) {
        return token.equals("+") || token.equals("-") || 
               token.equals("×") || token.equals("÷");
    }
    
    /**
     * 获取操作符优先级
     * @param operator 操作符
     * @return 优先级（数字越大优先级越高）
     */
    private int getPrecedence(String operator) {
        switch (operator) {
            case "+":
            case "-":
                return 1;
            case "×":
            case "÷":
                return 2;
            default:
                return 0;
        }
    }
    
    /**
     * 计算逆波兰表达式的值
     * @param rpnTokens 逆波兰表达式token列表
     * @return 计算结果
     */
    private double evaluateRPN(List<String> rpnTokens) {
        Stack<Double> stack = new Stack<>();
        
        for (String token : rpnTokens) {
            if (isOperator(token)) {
                if (stack.size() < 2) {
                    throw new RuntimeException("表达式错误");
                }
                double b = stack.pop();
                double a = stack.pop();
                switch (token) {
                    case "+":
                        stack.push(a + b);
                        break;
                    case "-":
                        stack.push(a - b);
                        break;
                    case "×":
                        stack.push(a * b);
                        break;
                    case "÷":
                        if (b == 0) {
                            throw new RuntimeException("不能除以0");
                        }
                        stack.push(a / b);
                        break;
                }
            } else {
                stack.push(Double.parseDouble(token));
            }
        }
        
        if (stack.size() != 1) {
            throw new RuntimeException("表达式错误");
        }
        
        return stack.pop();
    }
    
    /**
     * 处理百分比操作
     */
    public void calculatePercent() {
        if (!errorState) {
            try {
                double value = Double.parseDouble(currentInput);
                value = value / 100;
                currentInput = formatNumber(value);
                
                // 更新表达式
                updateExpression();
                
                // 尝试实时计算结果
                calculateInRealTime();
            } catch (Exception e) {
                setErrorState("百分比计算错误");
            }
        }
    }
    
    /**
     * 计算平方根
     */
    public void calculateSquareRoot() {
        if (errorState) {
            clear();
        }
        
        // 如果当前在输入函数参数，先完成上一个函数的计算
        if (inFunctionInput) {
            completeFunctionInput();
        }
        
        // 如果当前正在输入一个数字（不是新输入状态），先将其添加到tokens
        if (!isNewInput && !tokens.isEmpty() && !isOperator(tokens.get(tokens.size() - 1))) {
            tokens.add(currentInput);
            isNewInput = true;
        }
        
        // 进入平方根函数输入模式
        inFunctionInput = true;
        currentFunction = "√";
        functionArgument = "";
        
        // 更新表达式显示
        updateFunctionExpression();
    }
    
    /**
     * 计算平方
     */
    public void calculateSquare() {
        if (errorState) {
            clear();
        }
        
        // 如果当前在输入函数参数，先完成上一个函数的计算
        if (inFunctionInput) {
            completeFunctionInput();
        }
        
        try {
            double value = Double.parseDouble(currentInput);
            double squareResult = value * value;
            
            // 保存计算前的表达式
            String functionExpr = formatNumber(value) + "²";
            
            // 更新计算结果
            currentInput = formatNumber(squareResult);
            
            // 更新表达式显示
            if (tokens.isEmpty()) {
                // 如果tokens为空，这是一个全新的计算
                displayExpression = functionExpr;
            } else if (isOperator(tokens.get(tokens.size() - 1))) {
                // 如果最后一个token是操作符，表达式已经包含操作符，添加函数表达式
                displayExpression += functionExpr;
            } else {
                // 其他情况，例如在一个数字后点击了函数按钮，替换最后一个token
                tokens.remove(tokens.size() - 1);
                displayExpression = buildExpressionFromTokens(tokens) + functionExpr;
            }
            
            // 设置为非新输入状态，让操作符处理添加结果到tokens
            isNewInput = false;
            
        } catch (Exception e) {
            setErrorState("计算错误");
        }
    }
    
    /**
     * 计算倒数
     */
    public void calculateReciprocal() {
        if (errorState) {
            clear();
        }
        
        // 如果当前在输入函数参数，先完成上一个函数的计算
        if (inFunctionInput) {
            completeFunctionInput();
        }
        
        try {
            double value = Double.parseDouble(currentInput);
            
            if (value == 0) {
                setErrorState("不能除以0");
                return;
            }
            
            double reciprocalResult = 1 / value;
            
            // 保存计算前的表达式
            String functionExpr = "1/(" + formatNumber(value) + ")";
            
            // 更新计算结果
            currentInput = formatNumber(reciprocalResult);
            
            // 更新表达式显示
            if (tokens.isEmpty()) {
                // 如果tokens为空，这是一个全新的计算
                displayExpression = functionExpr;
            } else if (isOperator(tokens.get(tokens.size() - 1))) {
                // 如果最后一个token是操作符，表达式已经包含操作符，添加函数表达式
                displayExpression += functionExpr;
            } else {
                // 其他情况，例如在一个数字后点击了函数按钮，替换最后一个token
                tokens.remove(tokens.size() - 1);
                displayExpression = buildExpressionFromTokens(tokens) + functionExpr;
            }
            
            // 设置为非新输入状态，让操作符处理添加结果到tokens
            isNewInput = false;
            
        } catch (Exception e) {
            setErrorState("计算错误");
        }
    }
    
    /**
     * 计算三角函数
     * @param type 三角函数类型 (sin, cos, tan)
     */
    public void calculateTrigFunction(String type) {
        if (errorState) {
            clear();
        }
        
        // 如果当前在输入函数参数，先完成上一个函数的计算
        if (inFunctionInput) {
            completeFunctionInput();
        }
        
        // 如果当前正在输入一个数字（不是新输入状态），先将其添加到tokens
        if (!isNewInput && !tokens.isEmpty() && !isOperator(tokens.get(tokens.size() - 1))) {
            tokens.add(currentInput);
            isNewInput = true;
        }
        
        // 进入三角函数输入模式
        inFunctionInput = true;
        currentFunction = type;
        functionArgument = "";
        
        // 更新表达式显示
        updateFunctionExpression();
    }
    
    /**
     * 使用圆周率 Pi
     */
    public void usePi() {
        if (errorState) {
            clear();
        }
        
        if (replaceExpressionWithResult) {
            // 如果显示结果状态，清除之前的运算
            clear();
            replaceExpressionWithResult = false;
        }
        
        currentInput = formatNumber(Math.PI);
        updateExpression();
        isNewInput = false;
        
        // 尝试实时计算结果
        calculateInRealTime();
    }
    
    /**
     * 使用自然常数 e
     */
    public void useE() {
        if (errorState) {
            clear();
        }
        
        if (replaceExpressionWithResult) {
            // 如果显示结果状态，清除之前的运算
            clear();
            replaceExpressionWithResult = false;
        }
        
        currentInput = formatNumber(Math.E);
        updateExpression();
        isNewInput = false;
        
        // 尝试实时计算结果
        calculateInRealTime();
    }
    
    /**
     * 计算阶乘
     */
    public void calculateFactorial() {
        if (errorState) {
            clear();
            return;
        }
        
        // 如果当前在输入函数参数，先完成上一个函数的计算
        if (inFunctionInput) {
            completeFunctionInput();
        }
        
        // 阶乘操作不需要输入模式，直接计算当前输入值的阶乘
        try {
            double value = Double.parseDouble(currentInput);
            
            // 检查是否为非负整数
            if (value < 0 || value != Math.floor(value)) {
                setErrorState("阶乘只适用于非负整数");
                return;
            }
            
            // 计算阶乘
            double result = 1;
            for (int i = 2; i <= value; i++) {
                result *= i;
                // 检查溢出
                if (Double.isInfinite(result)) {
                    setErrorState("结果太大");
                    return;
                }
            }
            
            // 保存计算前的表达式
            String functionExpr = formatNumber(value) + "!";
            
            // 更新计算结果
            currentInput = formatNumber(result);
            
            // 更新表达式显示
            if (tokens.isEmpty()) {
                // 如果tokens为空，这是一个全新的计算
                displayExpression = functionExpr;
            } else if (isOperator(tokens.get(tokens.size() - 1))) {
                // 如果最后一个token是操作符，表达式已经包含操作符，添加函数表达式
                displayExpression += functionExpr;
            } else {
                // 其他情况，例如在一个数字后点击了函数按钮，替换最后一个token
                tokens.remove(tokens.size() - 1);
                displayExpression = buildExpressionFromTokens(tokens) + functionExpr;
            }
            
            // 设置为非新输入状态，让操作符处理添加结果到tokens
            isNewInput = false;
            
        } catch (Exception e) {
            setErrorState("计算错误");
        }
    }
    
    /**
     * 计算对数 (lg - 常用对数)
     */
    public void calculateLog10() {
        if (errorState) {
            clear();
        }
        
        // 如果当前在输入函数参数，先完成上一个函数的计算
        if (inFunctionInput) {
            completeFunctionInput();
        }
        
        // 如果当前正在输入一个数字（不是新输入状态），先将其添加到tokens
        if (!isNewInput && !tokens.isEmpty() && !isOperator(tokens.get(tokens.size() - 1))) {
            tokens.add(currentInput);
            isNewInput = true;
        }
        
        // 进入对数函数输入模式
        inFunctionInput = true;
        currentFunction = "lg";
        functionArgument = "";
        
        // 更新表达式显示
        updateFunctionExpression();
    }
    
    /**
     * 计算自然对数 (ln)
     */
    public void calculateLn() {
        if (errorState) {
            clear();
        }
        
        // 如果当前在输入函数参数，先完成上一个函数的计算
        if (inFunctionInput) {
            completeFunctionInput();
        }
        
        // 如果当前正在输入一个数字（不是新输入状态），先将其添加到tokens
        if (!isNewInput && !tokens.isEmpty() && !isOperator(tokens.get(tokens.size() - 1))) {
            tokens.add(currentInput);
            isNewInput = true;
        }
        
        // 进入自然对数函数输入模式
        inFunctionInput = true;
        currentFunction = "ln";
        functionArgument = "";
        
        // 更新表达式显示
        updateFunctionExpression();
    }
    
    /**
     * 计算幂 (x^y)
     */
    public void calculatePower() {
        if (errorState) {
            clear();
        }
        
        // 如果当前在输入函数参数，先完成上一个函数的计算
        if (inFunctionInput) {
            completeFunctionInput();
        }
        
        try {
            double base = Double.parseDouble(currentInput);
            double result = Math.pow(base, 2); // 默认平方
            
            // 保存计算前的表达式
            String functionExpr = formatNumber(base) + "²";
            
            // 更新计算结果
            currentInput = formatNumber(result);
            
            // 更新表达式显示
            if (tokens.isEmpty()) {
                // 如果tokens为空，这是一个全新的计算
                displayExpression = functionExpr;
            } else if (isOperator(tokens.get(tokens.size() - 1))) {
                // 如果最后一个token是操作符，表达式已经包含操作符，添加函数表达式
                displayExpression += functionExpr;
            } else {
                // 其他情况，例如在一个数字后点击了函数按钮，替换最后一个token
                tokens.remove(tokens.size() - 1);
                displayExpression = buildExpressionFromTokens(tokens) + functionExpr;
            }
            
            // 设置为非新输入状态，让操作符处理添加结果到tokens
            isNewInput = false;
            
        } catch (Exception e) {
            setErrorState("计算错误");
        }
    }
    
    /**
     * 清除当前输入 (CE)
     */
    public void clearEntry() {
        // 如果在函数输入模式，清空函数参数
        if (inFunctionInput) {
            functionArgument = "";
            updateFunctionExpression();
            return;
        }
        
        currentInput = "0";
        isNewInput = true;
        errorState = false;
        
        // 更新当前结果
        currentResult = "0";
        
        if (!replaceExpressionWithResult) {
            // 如果不是显示结果状态，只清除当前输入
            updateExpression();
        } else {
            // 如果是显示结果状态，清除全部
            clear();
        }
    }
    
    /**
     * 清除所有 (C)
     */
    public void clear() {
        currentInput = "0";
        displayExpression = "";
        currentResult = "0";
        isNewInput = true;
        errorState = false;
        replaceExpressionWithResult = false;
        tokens.clear();
        
        // 清除函数输入状态
        inFunctionInput = false;
        currentFunction = "";
        functionArgument = "";
    }
    
    /**
     * 退格
     */
    public void backspace() {
        if (errorState) {
            clear();
            return;
        }
        
        // 如果在函数输入模式中，删除函数参数的最后一个字符
        if (inFunctionInput) {
            if (functionArgument.length() > 0) {
                functionArgument = functionArgument.substring(0, functionArgument.length() - 1);
                // 更新表达式显示
                updateFunctionExpression();
            } else {
                // 如果函数参数为空，退出函数输入模式
                inFunctionInput = false;
                currentFunction = "";
                displayExpression = currentInput;
            }
            return;
        }
        
        if (!isNewInput) {
            if (currentInput.length() > 1) {
                currentInput = currentInput.substring(0, currentInput.length() - 1);
            } else {
                currentInput = "0";
                isNewInput = true;
            }
            
            // 更新表达式
            updateExpression();
            
            // 尝试实时计算结果
            calculateInRealTime();
        } else if (replaceExpressionWithResult) {
            // 在显示结果状态下，退格相当于清除所有
            clear();
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
            
            if (replaceExpressionWithResult) {
                clear();
                replaceExpressionWithResult = false;
            }
            
            updateExpression();
            isNewInput = false;
            
            // 尝试实时计算结果
            calculateInRealTime();
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
        displayExpression = "错误";
        currentResult = "";
        tokens.clear();
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

    /**
     * 完成幂运算
     */
    public void completePowerCalculation() {
        if (!errorState) {
            calculatePower(); // 重定向到calculatePower方法
        }
    }

    /**
     * 更新函数表达式显示
     */
    private void updateFunctionExpression() {
        // 构建函数表达式，例如：sin(45)
        String arg = functionArgument.isEmpty() ? "" : functionArgument;
        String functionExpr = currentFunction + "(" + arg + ")";
        
        // 检查当前表达式的状态
        if (tokens.isEmpty()) {
            // 如果tokens为空，这是一个全新的计算
            displayExpression = functionExpr;
        } else if (!tokens.isEmpty() && isOperator(tokens.get(tokens.size() - 1))) {
            // 如果最后一个token是操作符，保留之前的表达式，并添加函数表达式
            displayExpression = buildExpressionFromTokens(tokens) + functionExpr;
        } else {
            // 其他情况，保留之前的表达式，并替换最后一个token
            if (!tokens.isEmpty()) {
                tokens.remove(tokens.size() - 1);
            }
            displayExpression = buildExpressionFromTokens(tokens) + functionExpr;
        }
        
        currentInput = arg.isEmpty() ? "0" : arg;
    }
    
    /**
     * 完成函数输入并计算结果
     */
    private void completeFunctionInput() {
        if (!inFunctionInput) return;
        
        try {
            // 如果没有输入参数，默认使用当前输入值
            if (functionArgument.isEmpty()) {
                functionArgument = currentInput;
            }
            
            double value = Double.parseDouble(functionArgument);
            double result = 0;
            
            // 根据函数类型计算
            switch (currentFunction) {
                case "sin":
                    result = Math.sin(Math.toRadians(value));
                    break;
                case "cos":
                    result = Math.cos(Math.toRadians(value));
                    break;
                case "tan":
                    result = Math.tan(Math.toRadians(value));
                    break;
                case "lg":
                    if (value <= 0) {
                        setErrorState("必须为正数");
                        return;
                    }
                    result = Math.log10(value);
                    break;
                case "ln":
                    if (value <= 0) {
                        setErrorState("必须为正数");
                        return;
                    }
                    result = Math.log(value);
                    break;
                case "√":
                    if (value < 0) {
                        setErrorState("负数不能开平方");
                        return;
                    }
                    result = Math.sqrt(value);
                    break;
                default:
                    setErrorState("未知函数");
                    return;
            }
            
            // 保存计算前的表达式用于显示
            String functionExpr = currentFunction + "(" + functionArgument + ")";
            
            // 退出函数输入模式
            inFunctionInput = false;
            currentFunction = "";
            functionArgument = "";
            
            // 更新计算结果
            currentInput = formatNumber(result);
            
            // 更新tokens和表达式
            if (tokens.isEmpty()) {
                // 如果tokens为空，这是一个全新的计算
                displayExpression = functionExpr;
            } else if (isOperator(tokens.get(tokens.size() - 1))) {
                // 如果最后一个token是操作符，表达式已经包含操作符，添加函数表达式
                displayExpression += functionExpr;
            } else {
                // 其他情况，例如在一个数字后点击了函数按钮，替换最后一个token
                tokens.remove(tokens.size() - 1);
                displayExpression = buildExpressionFromTokens(tokens) + functionExpr;
            }
            
            // 重要修改：不自动将结果添加到tokens中，让操作符处理来处理
            // 设置isNewInput为false，这样在按操作符时会正确添加当前结果
            isNewInput = false;
            
        } catch (Exception e) {
            setErrorState("计算错误");
        }
    }

    /**
     * 从tokens构建表达式字符串，辅助方法
     */
    private String buildExpressionFromTokens(List<String> tokens) {
        StringBuilder sb = new StringBuilder();
        for (String token : tokens) {
            if (isOperator(token)) {
                sb.append(" ").append(token).append(" ");
            } else {
                sb.append(token);
            }
        }
        return sb.toString().trim();
    }
} 