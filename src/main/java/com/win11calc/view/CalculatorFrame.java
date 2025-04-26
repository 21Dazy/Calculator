package com.win11calc.view;

import com.win11calc.controller.CalculatorController;
import com.win11calc.model.CalculatorModel;
import com.win11calc.model.CalculatorOperation;
import com.win11calc.utils.ColorScheme;
import com.win11calc.utils.RoundedButton;
import com.win11calc.utils.RoundedPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * 计算器主窗口
 */
public class CalculatorFrame extends JFrame {
    
    private final CalculatorModel model;
    private final CalculatorController controller;

    private JMenuBar menuBar;
    private JPanel displayPanel;
    private JLabel expressionLabel;
    private JLabel resultLabel;
    
    private JPanel standardPanel;
    private JPanel scientificPanel;
    
    private boolean isScientificMode = false;
    
    /**
     * 构造函数
     */
    public CalculatorFrame() {
        model = new CalculatorModel();
        controller = new CalculatorController(model, this);
        

        initFrame();
        initComponents();
        initKeyListener();
        
        setDefaultDisplay();
    }
    
    /**
     * 初始化菜单栏
     */
    private void initMenuBar() {
        menuBar = new JMenuBar();
        JMenu modeMenu = new JMenu("模式");
        JMenu helpMenu = new JMenu("帮助");
        JMenu SetMenu = new JMenu("设置");
        
        

        JMenuItem standardModeItem = new JMenuItem("标准计算器");
        JMenuItem scientificModeItem = new JMenuItem("科学计算器");

        JMenuItem aboutItem = new JMenuItem("关于");
        aboutItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "作者：21Dazy\n版本：1.0.0\n发布日期：2025-4-15", "关于", JOptionPane.INFORMATION_MESSAGE);
        });
        
        JMenuItem settingsItem = new JMenuItem("主题设置");



        standardModeItem.addActionListener(e -> {
            if (isScientificMode) {
                toggleMode();
            }
        });
        
        scientificModeItem.addActionListener(e -> {
            if (!isScientificMode) {
                toggleMode();
            }
        });

        settingsItem.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(this, "主题设置", ColorScheme.BACKGROUND);
            if (newColor != null) {
                ColorScheme.BACKGROUND = newColor;
                displayPanel.setBackground(ColorScheme.DISPLAY_BACKGROUND);
                resultLabel.setForeground(ColorScheme.DISPLAY_TEXT);
                expressionLabel.setForeground(ColorScheme.EXPRESSION_TEXT);
                for (Component component : standardPanel.getComponents()) {
                    if (component instanceof RoundedButton) {
                        ((RoundedButton) component).updateBackground(newColor);
                    }
                }
                for (Component component : scientificPanel.getComponents()) {
                    if (component instanceof RoundedButton) {
                        ((RoundedButton) component).updateBackground(newColor);
                    }
                }
            }
        });

        //模式·菜单
        modeMenu.add(standardModeItem);
        modeMenu.add(scientificModeItem);

        //菜单栏
        menuBar.add(modeMenu);
        menuBar.add(helpMenu);
        menuBar.add(SetMenu);

        //帮助·菜单
        helpMenu.add(aboutItem);

        //设置·菜单
        SetMenu.add(settingsItem);
        
        setJMenuBar(menuBar);
    }

    /**
     * 初始化窗口
     */
    private void initFrame() {
        setTitle("计算器");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(320, 580);
        setMinimumSize(new Dimension(320, 580));
        setLocationRelativeTo(null);
        setBackground(ColorScheme.BACKGROUND);
        
        try {
            // 设置Windows系统的应用图标
            setIconImage(new ImageIcon(getClass().getResource("/calculator_icon.png")).getImage());
        } catch (Exception e) {
            // 图标加载失败，忽略
        }
        initMenuBar();
    }
    
    /**
     * 初始化组件
     */
    private void initComponents() {
        // 使用BorderLayout布局
        setLayout(new BorderLayout());
        
        // 初始化显示面板
        initDisplayPanel();
        
        // 初始化按钮面板
        JPanel buttonPanel = new JPanel(new CardLayout());
        
        // 初始化标准计算器面板
        standardPanel = createStandardPanel();
        buttonPanel.add(standardPanel, "standard");
        
        // 初始化科学计算器面板
        scientificPanel = createScientificPanel();
        buttonPanel.add(scientificPanel, "scientific");
        
        // 添加组件到窗口
        add(displayPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
    }
    
    /**
     * 初始化显示面板
     */
    private void initDisplayPanel() {
        displayPanel = new RoundedPanel(0);
        displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.Y_AXIS));
        displayPanel.setBackground(ColorScheme.DISPLAY_BACKGROUND);
        displayPanel.setBorder(BorderFactory.createEmptyBorder(30, 15, 15, 15));
        displayPanel.setPreferredSize(new Dimension(getWidth(), 180));
        
        // 表达式显示标签
        expressionLabel = new JLabel("");
        expressionLabel.setForeground(ColorScheme.EXPRESSION_TEXT);
        expressionLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 18));
        expressionLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        expressionLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        // 结果显示标签（修改名称以符合新逻辑）
        resultLabel = new JLabel("0");
        resultLabel.setForeground(ColorScheme.DISPLAY_TEXT);
        resultLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 60));
        resultLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        resultLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        // 添加标签到显示面板
        displayPanel.add(Box.createVerticalGlue());
        displayPanel.add(expressionLabel);
        displayPanel.add(Box.createVerticalStrut(10));
        displayPanel.add(resultLabel);
    }

    
    
    /**
     * 创建标准计算器面板
     */
    private JPanel createStandardPanel() {
        JPanel panel = new JPanel(new GridLayout(7, 5, 2, 2));
        panel.setBackground(ColorScheme.BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        
        // 第一行按钮 (2nd, deg, sin, cos, tan)
        addButton(panel, "2nd", e -> {/* 切换至二级功能 - 可实现扩展功能 */}, ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        addButton(panel, "deg", e -> {/* 切换角度制/弧度制 - 可实现扩展功能 */}, ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        addButton(panel, "sin", e -> controller.calculateTrigFunction("sin"), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        addButton(panel, "cos", e -> controller.calculateTrigFunction("cos"), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        addButton(panel, "tan", e -> controller.calculateTrigFunction("tan"), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        
        // 第二行按钮 (x^y, lg, ln, (, ))
        addButton(panel, "xʸ", e -> controller.calculatePower(), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        addButton(panel, "lg", e -> controller.calculateLog10(), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        addButton(panel, "ln", e -> controller.calculateLn(), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        addButton(panel, "(", e -> controller.addLeftParenthesis(), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        addButton(panel, ")", e -> controller.addRightParenthesis(), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        
        // 第三行按钮 (√x, AC, ⌫, %, ÷)
        addButton(panel, "√x", e -> controller.calculateSquareRoot(), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        addButton(panel, "AC", e -> controller.clear(), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        addButton(panel, "<-", e -> controller.backspace(), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        addButton(panel, "%", e -> controller.calculatePercent(), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        addButton(panel, "÷", e -> controller.setOperation(CalculatorOperation.DIVIDE), ColorScheme.OPERATION_BUTTON_BACKGROUND);
        
        // 第四行按钮 (x!, 7, 8, 9, ×)
        addButton(panel, "x!", e -> controller.calculateFactorial(), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        addButton(panel, "7", e -> controller.addDigit("7"), ColorScheme.NUMBER_BUTTON_BACKGROUND);
        addButton(panel, "8", e -> controller.addDigit("8"), ColorScheme.NUMBER_BUTTON_BACKGROUND);
        addButton(panel, "9", e -> controller.addDigit("9"), ColorScheme.NUMBER_BUTTON_BACKGROUND);
        addButton(panel, "×", e -> controller.setOperation(CalculatorOperation.MULTIPLY), ColorScheme.OPERATION_BUTTON_BACKGROUND);
        
        // 第五行按钮 (1/x, 4, 5, 6, -)
        addButton(panel, "1/x", e -> controller.calculateReciprocal(), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        addButton(panel, "4", e -> controller.addDigit("4"), ColorScheme.NUMBER_BUTTON_BACKGROUND);
        addButton(panel, "5", e -> controller.addDigit("5"), ColorScheme.NUMBER_BUTTON_BACKGROUND);
        addButton(panel, "6", e -> controller.addDigit("6"), ColorScheme.NUMBER_BUTTON_BACKGROUND);
        addButton(panel, "-", e -> controller.setOperation(CalculatorOperation.SUBTRACT), ColorScheme.OPERATION_BUTTON_BACKGROUND);
        
        // 第六行按钮 (π, 1, 2, 3, +)
        addButton(panel, "π", e -> controller.usePi(), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        addButton(panel, "1", e -> controller.addDigit("1"), ColorScheme.NUMBER_BUTTON_BACKGROUND);
        addButton(panel, "2", e -> controller.addDigit("2"), ColorScheme.NUMBER_BUTTON_BACKGROUND);
        addButton(panel, "3", e -> controller.addDigit("3"), ColorScheme.NUMBER_BUTTON_BACKGROUND);
        addButton(panel, "+", e -> controller.setOperation(CalculatorOperation.ADD), ColorScheme.OPERATION_BUTTON_BACKGROUND);
        
        // 第七行按钮 (+/-, e, 0, ., =)
        addButton(panel, "+/-", e -> controller.toggleSign(), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        addButton(panel, "e", e -> controller.useE(), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        addButton(panel, "0", e -> controller.addDigit("0"), ColorScheme.NUMBER_BUTTON_BACKGROUND);
        addButton(panel, ".", e -> controller.addDecimalPoint(), ColorScheme.NUMBER_BUTTON_BACKGROUND);
        addButton(panel, "=", e -> controller.calculateResult(), ColorScheme.EQUALS_BUTTON_BACKGROUND, ColorScheme.EQUALS_BUTTON_TEXT);
        
        return panel;
    }
    
    /**
     * 创建科学计算器面板
     */
    private JPanel createScientificPanel() {
        JPanel panel = new JPanel(new GridLayout(8, 5, 2, 2));
        panel.setBackground(ColorScheme.BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        
        // 第一行按钮
        addButton(panel, "2nd", e -> {/* 切换至二级功能 - 可实现扩展功能 */}, ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        addButton(panel, "deg", e -> {/* 切换角度制/弧度制 - 可实现扩展功能 */}, ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        addButton(panel, "sin", e -> controller.calculateTrigFunction("sin"), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        addButton(panel, "cos", e -> controller.calculateTrigFunction("cos"), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        addButton(panel, "tan", e -> controller.calculateTrigFunction("tan"), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        
        // 第二行按钮
        addButton(panel, "xʸ", e -> controller.calculatePower(), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        addButton(panel, "lg", e -> controller.calculateLog10(), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        addButton(panel, "ln", e -> controller.calculateLn(), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        addButton(panel, "(", e -> controller.addLeftParenthesis(), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        addButton(panel, ")", e -> controller.addRightParenthesis(), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        
        // 第三行按钮
        addButton(panel, "√x", e -> controller.calculateSquareRoot(), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        addButton(panel, "AC", e -> controller.clear(), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        addButton(panel, "<-", e -> controller.backspace(), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        addButton(panel, "%", e -> controller.calculatePercent(), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        addButton(panel, "÷", e -> controller.setOperation(CalculatorOperation.DIVIDE), ColorScheme.OPERATION_BUTTON_BACKGROUND);
        
        // 第四行按钮
        addButton(panel, "x!", e -> controller.calculateFactorial(), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        addButton(panel, "7", e -> controller.addDigit("7"), ColorScheme.NUMBER_BUTTON_BACKGROUND);
        addButton(panel, "8", e -> controller.addDigit("8"), ColorScheme.NUMBER_BUTTON_BACKGROUND);
        addButton(panel, "9", e -> controller.addDigit("9"), ColorScheme.NUMBER_BUTTON_BACKGROUND);
        addButton(panel, "×", e -> controller.setOperation(CalculatorOperation.MULTIPLY), ColorScheme.OPERATION_BUTTON_BACKGROUND);
        
        // 第五行按钮
        addButton(panel, "1/x", e -> controller.calculateReciprocal(), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        addButton(panel, "4", e -> controller.addDigit("4"), ColorScheme.NUMBER_BUTTON_BACKGROUND);
        addButton(panel, "5", e -> controller.addDigit("5"), ColorScheme.NUMBER_BUTTON_BACKGROUND);
        addButton(panel, "6", e -> controller.addDigit("6"), ColorScheme.NUMBER_BUTTON_BACKGROUND);
        addButton(panel, "-", e -> controller.setOperation(CalculatorOperation.SUBTRACT), ColorScheme.OPERATION_BUTTON_BACKGROUND);
        
        // 第六行按钮
        addButton(panel, "π", e -> controller.usePi(), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        addButton(panel, "1", e -> controller.addDigit("1"), ColorScheme.NUMBER_BUTTON_BACKGROUND);
        addButton(panel, "2", e -> controller.addDigit("2"), ColorScheme.NUMBER_BUTTON_BACKGROUND);
        addButton(panel, "3", e -> controller.addDigit("3"), ColorScheme.NUMBER_BUTTON_BACKGROUND);
        addButton(panel, "+", e -> controller.setOperation(CalculatorOperation.ADD), ColorScheme.OPERATION_BUTTON_BACKGROUND);
        
        // 第七行按钮
        addButton(panel, "+/-", e -> controller.toggleSign(), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        addButton(panel, "e", e -> controller.useE(), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        addButton(panel, "0", e -> controller.addDigit("0"), ColorScheme.NUMBER_BUTTON_BACKGROUND);
        addButton(panel, ".", e -> controller.addDecimalPoint(), ColorScheme.NUMBER_BUTTON_BACKGROUND);
        addButton(panel, "=", e -> controller.calculateResult(), ColorScheme.EQUALS_BUTTON_BACKGROUND, ColorScheme.EQUALS_BUTTON_TEXT);
        
        return panel;
    }
    
    /**
     * 添加按钮到面板
     */
    private void addButton(JPanel panel, String text, Action action, Color backgroundColor) {
        addButton(panel, text, action, backgroundColor, ColorScheme.BUTTON_TEXT);
    }
    
    /**
     * 添加按钮到面板（指定文本颜色）
     */
    private void addButton(JPanel panel, String text, Action action, Color backgroundColor, Color textColor) {
        JButton button = new RoundedButton(text, 8);
         button.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        button.setBackground(backgroundColor);
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.addActionListener(e -> action.actionPerformed(null));
        panel.add(button);
    }
    
    /**
     * 初始化键盘监听器
     */
    private void initKeyListener() {
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_0:
                    case KeyEvent.VK_NUMPAD0:
                        controller.addDigit("0");
                        break;
                    case KeyEvent.VK_1:
                    case KeyEvent.VK_NUMPAD1:
                        controller.addDigit("1");
                        break;
                    case KeyEvent.VK_2:
                    case KeyEvent.VK_NUMPAD2:
                        controller.addDigit("2");
                        break;
                    case KeyEvent.VK_3:
                    case KeyEvent.VK_NUMPAD3:
                        controller.addDigit("3");
                        break;
                    case KeyEvent.VK_4:
                    case KeyEvent.VK_NUMPAD4:
                        controller.addDigit("4");
                        break;
                    case KeyEvent.VK_5:
                    case KeyEvent.VK_NUMPAD5:
                        controller.addDigit("5");
                        break;
                    case KeyEvent.VK_6:
                    case KeyEvent.VK_NUMPAD6:
                        controller.addDigit("6");
                        break;
                    case KeyEvent.VK_7:
                    case KeyEvent.VK_NUMPAD7:
                        controller.addDigit("7");
                        break;
                    case KeyEvent.VK_8:
                    case KeyEvent.VK_NUMPAD8:
                        controller.addDigit("8");
                        break;
                    case KeyEvent.VK_9:
                    case KeyEvent.VK_NUMPAD9:
                        controller.addDigit("9");
                        break;
                    case KeyEvent.VK_PERIOD:
                    case KeyEvent.VK_DECIMAL:
                        controller.addDecimalPoint();
                        break;
                    case KeyEvent.VK_PLUS:
                    case KeyEvent.VK_ADD:
                        controller.setOperation(CalculatorOperation.ADD);
                        break;
                    case KeyEvent.VK_MINUS:
                    case KeyEvent.VK_SUBTRACT:
                        controller.setOperation(CalculatorOperation.SUBTRACT);
                        break;
                    case KeyEvent.VK_MULTIPLY:
                        controller.setOperation(CalculatorOperation.MULTIPLY);
                        break;
                    case KeyEvent.VK_SLASH:
                    case KeyEvent.VK_DIVIDE:
                        controller.setOperation(CalculatorOperation.DIVIDE);
                        break;
                    case KeyEvent.VK_ENTER:
                    case KeyEvent.VK_EQUALS:
                        controller.calculateResult();
                        break;
                    case KeyEvent.VK_BACK_SPACE:
                        controller.backspace();
                        break;
                    case KeyEvent.VK_DELETE:
                        controller.clear();
                        break;
                    case KeyEvent.VK_ESCAPE:
                        controller.clear();
                        break;
                    // 添加括号支持
                    case KeyEvent.VK_OPEN_BRACKET:
                    case KeyEvent.VK_LEFT_PARENTHESIS:
                        controller.addLeftParenthesis();
                        break;
                    case KeyEvent.VK_CLOSE_BRACKET:
                    case KeyEvent.VK_RIGHT_PARENTHESIS:
                        controller.addRightParenthesis();
                        break;
                }
            }
        });
        
        // 设置窗口可获取焦点
        setFocusable(true);
        requestFocusInWindow();
    }
    
    /**
     * 设置默认显示
     */
    private void setDefaultDisplay() {
        updateDisplay("0", "", "");
    }
    
    /**
     * 更新显示
     * @param input 输入值或当前结果
     * @param expression 表达式
     * @param result 计算结果
     */
    public void updateDisplay(String input, String expression, String result) {
        // 检查是否显示错误信息
        if (expression.equals("错误")) {
            // 如果是错误信息，显示在结果区
            resultLabel.setText(input);
            expressionLabel.setText(expression);
        } else if (expression.isEmpty()) {
            // 空表达式时
            expressionLabel.setText("");
            resultLabel.setText(input);
        } else {
            // 将表达式显示在上方标签
            expressionLabel.setText(expression);
            
            // 当前结果显示在下方标签
            resultLabel.setText(result);
        }
        
        // 自动调整字体大小，以适应长文本
        adjustFontSize(resultLabel, 60);
    }
    
    /**
     * 自动调整标签的字体大小
     * @param label 要调整的标签
     * @param maxSize 最大字体大小
     */
    private void adjustFontSize(JLabel label, int maxSize) {
        String text = label.getText();
        int size = maxSize;
        
        // 根据文本长度调整字体大小
        if (text.length() > 10) {
            size = Math.max(24, maxSize - (text.length() - 10) * 2);
        }
        
        label.setFont(new Font("Microsoft YaHei", Font.BOLD, size));
    }
    
    /**
     * 切换计算器模式
     */
    public void toggleMode() {
        isScientificMode = !isScientificMode;
        
        CardLayout cl = (CardLayout)(standardPanel.getParent().getLayout());
        cl.show(standardPanel.getParent(), isScientificMode ? "scientific" : "standard");
        
        // 调整窗口尺寸
        if (isScientificMode) {
            setSize(320, 580);
        } else {
            setSize(320, 580);
        }
    }
    
    /**
     * 函数接口，用于定义按钮动作
     */
    @FunctionalInterface
    interface Action {
        void actionPerformed(ActionEvent e);
    }
} 