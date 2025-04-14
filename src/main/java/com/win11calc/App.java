package com.win11calc;

import com.formdev.flatlaf.FlatDarkLaf;
import com.win11calc.view.CalculatorFrame;

import javax.swing.*;

/**
 * Windows 11 风格计算器主应用程序
 */
public class App {
    public static void main(String[] args) {
        try {
            // 设置FlatLaf暗色主题作为默认Look and Feel
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception ex) {
            System.err.println("无法初始化主题: " + ex);
        }
        
        // 在事件调度线程上启动GUI
        SwingUtilities.invokeLater(() -> {
            CalculatorFrame calculator = new CalculatorFrame();
            calculator.setVisible(true);
        });
    }
} 