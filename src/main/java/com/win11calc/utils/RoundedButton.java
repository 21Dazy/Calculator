package com.win11calc.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * 自定义圆角按钮
 */
public class RoundedButton extends JButton {
    private int radius;
    private boolean isHovered = false;
    private boolean isPressed = false;
    
    /**
     * 构造函数
     * @param text 按钮文本
     * @param radius 圆角半径
     */
    public RoundedButton(String text, int radius) {
        super(text);
        this.radius = radius;
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        
        // 添加鼠标事件监听
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                isHovered = true;
                repaint();
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                isHovered = false;
                repaint();
            }
            
            public void mousePressed(java.awt.event.MouseEvent evt) {
                isPressed = true;
                repaint();
            }
            
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                isPressed = false;
                repaint();
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 绘制背景
        if (isPressed) {
            // 按下状态 - 加深颜色
            g2.setColor(darken(getBackground(), 0.1f));
        } else if (isHovered) {
            // 悬停状态 - 变亮颜色
            g2.setColor(brighten(getBackground(), 0.1f));
        } else {
            // 默认状态
            g2.setColor(getBackground());
        }
        
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));
        
        // 设置字体和颜色
        g2.setFont(getFont());
        g2.setColor(getForeground());
        
        // 绘制文本（居中）
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(getText());
        int textHeight = fm.getHeight();
        
        int x = (getWidth() - textWidth) / 2;
        int y = (getHeight() - textHeight) / 2 + fm.getAscent();
        
        g2.drawString(getText(), x, y);
        
        g2.dispose();
    }
    
    /**
     * 使颜色变暗
     * @param color 原颜色
     * @param factor 变暗因子
     * @return 变暗后的颜色
     */
    private Color darken(Color color, float factor) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        hsb[2] = Math.max(0, hsb[2] - factor);
        return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
    }
    
    /**
     * 使颜色变亮
     * @param color 原颜色
     * @param factor 变亮因子
     * @return 变亮后的颜色
     */
    private Color brighten(Color color, float factor) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        hsb[2] = Math.min(1.0f, hsb[2] + factor);
        return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
   
    }

    /**
     * 更新背景，刷新按钮
     * @param color 背景颜色
     * @return void
     */
    public void updateBackground(Color color) {
        setBackground(color);
        repaint();
    }
} 