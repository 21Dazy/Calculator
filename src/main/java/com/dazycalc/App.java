/**
 * 项目： GUI计算器
 * 作者： 102300229 叶达
 * 日期： 2025-04-15
 * 描述： 计算器应用程序入口类
 */
package com.dazycalc;

import com.dazycalc.utils.ThemeManager;
import com.dazycalc.view.CalculatorFrame;

import javax.swing.SwingUtilities;

/**
 * 计算器应用程序入口类
 */
public class App {
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