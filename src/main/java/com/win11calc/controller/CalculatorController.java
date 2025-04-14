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
        model.calculateSquare();
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
    }
    
    /**
     * 内存加
     */
    public void memoryAdd() {
        model.memoryAdd();
    }
    
    /**
     * 内存减
     */
    public void memorySubtract() {
        model.memorySubtract();
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
    }
    
    /**
     * 更新视图
     */
    private void updateView() {
        view.updateDisplay(model.getCurrentInput(), model.getExpression());
    }
} 