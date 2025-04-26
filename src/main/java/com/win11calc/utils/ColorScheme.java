package com.win11calc.utils;

import java.awt.Color;

/**
 * 计算器颜色方案
 */
public class ColorScheme {
    // 背景颜色
    public static Color BACKGROUND = new Color(242, 242, 247); // Mac 风格浅灰色背景
    
    // 显示区域背景
    public static Color DISPLAY_BACKGROUND = new Color(242, 242, 247);  // 与背景相同
    
    // 显示文本颜色
    public static Color DISPLAY_TEXT = new Color(0, 0, 0);  // 黑色显示文本
    
    // 表达式文本颜色
    public static Color EXPRESSION_TEXT = new Color(100, 100, 100);  // 灰色表达式文本
    
    // 按钮背景颜色
    public static Color NUMBER_BUTTON_BACKGROUND = new Color(252, 252, 252);  // 数字按钮背景 - 白色
    public static Color OPERATION_BUTTON_BACKGROUND = new Color(216, 216, 220);  // 操作按钮背景 - 浅灰色
    public static Color FUNCTION_BUTTON_BACKGROUND = new Color(229, 229, 234);  // 功能按钮背景 - 中灰色
    public static Color EQUALS_BUTTON_BACKGROUND = new Color(0, 122, 255);  // 等号按钮背景 - Mac蓝色
    
    // 按钮文本颜色
    public static Color BUTTON_TEXT = new Color(0, 0, 0);  // 黑色按钮文本
    public static Color EQUALS_BUTTON_TEXT = new Color(255, 255, 255);  // 白色等号按钮文本
    
    // 高亮颜色
    public static Color HIGHLIGHT_COLOR = new Color(255, 149, 0);  // Mac橙色高亮
    
    // 边框颜色
    public static Color BORDER_COLOR = new Color(200, 200, 200);  // 浅灰色边框
} 