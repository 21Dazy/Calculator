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
    private boolean isSecondMode = false;  // 2nd功能开关状态
    private boolean isRadianMode = true;   // 弧度制开关状态，true为弧度制，false为角度制
    
    // 三角函数按钮，需要保存引用以便在2nd模式下更新文本
    private JButton sinButton;
    private JButton cosButton;
    private JButton tanButton;
    private JButton secondButtonStandard; // 标准面板的2nd按钮
    private JButton secondButtonScientific; // 科学面板的2nd按钮
    private JButton degButtonStandard; // 标准面板的deg按钮
    private JButton degButtonScientific; // 科学面板的deg按钮
    
    /**
     * 构造函数
     */
    public CalculatorFrame() {
        model = new CalculatorModel();
        controller = new CalculatorController(model, this);
        
        initFrame();
        initComponents();
        initKeyListener();
        
        // 确保所有按钮引用初始化完成后，再设置默认显示和状态
        initButtonReferences();
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
        secondButtonStandard = addFunctionButton(panel, "2nd", e -> toggleSecondMode(), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        degButtonStandard = addFunctionButton(panel, "rad", e -> toggleAngleMode(), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        sinButton = addFunctionButton(panel, "sin", e -> trigFunctionClick("sin"), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        cosButton = addFunctionButton(panel, "cos", e -> trigFunctionClick("cos"), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        tanButton = addFunctionButton(panel, "tan", e -> trigFunctionClick("tan"), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        
        // 第二行按钮 (x^y, x², Mod, (, ))
        addButton(panel, "x^y", e -> controller.calculatePower(), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        addButton(panel, "x²", e -> controller.calculateSquare(), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        addButton(panel, "Mod", e -> controller.calculateModulo(), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
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
        secondButtonScientific = addFunctionButton(panel, "2nd", e -> toggleSecondMode(), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        addButton(panel, "exp", e -> controller.addScientificNotation(), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        sinButton = addFunctionButton(panel, "sin", e -> trigFunctionClick("sin"), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        cosButton = addFunctionButton(panel, "cos", e -> trigFunctionClick("cos"), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        tanButton = addFunctionButton(panel, "tan", e -> trigFunctionClick("tan"), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        
        // 第二行按钮
        addButton(panel, "x^y", e -> controller.calculatePower(), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        addButton(panel, "x²", e -> controller.calculateSquare(), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        addButton(panel, "Mod", e -> controller.calculateModulo(), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        addButton(panel, "|x|", e -> controller.calculateAbsoluteValue(), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        degButtonScientific = addFunctionButton(panel, "rad", e -> toggleAngleMode(), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        
        // 第三行按钮
        addButton(panel, "√x", e -> controller.calculateSquareRoot(), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        addButton(panel, "AC", e -> controller.clear(), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        addButton(panel, "<-", e -> controller.backspace(), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        addButton(panel, "(", e -> controller.addLeftParenthesis(), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        addButton(panel, ")", e -> controller.addRightParenthesis(), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        
        // 第四行按钮
        addButton(panel, "x!", e -> controller.calculateFactorial(), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        addButton(panel, "7", e -> controller.addDigit("7"), ColorScheme.NUMBER_BUTTON_BACKGROUND);
        addButton(panel, "8", e -> controller.addDigit("8"), ColorScheme.NUMBER_BUTTON_BACKGROUND);
        addButton(panel, "9", e -> controller.addDigit("9"), ColorScheme.NUMBER_BUTTON_BACKGROUND);
        addButton(panel, "÷", e -> controller.setOperation(CalculatorOperation.DIVIDE), ColorScheme.OPERATION_BUTTON_BACKGROUND);
        
        // 第五行按钮
        addButton(panel, "1/x", e -> controller.calculateReciprocal(), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        addButton(panel, "4", e -> controller.addDigit("4"), ColorScheme.NUMBER_BUTTON_BACKGROUND);
        addButton(panel, "5", e -> controller.addDigit("5"), ColorScheme.NUMBER_BUTTON_BACKGROUND);
        addButton(panel, "6", e -> controller.addDigit("6"), ColorScheme.NUMBER_BUTTON_BACKGROUND);
        addButton(panel, "×", e -> controller.setOperation(CalculatorOperation.MULTIPLY), ColorScheme.OPERATION_BUTTON_BACKGROUND);
        
        // 第六行按钮
        addButton(panel, "%", e -> controller.calculatePercent(), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        addButton(panel, "1", e -> controller.addDigit("1"), ColorScheme.NUMBER_BUTTON_BACKGROUND);
        addButton(panel, "2", e -> controller.addDigit("2"), ColorScheme.NUMBER_BUTTON_BACKGROUND);
        addButton(panel, "3", e -> controller.addDigit("3"), ColorScheme.NUMBER_BUTTON_BACKGROUND);
        addButton(panel, "-", e -> controller.setOperation(CalculatorOperation.SUBTRACT), ColorScheme.OPERATION_BUTTON_BACKGROUND);
        
        // 第七行按钮
        addButton(panel, "π", e -> controller.usePi(), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        addButton(panel, "0", e -> controller.addDigit("0"), ColorScheme.NUMBER_BUTTON_BACKGROUND);
        addButton(panel, ".", e -> controller.addDecimalPoint(), ColorScheme.NUMBER_BUTTON_BACKGROUND);
        addButton(panel, "+/-", e -> controller.toggleSign(), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        addButton(panel, "+", e -> controller.setOperation(CalculatorOperation.ADD), ColorScheme.OPERATION_BUTTON_BACKGROUND);
        
        // 第八行按钮
        addButton(panel, "e", e -> controller.useE(), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        addButton(panel, "ln", e -> controller.calculateLn(), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
        addButton(panel, "log", e -> controller.calculateLog10(), ColorScheme.FUNCTION_BUTTON_BACKGROUND);
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
     * 添加功能按钮并返回按钮引用
     */
    private JButton addFunctionButton(JPanel panel, String text, Action action, Color backgroundColor) {
        JButton button = new RoundedButton(text, 8);
        button.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        button.setBackground(backgroundColor);
        button.setForeground(ColorScheme.BUTTON_TEXT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.addActionListener(e -> action.actionPerformed(null));
        panel.add(button);
        return button;
    }
    
    /**
     * 初始化按钮引用
     * 确保在界面初始化后立即获取所有按钮的引用
     */
    private void initButtonReferences() {
        // 获取标准面板中的按钮引用
        if (standardPanel != null) {
            for (Component component : standardPanel.getComponents()) {
                if (component instanceof JButton) {
                    JButton button = (JButton) component;
                    String text = button.getText().toLowerCase();
                    if (text.equals("sin")) {
                        sinButton = button;
                    } else if (text.equals("cos")) {
                        cosButton = button;
                    } else if (text.equals("tan")) {
                        tanButton = button;
                    } else if (text.equals("2nd")) {
                        secondButtonStandard = button;
                    } else if (text.equals("rad") || text.equals("deg")) {
                        degButtonStandard = button;
                    }
                }
            }
        }
        
        // 获取科学面板中的按钮引用
        if (scientificPanel != null) {
            for (Component component : scientificPanel.getComponents()) {
                if (component instanceof JButton) {
                    JButton button = (JButton) component;
                    String text = button.getText().toLowerCase();
                    if (text.equals("sin")) {
                        // 如果已经在标准模式下获取到引用，则科学模式下的引用是备用的
                        if (!isScientificMode) {
                            // 仅保存引用，不更改当前显示的按钮
                        } else {
                            sinButton = button;
                        }
                    } else if (text.equals("cos")) {
                        if (!isScientificMode) {
                            // 仅保存引用，不更改当前显示的按钮
                        } else {
                            cosButton = button;
                        }
                    } else if (text.equals("tan")) {
                        if (!isScientificMode) {
                            // 仅保存引用，不更改当前显示的按钮
                        } else {
                            tanButton = button;
                        }
                    } else if (text.equals("2nd")) {
                        secondButtonScientific = button;
                    } else if (text.equals("rad") || text.equals("deg")) {
                        degButtonScientific = button;
                    }
                }
            }
        }
        
        // 根据当前模式更新按钮引用
        updateActiveButtonReferences();
        
        // 初始化角度模式按钮状态
        updateAngleModeDisplay();
    }
    
    /**
     * 更新当前活动的按钮引用
     */
    private void updateActiveButtonReferences() {
        if (isScientificMode) {
            // 在科学模式下，使用科学面板的按钮引用
            for (Component component : scientificPanel.getComponents()) {
                if (component instanceof JButton) {
                    JButton button = (JButton) component;
                    String text = button.getText().toLowerCase();
                    if (text.contains("sin") || text.contains("arcsin")) {
                        sinButton = button;
                    } else if (text.contains("cos") || text.contains("arccos")) {
                        cosButton = button;
                    } else if (text.contains("tan") || text.contains("arctan")) {
                        tanButton = button;
                    }
                }
            }
        } else {
            // 在标准模式下，使用标准面板的按钮引用
            for (Component component : standardPanel.getComponents()) {
                if (component instanceof JButton) {
                    JButton button = (JButton) component;
                    String text = button.getText().toLowerCase();
                    if (text.contains("sin") || text.contains("arcsin")) {
                        sinButton = button;
                    } else if (text.contains("cos") || text.contains("arccos")) {
                        cosButton = button;
                    } else if (text.contains("tan") || text.contains("arctan")) {
                        tanButton = button;
                    }
                }
            }
        }
        
        // 更新角度模式按钮引用
        if (isScientificMode) {
            for (Component component : scientificPanel.getComponents()) {
                if (component instanceof JButton) {
                    JButton button = (JButton) component;
                    String text = button.getText().toLowerCase();
                    if (text.equals("rad") || text.equals("deg")) {
                        degButtonScientific = button;
                    }
                }
            }
        } else {
            for (Component component : standardPanel.getComponents()) {
                if (component instanceof JButton) {
                    JButton button = (JButton) component;
                    String text = button.getText().toLowerCase();
                    if (text.equals("rad") || text.equals("deg")) {
                        degButtonStandard = button;
                    }
                }
            }
        }
        
        // 根据当前的2nd模式状态更新按钮显示
        updateButtonsForSecondMode();
        
        // 根据当前的角度模式更新按钮显示
        updateAngleModeDisplay();
    }
    
    /**
     * 根据当前的2nd模式状态更新按钮显示
     */
    private void updateButtonsForSecondMode() {
        Color highlightColor = new Color(255, 165, 0); // 橙色高亮
        
        if (isSecondMode) {
            // 设置为反三角函数并高亮
            if (sinButton != null) {
                sinButton.setText("arcsin");
                sinButton.setBackground(highlightColor);
            }
            if (cosButton != null) {
                cosButton.setText("arccos");
                cosButton.setBackground(highlightColor);
            }
            if (tanButton != null) {
                tanButton.setText("arctan");
                tanButton.setBackground(highlightColor);
            }
            
            // 高亮2nd按钮
            if (secondButtonStandard != null) {
                secondButtonStandard.setBackground(highlightColor);
            }
            if (secondButtonScientific != null) {
                secondButtonScientific.setBackground(highlightColor);
            }
        } else {
            // 恢复正常文本和颜色
            if (sinButton != null) {
                sinButton.setText("sin");
                sinButton.setBackground(ColorScheme.FUNCTION_BUTTON_BACKGROUND);
            }
            if (cosButton != null) {
                cosButton.setText("cos");
                cosButton.setBackground(ColorScheme.FUNCTION_BUTTON_BACKGROUND);
            }
            if (tanButton != null) {
                tanButton.setText("tan");
                tanButton.setBackground(ColorScheme.FUNCTION_BUTTON_BACKGROUND);
            }
            
            // 取消2nd按钮高亮
            if (secondButtonStandard != null) {
                secondButtonStandard.setBackground(ColorScheme.FUNCTION_BUTTON_BACKGROUND);
            }
            if (secondButtonScientific != null) {
                secondButtonScientific.setBackground(ColorScheme.FUNCTION_BUTTON_BACKGROUND);
            }
        }
    }
    
    /**
     * 切换2nd功能模式
     */
    private void toggleSecondMode() {
        isSecondMode = !isSecondMode;
        updateButtonsForSecondMode();
    }
    
    /**
     * 切换角度制/弧度制
     */
    private void toggleAngleMode() {
        isRadianMode = !isRadianMode;
        updateAngleModeDisplay();
    }
    
    /**
     * 根据当前的角度模式更新按钮显示
     */
    private void updateAngleModeDisplay() {
        Color highlightColor = new Color(255, 165, 0); // 橙色高亮
        
        // 更新标准面板的deg按钮
        if (degButtonStandard != null) {
            degButtonStandard.setText(isRadianMode ? "rad" : "deg");
            if (!isRadianMode) {
                degButtonStandard.setBackground(highlightColor);
            } else {
                degButtonStandard.setBackground(ColorScheme.FUNCTION_BUTTON_BACKGROUND);
            }
        }
        
        // 更新科学面板的deg按钮
        if (degButtonScientific != null) {
            degButtonScientific.setText(isRadianMode ? "rad" : "deg");
            if (!isRadianMode) {
                degButtonScientific.setBackground(highlightColor);
            } else {
                degButtonScientific.setBackground(ColorScheme.FUNCTION_BUTTON_BACKGROUND);
            }
        }
    }
    
    /**
     * 切换计算器模式
     */
    public void toggleMode() {
        isScientificMode = !isScientificMode;
        
        CardLayout cl = (CardLayout)(standardPanel.getParent().getLayout());
        cl.show(standardPanel.getParent(), isScientificMode ? "scientific" : "standard");
        
        // 调整窗口尺寸
        setSize(320, 580);
        
        // 更新当前活动的按钮引用
        updateActiveButtonReferences();
    }
    
    /**
     * 处理三角函数按钮点击，根据2nd模式决定是计算三角函数还是反三角函数
     */
    private void trigFunctionClick(String type) {
        if (isSecondMode) {
            controller.calculateInverseTrigFunction(type, isRadianMode);
            // 点击后自动关闭2nd模式
            toggleSecondMode();
        } else {
            controller.calculateTrigFunction(type, isRadianMode);
        }
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
                    // 添加百分号支持
                    case KeyEvent.VK_SHIFT:
                        // 检测是否同时按下了5键（%符号）
                        if (e.isShiftDown() && e.getKeyCode() == KeyEvent.VK_5) {
                            controller.calculatePercent();
                        }
                        break;
                    // 添加脱字符支持（^，用于幂运算）
                    case KeyEvent.VK_CIRCUMFLEX:
                        controller.setOperation(CalculatorOperation.POWER);
                        break;
                    // 添加科学计数法支持（E）
                    case KeyEvent.VK_E:
                        if (e.isControlDown()) {
                            controller.addScientificNotation();
                        } else {
                            controller.useE();
                        }
                        break;
                    // 添加绝对值支持（|）
                    case KeyEvent.VK_BACK_SLASH:
                        if (e.isShiftDown()) { // | 符号（Shift+\）
                            controller.calculateAbsoluteValue();
                        }
                        break;
                }
                
                // 检查是否按下了特定字符
                char keyChar = e.getKeyChar();
                if (keyChar == '%') {
                    // 根据上下文判断是百分比还是取余
                    if (e.isShiftDown()) {
                        controller.calculateModulo();
                    } else {
                        controller.calculatePercent();
                    }
                } else if (keyChar == '^') {
                    controller.setOperation(CalculatorOperation.POWER);
                } else if (keyChar == '|') {
                    controller.calculateAbsoluteValue();
                } else if (keyChar == 'e' || keyChar == 'E') {
                    // 通常单个e表示自然对数底
                    if (e.isControlDown()) {
                        controller.addScientificNotation();
                    }
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
     * 函数接口，用于定义按钮动作
     */
    @FunctionalInterface
    interface Action {
        void actionPerformed(ActionEvent e);
    }
} 