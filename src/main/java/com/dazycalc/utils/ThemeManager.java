package com.dazycalc.utils;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import javax.swing.SwingUtilities;
import javax.swing.JPanel;
import javax.swing.JFrame;

/**
 * 主题管理类，用于管理不同UI风格的配色方案和样式
 */
public class ThemeManager {
    // 主题枚举
    public enum Theme {
        XIAOMI("小米风格"),
        MACOS("macOS风格"),
        WINDOWS("Windows风格");
        
        private final String displayName;
        
        Theme(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    // 当前主题
    private static Theme currentTheme = Theme.XIAOMI; // 将小米主题设为默认
    // 配置文件路径
    private static final String CONFIG_FILE = "calculator_config.properties";
    
    /**
     * 加载用户偏好设置
     */
    public static void loadSettings() {
        File configFile = new File(CONFIG_FILE);
        if (configFile.exists()) {
            Properties props = new Properties();
            try (FileInputStream fis = new FileInputStream(configFile)) {
                props.load(fis);
                String themeName = props.getProperty("theme");
                
                if (themeName != null) {
                    try {
                        currentTheme = Theme.valueOf(themeName);
                    } catch (IllegalArgumentException e) {
                        // 无效的主题名，使用默认值
                        currentTheme = Theme.XIAOMI;
                    }
                }
            } catch (IOException e) {
                // 读取失败，使用默认值
                currentTheme = Theme.XIAOMI;
            }
        }
        
        // 应用当前主题
        applyTheme(currentTheme);
    }
    
    /**
     * 保存用户设置
     */
    public static void saveSettings() {
        Properties props = new Properties();
        props.setProperty("theme", currentTheme.name());
        
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            props.store(fos, "Calculator Theme Settings");
        } catch (IOException e) {
            System.err.println("保存配置文件失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取当前主题
     */
    public static Theme getCurrentTheme() {
        return currentTheme;
    }
    
    /**
     * 切换并应用主题
     * @param theme 要应用的主题
     */
    public static void applyTheme(Theme theme) {
        currentTheme = theme;
        
        switch (theme) {
            case XIAOMI:
                applyXiaomiTheme();
                break;
            case MACOS:
                applyMacOSTheme();
                break;
            case WINDOWS:
                applyWindowsTheme();
                break;
        }
        
        // 保存设置
        saveSettings();
    }
    
    /**
     * 应用小米风格暗色主题
     */
    private static void applyXiaomiTheme() {
        // 背景颜色
        ColorScheme.BACKGROUND = new Color(0, 0, 0); // 纯黑色背景
        
        // 显示区域背景
        ColorScheme.DISPLAY_BACKGROUND = new Color(0, 0, 0);  // 纯黑背景
        
        // 显示文本颜色
        ColorScheme.DISPLAY_TEXT = new Color(255, 255, 255);  // 白色显示文本
        
        // 表达式文本颜色
        ColorScheme.EXPRESSION_TEXT = new Color(200, 200, 200);  // 浅灰色表达式文本
        
        // 按钮背景颜色
        ColorScheme.NUMBER_BUTTON_BACKGROUND = new Color(45, 45, 45);  // 深灰色数字按钮背景
        ColorScheme.OPERATION_BUTTON_BACKGROUND = new Color(255, 140, 0);  // 橙色操作按钮背景（小米风格）
        ColorScheme.FUNCTION_BUTTON_BACKGROUND = new Color(45, 45, 45);  // 深灰色功能按钮背景
        ColorScheme.EQUALS_BUTTON_BACKGROUND = new Color(255, 140, 0);  // 橙色等号按钮背景
        
        // 按钮文本颜色
        ColorScheme.BUTTON_TEXT = new Color(255, 255, 255);  // 白色按钮文本
        ColorScheme.EQUALS_BUTTON_TEXT = new Color(255, 255, 255);  // 白色等号按钮文本
        
        // 高亮颜色
        ColorScheme.HIGHLIGHT_COLOR = new Color(255, 140, 0);  // 橙色高亮
        
        // 边框颜色
        ColorScheme.BORDER_COLOR = new Color(45, 45, 45);  // 与按钮同色边框
        
        // 设置按钮圆角
        UIConfig.BUTTON_CORNER_RADIUS = 24;  // 大圆角按钮
        UIConfig.PANEL_CORNER_RADIUS = 20;   // 面板圆角
        
        // 字体
        UIConfig.FONT_NAME = "Arial";  // 使用Arial字体
    }
    
    /**
     * 应用 macOS 风格主题
     */
    private static void applyMacOSTheme() {
        // 背景颜色
        ColorScheme.BACKGROUND = new Color(242, 242, 247); // Mac 风格浅灰色背景
        
        // 显示区域背景
        ColorScheme.DISPLAY_BACKGROUND = new Color(242, 242, 247, 240);  // 半透明背景
        
        // 显示文本颜色
        ColorScheme.DISPLAY_TEXT = new Color(0, 0, 0);  // 黑色显示文本
        
        // 表达式文本颜色
        ColorScheme.EXPRESSION_TEXT = new Color(100, 100, 100);  // 灰色表达式文本
        
        // 按钮背景颜色
        ColorScheme.NUMBER_BUTTON_BACKGROUND = new Color(252, 252, 252, 240);  // 数字按钮背景 - 白色半透明
        ColorScheme.OPERATION_BUTTON_BACKGROUND = new Color(216, 216, 220, 240);  // 操作按钮背景 - 浅灰色半透明
        ColorScheme.FUNCTION_BUTTON_BACKGROUND = new Color(229, 229, 234, 240);  // 功能按钮背景 - 中灰色半透明
        ColorScheme.EQUALS_BUTTON_BACKGROUND = new Color(0, 122, 255);  // 等号按钮背景 - Mac蓝色
        
        // 按钮文本颜色
        ColorScheme.BUTTON_TEXT = new Color(0, 0, 0);  // 黑色按钮文本
        ColorScheme.EQUALS_BUTTON_TEXT = new Color(255, 255, 255);  // 白色等号按钮文本
        
        // 高亮颜色
        ColorScheme.HIGHLIGHT_COLOR = new Color(255, 149, 0);  // Mac橙色高亮
        
        // 边框颜色
        ColorScheme.BORDER_COLOR = new Color(200, 200, 200, 100);  // 浅灰色半透明边框
        
        // 设置按钮圆角
        UIConfig.BUTTON_CORNER_RADIUS = 20;
        UIConfig.PANEL_CORNER_RADIUS = 15;
        
        // 字体
        UIConfig.FONT_NAME = "SF Pro Display";
    }
    
    /**
     * 应用 Windows 风格主题
     */
    private static void applyWindowsTheme() {
        // 背景颜色
        ColorScheme.BACKGROUND = new Color(32, 32, 32); // Windows深色背景
        
        // 显示区域背景
        ColorScheme.DISPLAY_BACKGROUND = new Color(24, 24, 24, 240);  // 更深的背景
        
        // 显示文本颜色
        ColorScheme.DISPLAY_TEXT = new Color(255, 255, 255);  // 白色显示文本
        
        // 表达式文本颜色
        ColorScheme.EXPRESSION_TEXT = new Color(180, 180, 180);  // 浅灰色表达式文本
        
        // 按钮背景颜色
        ColorScheme.NUMBER_BUTTON_BACKGROUND = new Color(50, 50, 50, 240);  // 数字按钮背景 - 深灰色
        ColorScheme.OPERATION_BUTTON_BACKGROUND = new Color(0, 120, 215, 240);  // 操作按钮背景 - Windows蓝色
        ColorScheme.FUNCTION_BUTTON_BACKGROUND = new Color(40, 40, 40, 240);  // 功能按钮背景 - 中灰色
        ColorScheme.EQUALS_BUTTON_BACKGROUND = new Color(0, 120, 215);  // 等号按钮背景 - Windows蓝色
        
        // 按钮文本颜色
        ColorScheme.BUTTON_TEXT = new Color(255, 255, 255);  // 白色按钮文本
        ColorScheme.EQUALS_BUTTON_TEXT = new Color(255, 255, 255);  // 白色等号按钮文本
        
        // 高亮颜色
        ColorScheme.HIGHLIGHT_COLOR = new Color(255, 185, 0);  // Windows黄色高亮
        
        // 边框颜色
        ColorScheme.BORDER_COLOR = new Color(70, 70, 70, 100);  // 深灰色半透明边框
        
        // 设置按钮圆角
        UIConfig.BUTTON_CORNER_RADIUS = 4;
        UIConfig.PANEL_CORNER_RADIUS = 2;
        
        // 字体
        UIConfig.FONT_NAME = "Segoe UI";
    }
    
    /**
     * 刷新应用界面
     * @param rootComponent 需要刷新的根组件
     */
    public static void refreshUI(java.awt.Component rootComponent) {
        SwingUtilities.updateComponentTreeUI(rootComponent);
        rootComponent.repaint();
        
        // 检查并更新背景颜色
        if (rootComponent instanceof JPanel || rootComponent instanceof JFrame) {
            rootComponent.setBackground(ColorScheme.BACKGROUND);
        }
        
        // 遍历所有子组件并重绘
        if (rootComponent instanceof java.awt.Container) {
            java.awt.Container container = (java.awt.Container) rootComponent;
            for (java.awt.Component component : container.getComponents()) {
                // 更新面板背景色
                if (component instanceof JPanel) {
                    component.setBackground(ColorScheme.BACKGROUND);
                }
                
                component.invalidate();
                component.repaint();
                
                if (component instanceof java.awt.Container) {
                    refreshUI(component);
                }
            }
        }
    }
} 