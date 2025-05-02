package com.win11calc.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * 自定义圆角面板
 */
public class RoundedPanel extends JPanel {
    private final int customCornerRadius;
    private boolean useCustomRadius = false;
    
    /**
     * 构造函数
     */
    public RoundedPanel() {
        super();
        this.customCornerRadius = 0;
        setOpaque(false);
    }
    
    /**
     * 构造函数
     * 
     * @param cornerRadius 自定义圆角半径，如果为0则使用UIConfig中的设置
     */
    public RoundedPanel(int cornerRadius) {
        super();
        this.customCornerRadius = cornerRadius;
        this.useCustomRadius = cornerRadius > 0;
        setOpaque(false);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 计算绘制区域
        int width = getWidth();
        int height = getHeight();
        
        // 获取圆角半径，优先使用自定义值，否则使用当前主题设置
        int cornerRadius = useCustomRadius ? customCornerRadius : UIConfig.PANEL_CORNER_RADIUS;
        
        // 创建圆角矩形
        RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(
                0, 0, width, height, cornerRadius, cornerRadius);
        
        // 根据当前主题选择效果
        if (ThemeManager.getCurrentTheme() == ThemeManager.Theme.MACOS) {
            // Mac风格的轻微阴影效果
            applyMacOSShadow(g2, width, height, cornerRadius);
        } else if (ThemeManager.getCurrentTheme() == ThemeManager.Theme.WINDOWS) {
            // Windows风格的亚克力效果
            applyWindowsEffect(g2, width, height, cornerRadius);
        } else if (ThemeManager.getCurrentTheme() == ThemeManager.Theme.XIAOMI) {
            // 小米风格的扁平简洁效果
            applyXiaomiEffect(g2, width, height, cornerRadius);
        }
        
        // 绘制面板背景
        g2.setColor(getBackground());
        g2.fill(roundedRectangle);
        
        // 绘制细微的边框
        if (cornerRadius > 0) {
            g2.setColor(ColorScheme.BORDER_COLOR);
            g2.setStroke(new BasicStroke(0.5f));
            g2.draw(roundedRectangle);
        }
        
        g2.dispose();
    }
    
    /**
     * 应用macOS风格的阴影效果
     */
    private void applyMacOSShadow(Graphics2D g2, int width, int height, int cornerRadius) {
        if (cornerRadius > 0 && UIConfig.ENABLE_BLUR) {
            // 绘制轻微的阴影
            g2.setColor(new Color(0, 0, 0, 8));
            g2.fill(new RoundRectangle2D.Float(2, 2, width - 2, height, cornerRadius, cornerRadius));
            
            // 添加顶部高光
            g2.setColor(new Color(255, 255, 255, 15));
            g2.drawLine(
                cornerRadius / 2,
                1,
                width - cornerRadius / 2,
                1
            );
        }
    }
    
    /**
     * 应用Windows风格的亚克力效果
     */
    private void applyWindowsEffect(Graphics2D g2, int width, int height, int cornerRadius) {
        if (UIConfig.ENABLE_BLUR) {
            // 添加微妙的纹理
            for (int i = 0; i < width; i += 8) {
                for (int j = 0; j < height; j += 8) {
                    if (Math.random() > 0.7) {
                        g2.setColor(new Color(255, 255, 255, 2));
                        g2.fillRect(i, j, 2, 2);
                    }
                }
            }
            
            // 添加细微阴影效果
            g2.setColor(new Color(0, 0, 0, 15));
            g2.fillRect(0, height - 5, width, 5);
        }
    }
    
    /**
     * 应用小米风格的扁平效果
     */
    private void applyXiaomiEffect(Graphics2D g2, int width, int height, int cornerRadius) {
        // 小米风格保持绝对的扁平简洁，不添加任何额外纹理或阴影
        // 只有边框颜色与背景相近，营造无边框感
        g2.setColor(new Color(0, 0, 0, 10));
        g2.setStroke(new BasicStroke(0.5f));
        g2.draw(new RoundRectangle2D.Float(0, 0, width - 1, height - 1, cornerRadius, cornerRadius));
    }
} 