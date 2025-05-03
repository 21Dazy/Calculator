package com.dazycalc.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

/**
 * 自定义圆角按钮
 */
public class RoundedButton extends JButton {
    private Color backgroundColor;
    private boolean isPressed = false;
    private boolean isHovered = false;
    
    /**
     * 构造函数
     * 
     * @param text 按钮文本
     */
    public RoundedButton(String text) {
        super(text);
        this.backgroundColor = getBackground();
        
        // 设置按钮为透明，以便自定义绘制
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        
        // 添加鼠标事件监听
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                isPressed = true;
                repaint();
            }
            
            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                isPressed = false;
                repaint();
            }
            
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                isHovered = true;
                repaint();
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                isHovered = false;
                repaint();
            }
        });
    }
    
    /**
     * 构造函数（带指定圆角半径）
     * 
     * @param text 按钮文本
     * @param cornerRadius 圆角半径 (废弃，现在使用UIConfig中的设置)
     * @deprecated 使用 {@link #RoundedButton(String)} 代替
     */
    @Deprecated
    public RoundedButton(String text, int cornerRadius) {
        this(text);
    }
    
    /**
     * 更新按钮背景颜色
     * 
     * @param color 新的背景颜色
     */
    public void updateBackground(Color color) {
        this.backgroundColor = color;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        int width = getWidth();
        int height = getHeight();
        
        // 创建缓冲图像
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bufferedImage.createGraphics();
        
        // 抗锯齿设置
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // 获取当前主题的圆角半径
        int cornerRadius = UIConfig.BUTTON_CORNER_RADIUS;
        
        // 创建圆角矩形形状
        RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(
                0, 0, width, height, cornerRadius, cornerRadius);
        
        // 裁剪区域为圆角矩形
        g2.setClip(roundedRectangle);
        
        // 计算按钮颜色
        Color buttonColor = calculateButtonColor();
        
        // 绘制背景
        g2.setColor(buttonColor);
        g2.fill(roundedRectangle);
        
        // Windows 风格下添加额外的亚克力材质效果
        if (ThemeManager.getCurrentTheme() == ThemeManager.Theme.WINDOWS && UIConfig.ENABLE_BLUR) {
            applyAcrylicEffect(g2, roundedRectangle);
        }
        
        // 绘制边框
        g2.setColor(ColorScheme.BORDER_COLOR);
        g2.setStroke(new BasicStroke(0.5f));
        g2.draw(roundedRectangle);
        
        // 如果是 macOS 风格，添加按钮阴影
        if (ThemeManager.getCurrentTheme() == ThemeManager.Theme.MACOS) {
            applyMacOSShadow(g2, roundedRectangle);
        }
        
        // 如果是小米风格，保持简洁扁平的效果
        if (ThemeManager.getCurrentTheme() == ThemeManager.Theme.XIAOMI) {
            applyXiaomiStyle(g2, roundedRectangle);
        }
        
        // 释放图形资源
        g2.dispose();
        
        // 绘制缓冲的图像
        g = g.create();
        g.drawImage(bufferedImage, 0, 0, null);
        
        // 绘制按钮文本
        super.paintComponent(g);
        g.dispose();
    }
    
    /**
     * 计算按钮颜色（根据悬浮和按下状态）
     */
    private Color calculateButtonColor() {
        Color buttonColor = backgroundColor;
        
        if (isPressed) {
            // 按下状态 - 颜色变暗
            return new Color(
                Math.max(0, buttonColor.getRed() - 20),
                Math.max(0, buttonColor.getGreen() - 20),
                Math.max(0, buttonColor.getBlue() - 20),
                buttonColor.getAlpha()
            );
        } else if (isHovered) {
            // 悬浮状态 - 颜色变亮
            return new Color(
                Math.min(255, buttonColor.getRed() + 10),
                Math.min(255, buttonColor.getGreen() + 10),
                Math.min(255, buttonColor.getBlue() + 10),
                buttonColor.getAlpha()
            );
        }
        
        return buttonColor;
    }
    
    /**
     * 应用亚克力材质效果 (Windows风格)
     */
    private void applyAcrylicEffect(Graphics2D g2, RoundRectangle2D shape) {
        // 添加微妙的纹理
        for (int i = 0; i < getWidth(); i += 4) {
            for (int j = 0; j < getHeight(); j += 4) {
                if (Math.random() > 0.5) {
                    g2.setColor(new Color(255, 255, 255, 3));
                    g2.fillRect(i, j, 2, 2);
                }
            }
        }
        
        // 添加渐变叠加
        GradientPaint gradient = new GradientPaint(
            0, 0, new Color(255, 255, 255, 5),
            0, getHeight(), new Color(0, 0, 0, 5)
        );
        g2.setPaint(gradient);
        g2.fill(shape);
    }
    
    /**
     * 应用macOS样式阴影
     */
    private void applyMacOSShadow(Graphics2D g2, RoundRectangle2D shape) {
        // 绘制轻微的内部阴影
        g2.setColor(new Color(0, 0, 0, 10));
        g2.setStroke(new BasicStroke(1.0f));
        g2.draw(new RoundRectangle2D.Float(
            1, 1, getWidth()-2, getHeight()-2, 
            UIConfig.BUTTON_CORNER_RADIUS, UIConfig.BUTTON_CORNER_RADIUS
        ));
        
        // 添加顶部高光
        g2.setColor(new Color(255, 255, 255, 30));
        g2.drawLine(
            UIConfig.BUTTON_CORNER_RADIUS / 2, 
            1, 
            getWidth() - UIConfig.BUTTON_CORNER_RADIUS / 2, 
            1
        );
    }
    
    /**
     * 应用小米风格的扁平效果
     */
    private void applyXiaomiStyle(Graphics2D g2, RoundRectangle2D shape) {
        // 小米风格简洁，无额外装饰效果
        // 只对操作按钮添加极轻微的内阴影，增强触摸感
        String text = getText();
        if (text.equals("+") || text.equals("-") || 
            text.equals("×") || text.equals("÷") || 
            text.equals("=")) {
            
            g2.setColor(new Color(0, 0, 0, 15));
            g2.setStroke(new BasicStroke(0.5f));
            g2.draw(new RoundRectangle2D.Float(
                1, 1, getWidth()-2, getHeight()-2, 
                UIConfig.BUTTON_CORNER_RADIUS-1, UIConfig.BUTTON_CORNER_RADIUS-1
            ));
        }
    }
    
    @Override
    public void setBackground(Color bg) {
        super.setBackground(bg);
        this.backgroundColor = bg;
        repaint();
    }
    
    @Override
    protected void paintBorder(Graphics g) {
        // 不绘制默认边框
    }
    
    @Override
    public boolean contains(int x, int y) {
        // 确定点击区域也是圆角的
        if (0 <= x && x <= getWidth() && 0 <= y && y <= getHeight()) {
            int radius = UIConfig.BUTTON_CORNER_RADIUS;
            
            // 检查四个角落
            if ((x < radius && y < radius && 
                !new RoundRectangle2D.Float(0, 0, radius * 2, radius * 2, radius, radius).contains(x, y)) ||
                (x > getWidth() - radius && y < radius && 
                !new RoundRectangle2D.Float(getWidth() - radius * 2, 0, radius * 2, radius * 2, radius, radius).contains(x, y)) ||
                (x < radius && y > getHeight() - radius && 
                !new RoundRectangle2D.Float(0, getHeight() - radius * 2, radius * 2, radius * 2, radius, radius).contains(x, y)) ||
                (x > getWidth() - radius && y > getHeight() - radius && 
                !new RoundRectangle2D.Float(getWidth() - radius * 2, getHeight() - radius * 2, radius * 2, radius * 2, radius, radius).contains(x, y))) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }
} 