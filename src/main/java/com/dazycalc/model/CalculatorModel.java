package com.win11calc.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * 计算器模型类，负责处理计算逻辑
 * 重构后的设计：
 * 1. 所有按键操作都只是修改表达式文本
 * 2. 计算是对表达式文本的解析
 * 3. 只有按下等号时，才会计算结果并替换表达式
 */
public class CalculatorModel {
    // 显示相关
    private String displayExpression = ""; // 用于显示的表达式文本
    private String currentResult = "0"; // 当前计算结果
    private boolean errorState = false; // 错误状态
    private String errorMessage = ""; // 错误消息

    // 内存相关
    private double memory = 0; // 内存值
    private List<String> history = new ArrayList<>(); // 历史记录

    // 状态标记
    private boolean isInFunctionInput = false; // 是否在输入函数参数
    private int functionParenthesisPos = -1; // 函数左括号位置

    // 添加一个用于记录角度模式的字段，默认使用弧度制
    private boolean isUsingRadianMode = true;

    /**
     * 获取当前表达式文本
     */
    public String getExpression() {
        return displayExpression;
    }

    /**
     * 获取当前计算结果
     */
    public String getCurrentResult() {
        return currentResult;
    }

    /**
     * 检查是否处于错误状态
     */
    public boolean isErrorState() {
        return errorState;
    }

    /**
     * 获取错误消息
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * 获取计算历史记录
     */
    public List<String> getHistory() {
        return new ArrayList<>(history);
    }

    /**
     * 添加数字到表达式
     */
    public void addDigit(String digit) {
        if (errorState) {
            clear();
        }

        // 如果在函数参数输入模式
        if (isInFunctionInput) {
            int closingPos = displayExpression.lastIndexOf(')');
            if (closingPos == displayExpression.length() - 1) {
                // 如果已经有右括号，替换它
                displayExpression = displayExpression.substring(0, displayExpression.length() - 1) + digit + ")";
            } else {
                // 在函数名后面添加数字
                displayExpression += digit;
            }
        } else {
            // 普通数字输入
            if (displayExpression.equals("0")) {
                displayExpression = digit; // 替换前导0
            } else {
                displayExpression += digit;
            }
        }

        // 尝试实时计算结果
        tryCalculateResult();
    }

    /**
     * 添加小数点
     */
    public void addDecimalPoint() {
        if (errorState) {
            clear();
        }

        // 如果在函数参数输入模式
        if (isInFunctionInput) {
            // 解析当前函数参数
            String parameter = extractFunctionParameter();
            
            // 检查参数中是否已有小数点
            if (!parameter.contains(".")) {
                if (parameter.isEmpty()) {
                    // 如果参数为空，添加"0."
                    insertIntoFunction("0.");
                } else {
                    // 否则直接添加小数点
                    insertIntoFunction(".");
                }
            }
        } else {
            // 检查最后一个数字是否已包含小数点
            String lastNumber = extractLastNumber();
            
            if (!lastNumber.contains(".")) {
                if (lastNumber.isEmpty() || isOperator(displayExpression.substring(displayExpression.length() - 1))) {
                    displayExpression += "0.";
                } else {
                    displayExpression += ".";
                }
            }
        }
    }

    /**
     * 改变数值正负号
     */
    public void toggleSign() {
        if (errorState) {
            clear();
            return;
        }

        // 如果在函数参数输入模式
        if (isInFunctionInput) {
            String parameter = extractFunctionParameter();
            
            if (parameter.startsWith("-")) {
                // 移除负号
                String newParam = parameter.substring(1);
                replaceFunctionParameter(newParam);
            } else if (!parameter.isEmpty() && !parameter.equals("0")) {
                // 添加负号
                replaceFunctionParameter("-" + parameter);
            }
        } else {
            // 找到最后一个数字
            String lastNumber = extractLastNumber();
            int lastNumberPos = displayExpression.lastIndexOf(lastNumber);
            
            if (lastNumber.startsWith("-")) {
                // 移除负号
                displayExpression = displayExpression.substring(0, lastNumberPos) + 
                                   lastNumber.substring(1) + 
                                   displayExpression.substring(lastNumberPos + lastNumber.length());
            } else if (!lastNumber.isEmpty() && !lastNumber.equals("0")) {
                // 添加负号
                displayExpression = displayExpression.substring(0, lastNumberPos) + 
                                   "-" + lastNumber + 
                                   displayExpression.substring(lastNumberPos + lastNumber.length());
            }
        }

        // 尝试实时计算结果
        tryCalculateResult();
    }

    /**
     * 设置操作符（+, -, ×, ÷）
     */
    public void setOperation(CalculatorOperation operation) {
        if (errorState) {
            return;
        }

        // 如果当前在函数输入模式，完成函数输入
        if (isInFunctionInput) {
            completeFunctionInput();
        }

        // 检查表达式是否为空
        if (displayExpression.isEmpty()) {
            displayExpression = "0" + operation.getSymbol();
            return;
        }

        // 检查最后一个字符是否是操作符，如果是则替换
        if (displayExpression.length() > 0) {
            char lastChar = displayExpression.charAt(displayExpression.length() - 1);
            if (isOperator(String.valueOf(lastChar))) {
                displayExpression = displayExpression.substring(0, displayExpression.length() - 1) + 
                                   operation.getSymbol();
            } else if (lastChar == '(') {
                // 如果最后是左括号，添加0和操作符
                displayExpression += "0" + operation.getSymbol();
            } else {
                // 否则直接添加操作符
                displayExpression += operation.getSymbol();
            }
        }

        // 尝试实时计算结果
        tryCalculateResult();
    }

    /**
     * 添加左括号
     */
    public void addLeftParenthesis() {
        if (errorState) {
            clear();
        }

        // 检查最后一个字符
        if (!displayExpression.isEmpty()) {
            char lastChar = displayExpression.charAt(displayExpression.length() - 1);
            // 如果最后一个字符是数字或右括号，添加乘号
            if (Character.isDigit(lastChar) || lastChar == ')' || lastChar == '.' || 
                lastChar == 'π' || lastChar == 'e') {
                displayExpression += "×(";
            } else {
                displayExpression += "(";
            }
        } else {
            displayExpression = "(";
        }
    }

    /**
     * 添加右括号
     */
    public void addRightParenthesis() {
        if (errorState) {
            return;
        }

        // 计算左右括号数量
        int leftCount = 0;
        int rightCount = 0;
        for (char c : displayExpression.toCharArray()) {
            if (c == '(') leftCount++;
            if (c == ')') rightCount++;
        }

        // 确保有未匹配的左括号
        if (leftCount > rightCount) {
            // 如果最后一个字符是操作符，先添加0
            if (!displayExpression.isEmpty() && 
                isOperator(String.valueOf(displayExpression.charAt(displayExpression.length() - 1)))) {
                displayExpression += "0";
            }
            displayExpression += ")";

            // 尝试实时计算结果
            tryCalculateResult();
        }
    }

    /**
     * 开始函数输入（如sin, cos, log等）
     */
    public void startFunctionInput(String functionName) {
        if (errorState) {
            clear();
        }

        // 如果已在函数输入模式，先完成当前函数
        if (isInFunctionInput) {
            completeFunctionInput();
        }

        // 检查是否需要添加乘号
        if (!displayExpression.isEmpty()) {
            char lastChar = displayExpression.charAt(displayExpression.length() - 1);
            if (Character.isDigit(lastChar) || lastChar == ')' || lastChar == '.' || 
                lastChar == 'π' || lastChar == 'e') {
                displayExpression += "×";
            }
        }

        // 添加函数名和左括号
        displayExpression += functionName + "(";
        isInFunctionInput = true;
        functionParenthesisPos = displayExpression.length() - 1;
    }

    /**
     * 完成函数输入
     */
    public void completeFunctionInput() {
        if (!isInFunctionInput) {
            return;
        }

        // 如果函数输入不完整，添加右括号
        if (displayExpression.charAt(displayExpression.length() - 1) != ')') {
            // 如果括号内为空，添加0
            if (displayExpression.charAt(displayExpression.length() - 1) == '(') {
                displayExpression += "0";
            }
            displayExpression += ")";
        }

        // 重置函数输入状态
        isInFunctionInput = false;
        functionParenthesisPos = -1;

        // 尝试实时计算结果
        tryCalculateResult();
    }

    /**
     * 添加特殊常数（如π, e）
     */
    public void addConstant(String constant, double value) {
        if (errorState) {
            clear();
        }

        // 检查是否需要添加乘号
        if (!displayExpression.isEmpty()) {
            char lastChar = displayExpression.charAt(displayExpression.length() - 1);
            if (Character.isDigit(lastChar) || lastChar == ')' || lastChar == '.' || 
                lastChar == 'π' || lastChar == 'e') {
                displayExpression += "×";
            }
        }

        // 添加常数符号
        displayExpression += constant;

        // 尝试实时计算结果
        tryCalculateResult();
    }

    /**
     * 计算结果（按下等号）
     */
    public void calculateResult() {
        if (errorState) {
            return;
        }

        // 如果当前在函数输入模式，完成函数输入
        if (isInFunctionInput) {
            completeFunctionInput();
        }

        try {
            // 确保表达式不为空
            if (displayExpression.isEmpty()) {
                displayExpression = "0";
                currentResult = "0";
                return;
            }

            // 解析并计算表达式
            double result = evaluateExpression(displayExpression);
            
            // 检查结果是否有效
            if (Double.isInfinite(result) || Double.isNaN(result)) {
                setErrorState("计算结果无效");
                return;
            }

            // 添加到历史记录
            String historyEntry = displayExpression + " = " + formatNumber(result);
            history.add(historyEntry);

            // 更新当前结果和表达式
            currentResult = formatNumber(result);
            displayExpression = currentResult;

        } catch (Exception e) {
            setErrorState("计算错误: " + e.getMessage());
        }
    }

    /**
     * 尝试实时计算结果（不改变表达式）
     */
    private void tryCalculateResult() {
        // 如果表达式为空，不计算
        if (displayExpression.isEmpty()) {
            currentResult = "0";
            return;
        }

        try {
            // 创建一个临时表达式用于计算
            String tempExpression = displayExpression;
            
            // 如果表达式以操作符结尾，添加0
            if (isOperator(tempExpression.substring(tempExpression.length() - 1))) {
                tempExpression += "0";
            }
            
            // 确保所有括号都闭合
            int leftCount = 0;
            int rightCount = 0;
            for (char c : tempExpression.toCharArray()) {
                if (c == '(') leftCount++;
                if (c == ')') rightCount++;
            }
            
            // 添加缺失的右括号
            for (int i = 0; i < leftCount - rightCount; i++) {
                tempExpression += ")";
            }

            // 解析并计算表达式
            double result = evaluateExpression(tempExpression);
            
            // 更新当前结果
            if (!Double.isInfinite(result) && !Double.isNaN(result)) {
                currentResult = formatNumber(result);
            } else {
                currentResult = "";
            }
        } catch (Exception e) {
            // 计算错误时不显示结果
            currentResult = "";
        }
    }

    /**
     * 清除所有 (C)
     */
    public void clear() {
        displayExpression = "";
        currentResult = "0";
        errorState = false;
        errorMessage = "";
        isInFunctionInput = false;
        functionParenthesisPos = -1;
    }

    /**
     * 清除当前输入 (CE)
     */
    public void clearEntry() {
        if (isInFunctionInput) {
            // 仅清除函数参数
            replaceFunctionParameter("");
        } else {
            // 清除最后输入的数字或操作符
            String lastToken = extractLastToken();
            if (!lastToken.isEmpty()) {
                displayExpression = displayExpression.substring(0, displayExpression.length() - lastToken.length());
                if (displayExpression.isEmpty()) {
                    displayExpression = "0";
                }
            } else {
                // 如果没有可清除的部分，清除全部
                clear();
            }
        }

        // 尝试实时计算结果
        tryCalculateResult();
    }

    /**
     * 退格
     */
    public void backspace() {
        if (errorState) {
            clear();
            return;
        }

        if (!displayExpression.isEmpty()) {
            // 删除最后一个字符
            displayExpression = displayExpression.substring(0, displayExpression.length() - 1);
            
            // 如果删除后为空，设为0
            if (displayExpression.isEmpty()) {
                displayExpression = "0";
            }
            
            // 检查是否还在函数输入模式
            if (isInFunctionInput) {
                int lastOpenParen = displayExpression.lastIndexOf("(");
                if (lastOpenParen == -1 || !isFunctionName(displayExpression.substring(0, lastOpenParen))) {
                    // 如果没有左括号或括号前不是函数名，退出函数输入模式
                    isInFunctionInput = false;
                    functionParenthesisPos = -1;
                }
            }
            
            // 尝试实时计算结果
            tryCalculateResult();
        }
    }

    /**
     * 内存存储 (MS)
     */
    public void memoryStore() {
        try {
            if (!currentResult.isEmpty()) {
                memory = Double.parseDouble(currentResult);
            }
        } catch (Exception e) {
            // 忽略错误
        }
    }

    /**
     * 内存加 (M+)
     */
    public void memoryAdd() {
        try {
            if (!currentResult.isEmpty()) {
                memory += Double.parseDouble(currentResult);
            }
        } catch (Exception e) {
            // 忽略错误
        }
    }

    /**
     * 内存减 (M-)
     */
    public void memorySubtract() {
        try {
            if (!currentResult.isEmpty()) {
                memory -= Double.parseDouble(currentResult);
            }
        } catch (Exception e) {
            // 忽略错误
        }
    }

    /**
     * 内存调用 (MR)
     */
    public void memoryRecall() {
        // 根据上下文决定如何插入内存值
        String memValue = formatNumber(memory);
        
        if (isInFunctionInput) {
            // 在函数参数中插入内存值
            replaceFunctionParameter(memValue);
        } else {
            // 检查是否需要插入乘号
            if (!displayExpression.isEmpty()) {
                char lastChar = displayExpression.charAt(displayExpression.length() - 1);
                if (Character.isDigit(lastChar) || lastChar == ')' || lastChar == '.' || 
                    lastChar == 'π' || lastChar == 'e') {
                    displayExpression += "×";
                }
            }
            displayExpression += memValue;
        }
        
        // 尝试实时计算结果
        tryCalculateResult();
    }

    /**
     * 内存清除 (MC)
     */
    public void memoryClear() {
        memory = 0;
    }

    /**
     * 设置错误状态
     */
    private void setErrorState(String message) {
        errorState = true;
        errorMessage = message;
        currentResult = "";
    }

    // 辅助方法

    /**
     * 解析并计算表达式
     */
    private double evaluateExpression(String expression) {
        // 替换特殊常量
        expression = expression.replace("π", String.valueOf(Math.PI));
        expression = expression.replace("e", String.valueOf(Math.E));
        
        // 处理特殊表达式，如平方(x²)、阶乘(x!)、绝对值(|x|)等
        expression = processSpecialExpressions(expression);
        
        // 处理函数调用
        expression = processFunctions(expression);
        
        // 转换为后缀表达式并计算
        List<String> tokens = tokenizeExpression(expression);
        List<String> rpnTokens = convertToRPN(tokens);
        return evaluateRPN(rpnTokens);
    }

    /**
     * 处理特殊表达式，如平方(x²)、阶乘(x!)、绝对值(|x|)等
     */
    private String processSpecialExpressions(String expression) {
        StringBuilder processedExpr = new StringBuilder();
        
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            
            if (c == '²') {
                // 处理平方表达式
                // 寻找前面的数字
                int j = i - 1;
                while (j >= 0 && (Character.isDigit(expression.charAt(j)) || 
                                  expression.charAt(j) == '.' ||
                                  expression.charAt(j) == 'e' ||
                                  expression.charAt(j) == 'π' ||
                                  (j > 0 && expression.charAt(j) == '-' && !Character.isDigit(expression.charAt(j-1))))) {
                    j--;
                }
                
                // 提取数值
                String number = expression.substring(j + 1, i);
                
                try {
                    // 替换为平方值
                    double value = Double.parseDouble(number);
                    double squared = value * value;
                    
                    // 删除之前已追加的数字，并添加结果
                    processedExpr.delete(processedExpr.length() - number.length(), processedExpr.length());
                    processedExpr.append(formatNumber(squared));
                } catch (NumberFormatException e) {
                    // 如果不是有效数字，保留原始表达式
                    processedExpr.append(c);
                }
            } else if (c == '!') {
                // 处理阶乘表达式
                // 寻找前面的数字
                int j = i - 1;
                while (j >= 0 && (Character.isDigit(expression.charAt(j)) || 
                                 (j > 0 && expression.charAt(j) == '-' && !Character.isDigit(expression.charAt(j-1))))) {
                    j--;
                }
                
                // 提取数值
                String number = expression.substring(j + 1, i);
                
                try {
                    // 计算阶乘
                    double value = Double.parseDouble(number);
                    
                    // 检查是否为非负整数
                    if (value < 0 || value != Math.floor(value)) {
                        throw new RuntimeException("阶乘只适用于非负整数");
                    }
                    
                    // 计算阶乘
                    double result = 1;
                    for (int k = 2; k <= value; k++) {
                        result *= k;
                    }
                    
                    // 删除之前已追加的数字，并添加结果
                    processedExpr.delete(processedExpr.length() - number.length(), processedExpr.length());
                    processedExpr.append(formatNumber(result));
                } catch (NumberFormatException e) {
                    // 如果不是有效数字，保留原始表达式
                    processedExpr.append(c);
                }
            } else if (c == '|') {
                // 处理绝对值表达式
                // 找到匹配的闭合竖线
                int closePos = expression.indexOf('|', i + 1);
                if (closePos > i) {
                    // 提取绝对值内的内容
                    String absContent = expression.substring(i + 1, closePos);
                    
                    try {
                        // 计算内容的值
                        double value = evaluateExpression(absContent);
                        double absValue = Math.abs(value);
                        
                        // 添加绝对值计算结果
                        processedExpr.append(formatNumber(absValue));
                        
                        // 跳过已处理的内容
                        i = closePos;
                    } catch (Exception e) {
                        // 如果计算错误，保留原始表达式
                        processedExpr.append(c);
                    }
                } else {
                    // 如果没有找到匹配的闭合竖线，保留原始字符
                    processedExpr.append(c);
                }
            } else {
                // 其他字符直接添加
                processedExpr.append(c);
            }
        }
        
        return processedExpr.toString();
    }

    /**
     * 处理函数调用
     */
    private String processFunctions(String expression) {
        // 处理所有的函数调用，从内到外
        boolean containsFunction = true;
        while (containsFunction) {
            containsFunction = false;
            
            // 查找函数调用模式: 函数名(参数)
            int pos = 0;
            while (pos < expression.length()) {
                // 查找函数名
                int funcStart = -1;
                String funcName = "";
                
                // 检查常见函数名
                String[] functionNames = {"arcsin", "arccos", "arctan", "sin", "cos", "tan", "log10", "ln", "sqrt", "abs"};
                for (String name : functionNames) {
                    if (pos + name.length() <= expression.length() && 
                        expression.substring(pos, pos + name.length()).equals(name) && 
                        pos + name.length() < expression.length() && 
                        expression.charAt(pos + name.length()) == '(') {
                        funcStart = pos;
                        funcName = name;
                        pos += name.length();
                        break;
                    }
                }
                
                // 如果找到函数名
                if (funcStart >= 0) {
                    // 查找匹配的右括号
                    int parenCount = 1; // 已找到一个左括号
                    int parenEnd = -1;
                    for (int i = pos + 1; i < expression.length(); i++) {
                        if (expression.charAt(i) == '(') {
                            parenCount++;
                        } else if (expression.charAt(i) == ')') {
                            parenCount--;
                            if (parenCount == 0) {
                                parenEnd = i;
                                break;
                            }
                        }
                    }
                    
                    // 如果找到匹配的右括号
                    if (parenEnd > pos) {
                        // 提取参数表达式并递归求值
                        String paramExpr = expression.substring(pos + 1, parenEnd);
                        double paramValue = evaluateExpression(paramExpr);
                        
                        // 应用函数并获取结果
                        double result = 0;
                        try {
                            switch (funcName) {
                                case "sin":
                                    if (isUsingRadianMode) {
                                        result = Math.sin(paramValue);
                                    } else {
                                        // 角度转弧度后计算
                                        result = Math.sin(Math.toRadians(paramValue));
                                    }
                                    break;
                                case "cos":
                                    if (isUsingRadianMode) {
                                        result = Math.cos(paramValue);
                                    } else {
                                        // 角度转弧度后计算
                                        result = Math.cos(Math.toRadians(paramValue));
                                    }
                                    break;
                                case "tan":
                                    if (isUsingRadianMode) {
                                        result = Math.tan(paramValue);
                                    } else {
                                        // 角度转弧度后计算
                                        result = Math.tan(Math.toRadians(paramValue));
                                    }
                                    break;
                                case "arcsin":
                                    if (paramValue < -1 || paramValue > 1) {
                                        throw new RuntimeException("反正弦函数参数必须在[-1,1]之间");
                                    }
                                    if (isUsingRadianMode) {
                                        result = Math.asin(paramValue);
                                    } else {
                                        // 计算结果弧度转角度
                                        result = Math.toDegrees(Math.asin(paramValue));
                                    }
                                    break;
                                case "arccos":
                                    if (paramValue < -1 || paramValue > 1) {
                                        throw new RuntimeException("反余弦函数参数必须在[-1,1]之间");
                                    }
                                    if (isUsingRadianMode) {
                                        result = Math.acos(paramValue);
                                    } else {
                                        // 计算结果弧度转角度
                                        result = Math.toDegrees(Math.acos(paramValue));
                                    }
                                    break;
                                case "arctan":
                                    if (isUsingRadianMode) {
                                        result = Math.atan(paramValue);
                                    } else {
                                        // 计算结果弧度转角度
                                        result = Math.toDegrees(Math.atan(paramValue));
                                    }
                                    break;
                                case "log10":
                                    if (paramValue <= 0) {
                                        throw new RuntimeException("对数函数参数必须为正数");
                                    }
                                    result = Math.log10(paramValue);
                                    break;
                                case "ln":
                                    if (paramValue <= 0) {
                                        throw new RuntimeException("对数函数参数必须为正数");
                                    }
                                    result = Math.log(paramValue);
                                    break;
                                case "sqrt":
                                    if (paramValue < 0) {
                                        throw new RuntimeException("平方根函数参数必须为非负数");
                                    }
                                    result = Math.sqrt(paramValue);
                                    break;
                                case "abs":
                                    result = Math.abs(paramValue);
                                    break;
                                default:
                                    throw new RuntimeException("未知函数: " + funcName);
                            }
                            
                            // 替换函数调用为结果
                            expression = expression.substring(0, funcStart) + 
                                       formatNumber(result) + 
                                       expression.substring(parenEnd + 1);
                            
                            // 重置位置，继续查找下一个函数
                            pos = funcStart;
                            containsFunction = true;
                        } catch (Exception e) {
                            throw new RuntimeException("函数计算错误: " + e.getMessage());
                        }
                    } else {
                        pos = funcStart + funcName.length() + 1;
                    }
                } else {
                    pos++;
                }
            }
        }
        
        return expression;
    }

    /**
     * 将表达式分割成token
     */
    private List<String> tokenizeExpression(String expression) {
        List<String> tokens = new ArrayList<>();
        StringBuilder numBuilder = new StringBuilder();
        boolean numberStarted = false;
        
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            
            // 处理数字、小数点和科学计数法
            if (Character.isDigit(c) || c == '.') {
                numBuilder.append(c);
                numberStarted = true;
                continue;
            }
            
            // 处理科学计数法表示
            if ((c == 'e' || c == 'E') && numberStarted) {
                // 只有在数字已经开始且下一个字符是数字或正负号时，才视为科学计数法
                if (i + 1 < expression.length() && 
                    (Character.isDigit(expression.charAt(i + 1)) || 
                     expression.charAt(i + 1) == '+' || 
                     expression.charAt(i + 1) == '-')) {
                    
                    numBuilder.append(c); // 添加e或E字符
                    
                    // 添加可能的正负号
                    if (i + 1 < expression.length() && 
                        (expression.charAt(i + 1) == '+' || expression.charAt(i + 1) == '-')) {
                        numBuilder.append(expression.charAt(i + 1));
                        i++; // 跳过符号
                    }
                    
                    // 添加指数部分的数字
                    while (i + 1 < expression.length() && Character.isDigit(expression.charAt(i + 1))) {
                        numBuilder.append(expression.charAt(i + 1));
                        i++;
                    }
                    
                    continue;
                }
            }
            
            // 处理负号
            if (c == '-') {
                // 如果负号前面是操作符或左括号或是表达式的开始，视为负号
                if (i == 0 || expression.charAt(i - 1) == '(' || 
                    "+-×÷%^".indexOf(expression.charAt(i - 1)) >= 0) {
                    numBuilder.append(c);
                    numberStarted = true;
                    continue;
                }
            }
            
            // 如果之前有数字在构建中，添加到tokens
            if (numberStarted) {
                tokens.add(numBuilder.toString());
                numBuilder.setLength(0);
                numberStarted = false;
            }
            
            // 处理操作符和括号
            if (c == '+' || c == '-' || c == '×' || c == '÷' || c == '(' || c == ')' || c == '%' || c == '^') {
                tokens.add(String.valueOf(c));
            }
        }
        
        // 添加最后一个数字（如果有）
        if (numberStarted) {
            tokens.add(numBuilder.toString());
        }
        
        return tokens;
    }

    /**
     * 将中缀表达式转换为后缀表达式(RPN)
     */
    private List<String> convertToRPN(List<String> tokens) {
        List<String> output = new ArrayList<>();
        Stack<String> operatorStack = new Stack<>();
        
        for (String token : tokens) {
            // 数字直接加入输出
            if (!isOperator(token) && !token.equals("(") && !token.equals(")")) {
                output.add(token);
            }
            // 左括号压入栈
            else if (token.equals("(")) {
                operatorStack.push(token);
            }
            // 右括号处理
            else if (token.equals(")")) {
                while (!operatorStack.isEmpty() && !operatorStack.peek().equals("(")) {
                    output.add(operatorStack.pop());
                }
                if (!operatorStack.isEmpty() && operatorStack.peek().equals("(")) {
                    operatorStack.pop(); // 弹出左括号
                } else {
                    throw new RuntimeException("括号不匹配");
                }
            }
            // 操作符处理
            else if (isOperator(token)) {
                while (!operatorStack.isEmpty() && 
                       isOperator(operatorStack.peek()) && 
                       getPrecedence(operatorStack.peek()) >= getPrecedence(token)) {
                    output.add(operatorStack.pop());
                }
                operatorStack.push(token);
            }
        }
        
        // 处理栈中剩余的操作符
        while (!operatorStack.isEmpty()) {
            if (operatorStack.peek().equals("(")) {
                throw new RuntimeException("括号不匹配");
            }
            output.add(operatorStack.pop());
        }
        
        return output;
    }

    /**
     * 计算后缀表达式(RPN)
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
                            throw new RuntimeException("除数不能为零");
                        }
                        stack.push(a / b);
                        break;
                    case "%":
                        if (b == 0) {
                            throw new RuntimeException("除数不能为零");
                        }
                        stack.push(a % b); // 取余运算
                        break;
                    case "^":
                        stack.push(Math.pow(a, b)); // 幂运算
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
     * 获取操作符优先级
     */
    private int getPrecedence(String operator) {
        switch (operator) {
            case "+":
            case "-":
                return 1;
            case "×":
            case "÷":
            case "%":
                return 2;
            case "^":
                return 3;
            default:
                return 0;
        }
    }

    /**
     * 判断字符串是否为操作符
     */
    private boolean isOperator(String token) {
        return token.equals("+") || token.equals("-") || 
               token.equals("×") || token.equals("÷") ||
               token.equals("%") || token.equals("^");
    }

    /**
     * 提取函数参数
     */
    private String extractFunctionParameter() {
        if (!isInFunctionInput || functionParenthesisPos == -1) {
            return "";
        }
        
        int closingPos = displayExpression.lastIndexOf(')');
        if (closingPos != -1 && closingPos > functionParenthesisPos) {
            // 如果有右括号，返回括号内的内容
            return displayExpression.substring(functionParenthesisPos + 1, closingPos);
        } else {
            // 否则返回左括号后的所有内容
            return displayExpression.substring(functionParenthesisPos + 1);
        }
    }

    /**
     * 替换函数参数
     */
    private void replaceFunctionParameter(String newParameter) {
        if (!isInFunctionInput || functionParenthesisPos == -1) {
            return;
        }
        
        int closingPos = displayExpression.lastIndexOf(')');
        if (closingPos != -1 && closingPos > functionParenthesisPos) {
            // 如果有右括号，替换括号内的内容
            displayExpression = displayExpression.substring(0, functionParenthesisPos + 1) + 
                              newParameter + 
                              displayExpression.substring(closingPos);
        } else {
            // 否则替换左括号后的所有内容，并添加右括号
            displayExpression = displayExpression.substring(0, functionParenthesisPos + 1) + 
                              newParameter + ")";
        }
    }

    /**
     * 在函数内插入内容
     */
    private void insertIntoFunction(String content) {
        if (!isInFunctionInput || functionParenthesisPos == -1) {
            return;
        }
        
        int closingPos = displayExpression.lastIndexOf(')');
        if (closingPos != -1 && closingPos > functionParenthesisPos) {
            // 如果有右括号，在右括号前插入
            displayExpression = displayExpression.substring(0, closingPos) + 
                              content + 
                              displayExpression.substring(closingPos);
        } else {
            // 否则直接在表达式末尾添加内容
            displayExpression += content;
        }
    }

    /**
     * 提取表达式中的最后一个数字
     */
    private String extractLastNumber() {
        if (displayExpression.isEmpty()) {
            return "";
        }
        
        int i = displayExpression.length() - 1;
        // 跳过最后的右括号
        while (i >= 0 && displayExpression.charAt(i) == ')') {
            i--;
        }
        
        int end = i + 1;
        // 向前寻找非数字、非小数点、非负号、非科学计数法的字符
        while (i >= 0) {
            char c = displayExpression.charAt(i);
            // 处理普通数字部分（数字和小数点）
            if (Character.isDigit(c) || c == '.') {
                i--;
                continue;
            }
            
            // 处理科学计数法中的e和E
            if ((c == 'e' || c == 'E') && i > 0) {
                // 确保e或E之前有数字（即确实属于科学计数法）
                boolean hasDigitBefore = false;
                for (int j = i - 1; j >= 0; j--) {
                    if (Character.isDigit(displayExpression.charAt(j))) {
                        hasDigitBefore = true;
                        break;
                    } else if (displayExpression.charAt(j) != '.' && displayExpression.charAt(j) != '-') {
                        break;
                    }
                }
                
                if (hasDigitBefore) {
                    i--;
                    continue;
                }
            }
            
            // 处理科学计数法中e或E后面的正负号
            if ((c == '+' || c == '-') && i > 0) {
                char prev = displayExpression.charAt(i - 1);
                if (prev == 'e' || prev == 'E') {
                    i--;
                    continue;
                }
            }
            
            // 处理负数的负号（只有在开头或者之前是操作符时才视为负号的一部分）
            if (c == '-') {
                if (i == 0 || !Character.isDigit(displayExpression.charAt(i - 1))) {
                    i--;
                    continue;
                }
            }
            
            // 不是数字的一部分，停止搜索
            break;
        }
        
        return displayExpression.substring(i + 1, end);
    }

    /**
     * 提取表达式中的最后一个token（数字或操作符）
     */
    private String extractLastToken() {
        if (displayExpression.isEmpty()) {
            return "";
        }
        
        // 检查最后一个字符是否为操作符
        if (isOperator(displayExpression.substring(displayExpression.length() - 1))) {
            return displayExpression.substring(displayExpression.length() - 1);
        }
        
        // 否则尝试提取最后一个数字
        return extractLastNumber();
    }

    /**
     * 判断字符串是否为函数名
     */
    private boolean isFunctionName(String str) {
        String[] functionNames = {"sin", "cos", "tan", "arcsin", "arccos", "arctan", "log", "ln", "sqrt"};
        for (String name : functionNames) {
            if (str.endsWith(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 格式化数字
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
     * 处理百分比操作
     */
    public void calculatePercent() {
        if (errorState) {
            clear();
            return;
        }
        
        // 如果在函数参数输入模式
        if (isInFunctionInput) {
            String parameter = extractFunctionParameter();
            if (!parameter.isEmpty()) {
                try {
                    double value = Double.parseDouble(parameter);
                    value = value / 100;
                    replaceFunctionParameter(formatNumber(value));
                } catch (Exception e) {
                    setErrorState("百分比计算错误");
                }
            }
        } else {
            // 提取最后一个数字
            String lastNumber = extractLastNumber();
            if (!lastNumber.isEmpty()) {
                try {
                    double value = Double.parseDouble(lastNumber);
                    value = value / 100;
                    
                    // 替换最后一个数字
                    int lastPos = displayExpression.lastIndexOf(lastNumber);
                    displayExpression = displayExpression.substring(0, lastPos) + 
                                      formatNumber(value) + 
                                      displayExpression.substring(lastPos + lastNumber.length());
                    
                    // 尝试实时计算结果
                    tryCalculateResult();
                } catch (Exception e) {
                    setErrorState("百分比计算错误");
                }
            }
        }
    }
    
    /**
     * 计算平方
     */
    public void calculateSquare() {
        if (errorState) {
            clear();
            return;
        }
        
        // 如果在函数参数输入模式
        if (isInFunctionInput) {
            completeFunctionInput();
        }
        
        // 提取最后一个数字
        String lastNumber = extractLastNumber();
        if (!lastNumber.isEmpty()) {
            try {
                double value = Double.parseDouble(lastNumber);
                
                // 构建平方表达式
                String squareExpr = formatNumber(value) + "²";
                
                // 替换最后一个数字为平方表达式
                int lastPos = displayExpression.lastIndexOf(lastNumber);
                displayExpression = displayExpression.substring(0, lastPos) + 
                                  squareExpr + 
                                  displayExpression.substring(lastPos + lastNumber.length());
                
                // 尝试实时计算结果
                tryCalculateResult();
            } catch (Exception e) {
                setErrorState("平方计算错误");
            }
        }
    }
    
    /**
     * 计算倒数
     */
    public void calculateReciprocal() {
        if (errorState) {
            clear();
            return;
        }
        
        // 如果在函数参数输入模式
        if (isInFunctionInput) {
            completeFunctionInput();
        }
        
        // 提取最后一个数字
        String lastNumber = extractLastNumber();
        if (!lastNumber.isEmpty()) {
            try {
                double value = Double.parseDouble(lastNumber);
                
                if (value == 0) {
                    setErrorState("除数不能为零");
                    return;
                }
                
                // 构建倒数表达式
                String reciprocalExpr = "1/(" + formatNumber(value) + ")";
                
                // 替换最后一个数字为倒数表达式
                int lastPos = displayExpression.lastIndexOf(lastNumber);
                displayExpression = displayExpression.substring(0, lastPos) + 
                                  reciprocalExpr + 
                                  displayExpression.substring(lastPos + lastNumber.length());
                
                // 尝试实时计算结果
                tryCalculateResult();
            } catch (Exception e) {
                setErrorState("倒数计算错误");
            }
        }
    }
    
    /**
     * 计算阶乘
     */
    public void calculateFactorial() {
        if (errorState) {
            clear();
            return;
        }
        
        // 如果在函数参数输入模式
        if (isInFunctionInput) {
            completeFunctionInput();
        }
        
        // 提取最后一个数字
        String lastNumber = extractLastNumber();
        if (!lastNumber.isEmpty()) {
            try {
                double value = Double.parseDouble(lastNumber);
                
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
                
                // 构建阶乘表达式
                String factorialExpr = formatNumber(value) + "!";
                
                // 替换最后一个数字为阶乘表达式
                int lastPos = displayExpression.lastIndexOf(lastNumber);
                displayExpression = displayExpression.substring(0, lastPos) + 
                                  factorialExpr + 
                                  displayExpression.substring(lastPos + lastNumber.length());
                
                // 尝试实时计算结果
                tryCalculateResult();
            } catch (Exception e) {
                setErrorState("阶乘计算错误");
            }
        }
    }
    
    /**
     * 开始平方根函数输入
     */
    public void calculateSquareRoot() {
        startFunctionInput("sqrt");
    }
    
    /**
     * 开始对数函数输入
     */
    public void calculateLog10() {
        startFunctionInput("log10");
    }
    
    /**
     * 开始自然对数函数输入
     */
    public void calculateLn() {
        startFunctionInput("ln");
    }
    
    /**
     * 开始三角函数输入
     * @param type 三角函数类型 (sin, cos, tan)
     * @param isRadianMode 是否使用弧度制，true为弧度制，false为角度制
     */
    public void calculateTrigFunction(String type, boolean isRadianMode) {
        this.isUsingRadianMode = isRadianMode;
        startFunctionInput(type);
    }
    
    /**
     * 开始反三角函数输入
     * @param type 三角函数类型 (sin, cos, tan)
     * @param isRadianMode 是否使用弧度制，true为弧度制，false为角度制
     */
    public void calculateInverseTrigFunction(String type, boolean isRadianMode) {
        this.isUsingRadianMode = isRadianMode;
        startFunctionInput("arc" + type);
    }
    
    /**
     * 开始三角函数输入（默认使用弧度制）
     * @param type 三角函数类型 (sin, cos, tan)
     * @deprecated 使用 {@link #calculateTrigFunction(String, boolean)} 代替
     */
    public void calculateTrigFunction(String type) {
        calculateTrigFunction(type, true);
    }
    
    /**
     * 开始反三角函数输入（默认使用弧度制）
     * @param type 三角函数类型 (sin, cos, tan)
     * @deprecated 使用 {@link #calculateInverseTrigFunction(String, boolean)} 代替
     */
    public void calculateInverseTrigFunction(String type) {
        calculateInverseTrigFunction(type, true);
    }
    
    /**
     * 使用圆周率π
     */
    public void usePi() {
        addConstant("π", Math.PI);
    }
    
    /**
     * 使用自然常数e
     */
    public void useE() {
        addConstant("e", Math.E);
    }
    
    /**
     * 计算幂
     */
    public void calculatePower() {
        if (errorState) {
            clear();
            return;
        }
        
        // 如果在函数参数输入模式
        if (isInFunctionInput) {
            completeFunctionInput();
        }
        
        // 提取最后一个数字
        String lastNumber = extractLastNumber();
        if (!lastNumber.isEmpty()) {
            try {
                double value = Double.parseDouble(lastNumber);
                
                // 添加幂运算符，让用户输入指数
                // 替换最后一个数字为底数加幂运算符
                int lastPos = displayExpression.lastIndexOf(lastNumber);
                displayExpression = displayExpression.substring(0, lastPos) + 
                                   formatNumber(value) + "^" + 
                                   displayExpression.substring(lastPos + lastNumber.length());
                
                // 不需要立即计算结果，等待用户输入指数
            } catch (Exception e) {
                setErrorState("幂计算错误");
            }
        }
    }

    /**
     * 计算平方（使用 x² 按钮）
     */
    public void calculateSquarePower() {
        if (errorState) {
            clear();
            return;
        }
        
        // 如果在函数参数输入模式
        if (isInFunctionInput) {
            completeFunctionInput();
        }
        
        // 提取最后一个数字
        String lastNumber = extractLastNumber();
        if (!lastNumber.isEmpty()) {
            try {
                double value = Double.parseDouble(lastNumber);
                
                // 构建平方表达式
                String squareExpr = formatNumber(value) + "²";
                
                // 替换最后一个数字为平方表达式
                int lastPos = displayExpression.lastIndexOf(lastNumber);
                displayExpression = displayExpression.substring(0, lastPos) + 
                                  squareExpr + 
                                  displayExpression.substring(lastPos + lastNumber.length());
                
                // 尝试实时计算结果
                tryCalculateResult();
            } catch (Exception e) {
                setErrorState("平方计算错误");
            }
        }
    }

    /**
     * 执行取余操作
     */
    public void calculateModulo() {
        if (errorState) {
            clear();
            return;
        }
        
        // 如果在函数参数输入模式
        if (isInFunctionInput) {
            completeFunctionInput();
        }
        
        // 提取最后一个数字
        String lastNumber = extractLastNumber();
        if (!lastNumber.isEmpty()) {
            try {
                double value = Double.parseDouble(lastNumber);
                
                // 添加取余运算符
                int lastPos = displayExpression.lastIndexOf(lastNumber);
                displayExpression = displayExpression.substring(0, lastPos) + 
                                   formatNumber(value) + "%" + 
                                   displayExpression.substring(lastPos + lastNumber.length());
                
                // 不需要立即计算结果，等待用户输入除数
            } catch (Exception e) {
                setErrorState("取余计算错误");
            }
        }
    }

    /**
     * 计算绝对值
     */
    public void calculateAbsoluteValue() {
        if (errorState) {
            clear();
            return;
        }
        
        // 如果在函数参数输入模式
        if (isInFunctionInput) {
            completeFunctionInput();
        }
        
        // 提取最后一个数字
        String lastNumber = extractLastNumber();
        if (!lastNumber.isEmpty()) {
            try {
                double value = Double.parseDouble(lastNumber);
                
                // 构建绝对值表达式
                String absExpr = "|" + formatNumber(value) + "|";
                
                // 替换最后一个数字为绝对值表达式
                int lastPos = displayExpression.lastIndexOf(lastNumber);
                displayExpression = displayExpression.substring(0, lastPos) + 
                                  absExpr + 
                                  displayExpression.substring(lastPos + lastNumber.length());
                
                // 尝试实时计算结果
                tryCalculateResult();
            } catch (Exception e) {
                setErrorState("绝对值计算错误");
            }
        }
    }
    
    /**
     * 添加科学计数法表示
     */
    public void addScientificNotation() {
        if (errorState) {
            clear();
            return;
        }
        
        // 如果在函数参数输入模式
        if (isInFunctionInput) {
            completeFunctionInput();
        }
        
        // 提取最后一个数字
        String lastNumber = extractLastNumber();
        if (!lastNumber.isEmpty()) {
            try {
                double value = Double.parseDouble(lastNumber);
                
                // 将数值转换为科学计数法格式 - 使用自定义格式确保清晰度
                // 使用小括号包围整个科学计数法表示，避免与加法混淆
                // 格式：(1.23e+4) 而不是 1.23e+4
                String sciNotation = String.format("(%.2e)", value);
                
                // 替换最后一个数字为科学计数法表示
                int lastPos = displayExpression.lastIndexOf(lastNumber);
                displayExpression = displayExpression.substring(0, lastPos) + 
                                  sciNotation + 
                                  displayExpression.substring(lastPos + lastNumber.length());
                
                // 尝试实时计算结果
                tryCalculateResult();
            } catch (Exception e) {
                setErrorState("科学计数法转换错误");
            }
        }
    }
} 