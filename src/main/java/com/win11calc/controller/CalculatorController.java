package com.win11calc.controller;

import com.win11calc.model.CalculatorModel;
import com.win11calc.model.CalculatorOperation;
import com.win11calc.view.CalculatorFrame;

/**
 * 计算器控制器，连接模型和视图
 */
public class CalculatorController {
    private final CalculatorModel model;
    private final CalculatorFrame view;
    
    /**
     * 构造函数
     * @param model 计算器模型
     * @param view 计算器视图
     */
    public CalculatorController(CalculatorModel model, CalculatorFrame view) {
        this.model = model;
        this.view = view;
    }
    
    /**
     * 更新视图
     */
    private void updateView() {
        // 在错误状态下显示错误消息
        if (model.isErrorState()) {
            view.updateDisplay(model.getErrorMessage(), "错误", "");
        } else {
            // 正常状态下显示表达式和结果
            view.updateDisplay(
                model.getCurrentResult(),  // 当前结果作为主显示
                model.getExpression(),     // 表达式
                model.getCurrentResult()   // 同时也将结果传递给结果区域
            );
        }
    }
    
    /**
     * 添加数字
     * @param digit 数字字符
     */
    public void addDigit(String digit) {
        model.addDigit(digit);
        updateView();
    }
    
    /**
     * 添加小数点
     */
    public void addDecimalPoint() {
        model.addDecimalPoint();
        updateView();
    }
    
    /**
     * 切换正负号
     */
    public void toggleSign() {
        model.toggleSign();
        updateView();
    }
    
    /**
     * 设置操作
     * @param operation 计算操作
     */
    public void setOperation(CalculatorOperation operation) {
        model.setOperation(operation);
        updateView();
    }
    
    /**
     * 添加左括号
     */
    public void addLeftParenthesis() {
        model.addLeftParenthesis();
        updateView();
    }
    
    /**
     * 添加右括号
     */
    public void addRightParenthesis() {
        model.addRightParenthesis();
        updateView();
    }
    
    /**
     * 计算结果
     */
    public void calculateResult() {
        model.calculateResult();
        updateView();
    }
    
    /**
     * 计算百分比
     */
    public void calculatePercent() {
        model.calculatePercent();
        updateView();
    }
    
    /**
     * 计算取余
     */
    public void calculateModulo() {
        model.calculateModulo();
        updateView();
    }
    
    /**
     * 计算平方根
     */
    public void calculateSquareRoot() {
        model.calculateSquareRoot();
        updateView();
    }
    
    /**
     * 计算平方
     */
    public void calculateSquare() {
        model.calculateSquarePower();
        updateView();
    }
    
    /**
     * 计算幂运算（指定指数）
     */
    public void calculatePower() {
        model.calculatePower();
        updateView();
    }
    
    /**
     * 计算倒数
     */
    public void calculateReciprocal() {
        model.calculateReciprocal();
        updateView();
    }
    
    /**
     * 计算绝对值
     */
    public void calculateAbsoluteValue() {
        model.calculateAbsoluteValue();
        updateView();
    }
    
    /**
     * 转换为科学计数法
     */
    public void addScientificNotation() {
        model.addScientificNotation();
        updateView();
    }
    
    /**
     * 计算三角函数
     * @param type 三角函数类型
     * @param isRadianMode 是否为弧度模式，true为弧度制，false为角度制
     */
    public void calculateTrigFunction(String type, boolean isRadianMode) {
        model.calculateTrigFunction(type, isRadianMode);
        updateView();
    }
    
    /**
     * 计算反三角函数
     * @param type 反三角函数类型
     * @param isRadianMode 是否为弧度模式，true为弧度制，false为角度制
     */
    public void calculateInverseTrigFunction(String type, boolean isRadianMode) {
        model.calculateInverseTrigFunction(type, isRadianMode);
        updateView();
    }
    
    /**
     * 计算三角函数（默认使用弧度制）
     * @param type 三角函数类型
     * @deprecated 使用 {@link #calculateTrigFunction(String, boolean)} 代替
     */
    public void calculateTrigFunction(String type) {
        calculateTrigFunction(type, true);
    }
    
    /**
     * 计算反三角函数（默认使用弧度制）
     * @param type 反三角函数类型
     * @deprecated 使用 {@link #calculateInverseTrigFunction(String, boolean)} 代替
     */
    public void calculateInverseTrigFunction(String type) {
        calculateInverseTrigFunction(type, true);
    }
    
    /**
     * 使用圆周率
     */
    public void usePi() {
        model.usePi();
        updateView();
    }
    
    /**
     * 使用自然常数 e
     */
    public void useE() {
        model.useE();
        updateView();
    }
    
    /**
     * 计算阶乘
     */
    public void calculateFactorial() {
        model.calculateFactorial();
        updateView();
    }
    
    /**
     * 计算常用对数
     */
    public void calculateLog10() {
        model.calculateLog10();
        updateView();
    }
    
    /**
     * 计算自然对数
     */
    public void calculateLn() {
        model.calculateLn();
        updateView();
    }
    
    /**
     * 清除当前输入
     */
    public void clearEntry() {
        model.clearEntry();
        updateView();
    }
    
    /**
     * 清除所有
     */
    public void clear() {
        model.clear();
        updateView();
    }
    
    /**
     * 退格
     */
    public void backspace() {
        model.backspace();
        updateView();
    }
    
    /**
     * 内存存储
     */
    public void memoryStore() {
        model.memoryStore();
        updateView();
    }
    
    /**
     * 内存加
     */
    public void memoryAdd() {
        model.memoryAdd();
        updateView();
    }
    
    /**
     * 内存减
     */
    public void memorySubtract() {
        model.memorySubtract();
        updateView();
    }
    
    /**
     * 内存调用
     */
    public void memoryRecall() {
        model.memoryRecall();
        updateView();
    }
    
    /**
     * 内存清除
     */
    public void memoryClear() {
        model.memoryClear();
        updateView();
    }
} 