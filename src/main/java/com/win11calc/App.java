package com.win11calc;

import com.win11calc.utils.ThemeManager;
import com.win11calc.view.CalculatorFrame;

import javax.swing.SwingUtilities;

/**
 * 计算器应用程序入口类
 */
public class App {
    /**
     * 主方法
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        // 在EDT线程中启动应用
        SwingUtilities.invokeLater(() -> {
            // 强制设置为小米主题
            ThemeManager.applyTheme(ThemeManager.Theme.XIAOMI);
            
            // 加载主题设置
            ThemeManager.loadSettings();
            
            // 创建并显示主窗口
            CalculatorFrame frame = new CalculatorFrame();
            frame.setVisible(true);
        });
    }
} 