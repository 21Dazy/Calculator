# 计算器项目报告文档

## 一、开发工具与平台

- **Java版本**：open-jdk17
- **项目构建工具**：Maven 3.9.9
- **开发编辑器**：VSCode
- **开发系统**：Windows 11

## 二、设计思路

### 1、界面设计

计算器界面采用现代化设计，以Windows风格的暗色主题为默认样式，具有以下特点：

- **暗色主题**：采用深色背景配合蓝色和灰色按钮的高对比度设计，减轻眼睛疲劳，适合各种光线环境使用
- **简约风格**：极简的界面设计，去除多余装饰，突出核心功能
- **蓝色强调色**：使用蓝色作为等号和运算符的强调色，提高视觉识别度
- **圆角元素**：所有按钮和面板采用圆角设计，符合现代UI审美，提升视觉体验
- **双模式切换**：支持标准计算器和科学计算器两种模式，用户可以通过菜单或快捷键切换
- **自适应布局**：采用Java Swing的布局管理器，确保在不同屏幕分辨率下的合理显示
- **双显示区**：顶部为表达式编辑区，支持光标定位；底部为结果显示区，字体较大
- **自动字体调整**：根据显示内容长度自动调整字体大小，确保结果始终可见

#### 1.1 组件选择与布局设计

项目使用Java Swing构建用户界面，采用了以下主要组件和布局管理器：

1. **JFrame**：
   - 用途：作为应用程序的主窗口容器
   - 选择理由：提供了完整的窗口功能，包括标题栏、菜单、最小化/最大化按钮等
   - 配置：设置固定初始大小(380×650)，设置最小尺寸限制，确保界面不会因窗口缩小而变形

2. **布局管理器**：
   - **BorderLayout**：用于JFrame的主布局
     - 用途：将窗口分为五个区域（北/南/东/西/中）
     - 选择理由：适合计算器这种需要固定顶部显示区域和底部按钮区域的应用
     - 配置：将显示面板放在NORTH位置，按钮面板放在CENTER位置
   
   - **BoxLayout**：用于显示面板内部布局
     - 用途：垂直排列表达式标签和结果标签
     - 选择理由：能够简单地实现垂直堆叠元素，同时支持右对齐
     - 配置：设置为Y_AXIS方向，并使用`Component.RIGHT_ALIGNMENT`使文本右对齐
   
   - **GridLayout**：用于按钮面板布局
     - 用途：将按钮排列为网格状
     - 选择理由：计算器按钮天然适合网格排列，GridLayout提供了均匀的分布
     - 配置：标准面板使用7×5网格，科学面板使用8×5网格，设置统一间距(2px)
   
   - **CardLayout**：用于模式切换
     - 用途：在标准模式和科学模式之间切换
     - 选择理由：提供了在同一位置显示不同面板的能力，适合实现模式切换
     - 配置：通过`show()`方法实现两种计算器模式的无缝切换

3. **定制组件**：
   - **RoundedButton**：继承自JButton的自定义按钮类
     - 用途：实现带圆角效果的按钮
     - 选择理由：Java Swing默认按钮没有圆角效果，不符合现代UI设计要求
     - 实现方式：重写`paintComponent`方法，使用`Graphics2D`绘制圆角矩形
   
   - **RoundedPanel**：继承自JPanel的自定义面板类
     - 用途：实现带圆角效果的面板
     - 选择理由：统一UI风格，使面板与按钮样式一致
     - 实现方式：类似RoundedButton，通过绘制圆角矩形实现

4. **显示组件**：
   - **JLabel**：用于显示表达式和结果
     - 用途：显示计算表达式和计算结果
     - 选择理由：轻量级组件，适合显示只读内容，支持HTML和动态字体调整
     - 配置：表达式标签使用较小字体(18pt)，结果标签使用较大字体(60pt)，两者均右对齐

5. **菜单组件**：
   - **JMenuBar**、**JMenu**、**JMenuItem**：
     - 用途：实现顶部菜单栏，提供模式切换、编辑和帮助功能
     - 选择理由：符合桌面应用程序的标准界面规范，提供标准化的交互方式
     - 配置：包含模式菜单、编辑菜单和帮助菜单，支持键盘快捷键

6. **交互与事件处理**：
   - **ActionListener**：处理按钮点击事件
     - 实现方式：使用Lambda表达式简化事件处理代码
   - **KeyAdapter**：处理键盘输入
     - 用途：支持键盘操作计算器功能
     - 配置：映射数字键、运算符键和功能键到对应的计算器操作

7. **容器组合**：
   - 使用Box.createVerticalGlue()和Box.createVerticalStrut()在显示面板中创建弹性和固定间距
   - 使用BorderFactory.createEmptyBorder()为各面板创建内边距，提升视觉舒适度

8. **系统集成**：
   - 使用Toolkit.getDefaultToolkit().getSystemClipboard()实现与系统剪贴板的交互
   - 使用SwingUtilities.updateComponentTreeUI()刷新UI外观

#### 1.2 布局设计理念

计算器界面布局遵循以下设计理念：

1. **分层组织**：
   - 顶部显示区：占据窗口上部约1/4空间，包含表达式和结果标签
   - 按钮区：占据窗口下部约3/4空间，根据当前模式显示不同的按钮集

2. **功能分组**：
   - 功能键（AC、CE等）位于上方
   - 数字键位于中央区域，采用传统计算器的"电话键盘"布局
   - 运算符键位于右侧
   - 特殊函数键（三角函数、对数等）排列在科学模式的扩展区域

3. **视觉层次**：
   - 通过颜色区分不同类型按钮：数字键使用深灰色，运算符使用蓝色，等号按钮使用突出的蓝色
   - 结果显示区使用较大字体，表达式区使用较小字体，建立清晰的视觉层次

4. **一致性原则**：
   - 所有按钮大小一致
   - 按钮间距统一
   - 标准模式和科学模式保持视觉一致性，确保模式切换不会造成用户困惑

5. **响应式设计**：
   - 使用百分比和相对尺寸而非固定像素值
   - 实现字体大小自动调整，根据内容长度缩放字体
   - 保留窗口调整能力，同时设置最小尺寸限制防止UI变形

6. **交互优化**：
   - 按钮提供视觉反馈（按下状态）
   - 2nd模式和角度/弧度模式通过按钮高亮提供明确的状态指示
   - 所有功能支持键盘快捷键，提升操作效率
   - 菜单提供可发现性，帮助用户找到不直观的功能

#### 1.3 主题管理系统

计算器应用实现了一个完整的主题管理系统，支持多种视觉风格的动态切换，极大提升了用户体验的可定制性。

##### 1.3.1 主题设计与实现

主题管理系统的核心是`ThemeManager`类，它负责主题的定义、应用和持久化：

```java
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
    private static Theme currentTheme = Theme.XIAOMI;
    
    // 配置文件路径
    private static final String CONFIG_FILE = "calculator_config.properties";
    
    // 主题应用方法
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
    
    // 其他方法...
}
```

主题系统支持三种视觉风格，每种风格都有独特的设计语言和色彩方案：

1. **小米风格**：
   - 特点：黑色背景，橙色操作按钮，大圆角设计
   - 视觉风格：受小米MIUI启发，简约而现代
   - 配色：深黑背景(#000000)，橙色强调(#FF8C00)，白色文字(#FFFFFF)

2. **macOS风格**：
   - 特点：浅灰色背景，半透明按钮，中等圆角
   - 视觉风格：模仿macOS的精致风格，光亮且优雅
   - 配色：浅灰背景(#F2F2F7)，蓝色强调(#007AFF)，黑色文字(#000000)

3. **Windows风格**：
   - 特点：深灰色背景，蓝色操作按钮，小圆角
   - 视觉风格：遵循Windows现代UI设计，简洁而实用
   - 配色：深灰背景(#202020)，蓝色强调(#0078D7)，白色文字(#FFFFFF)

##### 1.3.2 主题切换机制

主题切换实现通过以下步骤完成：

1. **用户界面集成**：
   在`CalculatorFrame`类的菜单中添加主题切换选项：

   ```java
   // 设置菜单项
   JMenu uiStyleMenu = new JMenu("界面风格");
   
   // 界面风格子菜单
   JMenuItem xiaomiStyleItem = new JMenuItem("小米风格");
   JMenuItem macStyleItem = new JMenuItem("macOS风格");
   JMenuItem winStyleItem = new JMenuItem("Windows风格");
   
   // 添加界面风格切换事件监听
   xiaomiStyleItem.addActionListener(e -> {
       ThemeManager.applyTheme(ThemeManager.Theme.XIAOMI);
       updateUIStyle();
   });
   
   macStyleItem.addActionListener(e -> {
       ThemeManager.applyTheme(ThemeManager.Theme.MACOS);
       updateUIStyle();
   });
   
   winStyleItem.addActionListener(e -> {
       ThemeManager.applyTheme(ThemeManager.Theme.WINDOWS);
       updateUIStyle();
   });
   ```

2. **主题应用过程**：
   当用户选择主题时，系统执行以下步骤：
   - 调用`ThemeManager.applyTheme(theme)`方法更新全局主题设置
   - 根据选择的主题，应用相应的颜色方案和UI参数
   - 调用`updateUIStyle()`方法刷新整个界面

3. **界面更新实现**：
   `updateUIStyle()`方法负责将主题应用到所有UI组件：

   ```java
   private void updateUIStyle() {
       // 更新所有面板背景色
       getContentPane().setBackground(ColorScheme.BACKGROUND);
       if (displayPanel != null) {
           displayPanel.setBackground(ColorScheme.DISPLAY_BACKGROUND);
       }
       
       // 更新标签颜色
       if (expressionLabel != null) {
           expressionLabel.setForeground(ColorScheme.EXPRESSION_TEXT);
       }
       if (resultLabel != null) {
           resultLabel.setForeground(ColorScheme.DISPLAY_TEXT);
       }
       
       // 更新按钮颜色和字体
       updateButtons();
       
       // 更新字体
       updateFonts(this);
       
       // 更新显示面板
       updateDisplayPanel();
       
       // 刷新所有组件
       SwingUtilities.updateComponentTreeUI(this);
       this.repaint();
   }
   ```

##### 1.3.3 主题持久化

为确保用户的主题偏好在应用重启后仍然保持，系统实现了主题设置的持久化：

1. **设置存储**：
   ```java
   public static void saveSettings() {
       Properties props = new Properties();
       props.setProperty("theme", currentTheme.name());
       
       try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
           props.store(fos, "Calculator Theme Settings");
       } catch (IOException e) {
           System.err.println("保存配置文件失败: " + e.getMessage());
       }
   }
   ```

2. **设置加载**：
   ```java
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
   ```

3. **启动时加载**：
   在`CalculatorFrame`构造函数中，添加对设置的加载：
   ```java
   public CalculatorFrame() {
       // ...
       
       // 加载主题设置
       ThemeManager.loadSettings();
       
       // ...
   }
   ```

##### 1.3.4 动态UI刷新

主题切换后，需要刷新所有UI组件以应用新的视觉风格：

```java
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
```

通过递归遍历所有子组件，确保整个组件树都应用了新的主题设置。

### 2、逻辑设计（课题中的难点的解决方案）

#### 2.1 MVC架构设计

项目采用标准MVC（Model-View-Controller）架构，实现了关注点分离：

- **Model（模型层）**：`CalculatorModel`类负责所有计算逻辑和数据存储
  - 管理计算器的数据状态（表达式、结果、错误状态）
  - 实现所有数学计算功能
  - 提供表达式解析和计算的核心算法

- **View（视图层）**：`CalculatorFrame`类负责用户界面的显示和交互
  - 处理用户界面的渲染和布局
  - 捕获用户输入事件（按钮点击、键盘按键）
  - 将用户操作转发给控制器处理

- **Controller（控制层）**：`CalculatorController`类连接模型和视图，协调二者的数据交换
  - 接收来自视图的用户操作
  - 调用模型的相应方法处理数据
  - 通知视图更新界面显示

这种架构使各部分职责明确，便于维护和扩展。视图层不直接访问模型，确保了视图和模型的解耦，使得界面改变不会影响底层逻辑，反之亦然。数据流的清晰划分也便于调试和测试各个组件。

#### 2.2 表达式解析与计算

表达式计算采用了经典的中缀表达式转后缀表达式（逆波兰式）再计算的方法，实现了对复杂数学表达式的正确解析和计算。整个过程分为以下几个关键步骤：

##### 2.2.1 词法分析与标记化（Tokenization）

在`CalculatorModel`类中，`tokenizeExpression`方法负责将输入的字符串表达式分解为一系列标记（tokens）：

```java
private List<String> tokenizeExpression(String expression) {
    List<String> tokens = new ArrayList<>();
    StringBuilder numBuilder = new StringBuilder();
    boolean numberStarted = false;
    
    for (int i = 0; i < expression.length(); i++) {
        char c = expression.charAt(i);
        
        // 处理数字、小数点
        if (Character.isDigit(c) || c == '.') {
            numBuilder.append(c);
            numberStarted = true;
            continue;
        }
        
        // 处理科学计数法表示（如1.23E+5）
        if ((c == 'e' || c == 'E') && numberStarted) {
            // 检查是否为科学计数法格式
            if (i + 1 < expression.length() && 
                (Character.isDigit(expression.charAt(i + 1)) || 
                 expression.charAt(i + 1) == '+' || 
                 expression.charAt(i + 1) == '-')) {
                
                numBuilder.append(c); // 添加e或E字符
                
                // 处理指数部分
                // ...省略细节处理...
                
                continue;
            }
        }
        
        // 处理负号（区分负数符号和减法运算符）
        if (c == '-') {
            // 如果负号前面是操作符或左括号或是表达式的开始，视为负号
            if (i == 0 || expression.charAt(i - 1) == '(' || 
                "+-×÷%^".indexOf(expression.charAt(i - 1)) >= 0) {
                numBuilder.append(c);
                numberStarted = true;
                continue;
            }
        }
        
        // 处理已构建的数字
        if (numberStarted) {
            tokens.add(numBuilder.toString());
            numBuilder.setLength(0);
            numberStarted = false;
        }
        
        // 处理操作符和括号
        if (c == '+' || c == '-' || c == '×' || c == '÷' || 
            c == '(' || c == ')' || c == '%' || c == '^') {
            tokens.add(String.valueOf(c));
        }
    }
    
    // 添加最后一个数字（如果有）
    if (numberStarted) {
        tokens.add(numBuilder.toString());
    }
    
    return tokens;
}
```

这个方法解决了几个关键难点：
- **科学计数法识别**：正确识别如`1.23E+10`格式的数字
- **负数与减法符号的区分**：通过上下文分析区分负号和减法操作符
- **数字与操作符的分离**：确保`3+4`被正确解析为`["3", "+", "4"]`

##### 2.2.2 中缀表达式转后缀表达式（Shunting Yard算法）

在解析完标记后，使用Shunting Yard算法将中缀表达式转换为后缀表达式：

```java
private List<String> convertToRPN(List<String> tokens) {
    List<String> output = new ArrayList<>();
    Stack<String> operatorStack = new Stack<>();
    
    for (String token : tokens) {
        // 数字直接加入输出
        if (!isOperator(token) && !token.equals("(") && !token.equals(")")) {
            output.add(token);
        }
        // 左括号压入栈
        else if (token.equals("(")) {
            operatorStack.push(token);
        }
        // 右括号处理
        else if (token.equals(")")) {
            // 弹出所有操作符直到遇到左括号
            while (!operatorStack.isEmpty() && !operatorStack.peek().equals("(")) {
                output.add(operatorStack.pop());
            }
            // 弹出左括号（不加入输出）
            if (!operatorStack.isEmpty() && operatorStack.peek().equals("(")) {
                operatorStack.pop();
            } else {
                throw new RuntimeException("括号不匹配");
            }
        }
        // 操作符处理
        else if (isOperator(token)) {
            // 弹出优先级更高或相等的操作符
            while (!operatorStack.isEmpty() && 
                  isOperator(operatorStack.peek()) && 
                  getPrecedence(operatorStack.peek()) >= getPrecedence(token)) {
                output.add(operatorStack.pop());
            }
            // 当前操作符入栈
            operatorStack.push(token);
        }
    }
    
    // 处理栈中剩余的操作符
    while (!operatorStack.isEmpty()) {
        if (operatorStack.peek().equals("(")) {
            throw new RuntimeException("括号不匹配");
        }
        output.add(operatorStack.pop());
    }
    
    return output;
}
```

算法的核心难点：
- **操作符优先级处理**：通过`getPrecedence`方法确定不同操作符的优先级
- **括号嵌套处理**：正确处理括号嵌套，确保表达式如`(2+(3*4))`被正确转换
- **错误检测**：识别括号不匹配等常见错误

##### 2.2.3 后缀表达式求值

后缀表达式的计算采用栈实现：

```java
private double evaluateRPN(List<String> rpnTokens) {
    Stack<Double> stack = new Stack<>();
    
    for (String token : rpnTokens) {
        if (isOperator(token)) {
            // 确保栈中有足够的操作数
            if (stack.size() < 2) {
                throw new RuntimeException("表达式错误");
            }
            
            // 弹出两个操作数
            double b = stack.pop();
            double a = stack.pop();
            
            // 根据操作符执行相应运算
            switch (token) {
                case "+": stack.push(a + b); break;
                case "-": stack.push(a - b); break;
                case "×": stack.push(a * b); break;
                case "÷":
                    if (b == 0) {
                        throw new RuntimeException("除数不能为零");
                    }
                    stack.push(a / b);
                    break;
                case "%": stack.push(a % b); break;
                case "^": stack.push(Math.pow(a, b)); break;
            }
        } else {
            // 操作数直接入栈
            stack.push(Double.parseDouble(token));
        }
    }
    
    // 计算完成后，栈中应该只剩一个元素，即结果
    if (stack.size() != 1) {
        throw new RuntimeException("表达式错误");
    }
    
    return stack.pop();
}
```

该方法的技术难点：
- **错误处理**：检测除零错误和表达式格式错误
- **操作顺序确保**：确保操作数的弹出顺序与运算符要求一致

##### 2.2.4 特殊函数处理

为支持高级计算功能，项目实现了多种特殊函数处理：

```java
private String processFunctions(String expression) {
    String[] functionNames = {"sin", "cos", "tan", "log", "ln", "sqrt", "arcsin", "arccos", "arctan"};
    
    for (String funcName : functionNames) {
        int pos = 0;
        while (pos < expression.length()) {
            int funcStart = expression.indexOf(funcName, pos);
            if (funcStart == -1) break;
            
            // 确认是函数名（前面不是字母或数字）
            if (funcStart > 0 && (Character.isLetterOrDigit(expression.charAt(funcStart - 1)))) {
                pos = funcStart + 1;
                continue;
            }
            
            // 找到左括号位置
            int openParen = expression.indexOf("(", funcStart + funcName.length());
            if (openParen != -1) {
                // 查找匹配的右括号
                int parenCount = 1;
                int parenEnd = -1;
                for (int i = openParen + 1; i < expression.length(); i++) {
                    if (expression.charAt(i) == '(') parenCount++;
                    else if (expression.charAt(i) == ')') {
                        parenCount--;
                        if (parenCount == 0) {
                            parenEnd = i;
                            break;
                        }
                    }
                }
                
                // 如果找到匹配的右括号，计算函数值
                if (parenEnd > openParen) {
                    String paramExp = expression.substring(openParen + 1, parenEnd);
                    double paramValue = evaluateExpression(paramExp);
                    double result;
                    
                    // 根据函数名执行相应计算
                    switch (funcName) {
                        case "sin": result = Math.sin(paramValue); break;
                        case "cos": result = Math.cos(paramValue); break;
                        case "tan": result = Math.tan(paramValue); break;
                        case "arcsin": result = Math.asin(paramValue); break;
                        case "arccos": result = Math.acos(paramValue); break;
                        case "arctan": result = Math.atan(paramValue); break;
                        case "log": result = Math.log10(paramValue); break;
                        case "ln": result = Math.log(paramValue); break;
                        case "sqrt": 
                            if (paramValue < 0) {
                                throw new RuntimeException("负数不能开平方根");
                            }
                            result = Math.sqrt(paramValue); 
                            break;
                        default: result = paramValue;
                    }
                    
                    // 替换函数调用为计算结果
                    String replacement = formatNumber(result);
                    expression = expression.substring(0, funcStart) + 
                                replacement + 
                                expression.substring(parenEnd + 1);
                    
                    // 调整位置指针
                    pos = funcStart + replacement.length();
                } else {
                    pos = funcStart + funcName.length() + 1;
                }
            } else {
                pos++;
            }
        }
    }
    
    return expression;
}
```

这个方法解决了递归解析函数调用的复杂问题，支持嵌套函数调用如`sin(cos(x))`的正确计算。

#### 2.3 复制粘贴功能实现

为了方便用户操作，计算器实现了复制粘贴功能，允许用户将表达式或结果复制到剪贴板，或从剪贴板粘贴内容到计算器中：

##### 2.3.1 复制功能实现

`CalculatorFrame`类中的`copyToClipboard`方法实现了复制功能：

```java
private void copyToClipboard() {
    try {
        String expression = expressionLabel.getText();
        String result = resultLabel.getText();
        
        // 检查是否有表达式和结果可复制
        if (expression.isEmpty() && "0".equals(result)) {
            return; // 没有内容可复制
        }
        
        // 创建复制内容
        String copyContent;
        if (expression.isEmpty()) {
            // 只有结果
            copyContent = result;
        } else {
            // 表达式和结果
            copyContent = expression + " = " + result;
        }
        
        // 创建StringSelection
        StringSelection selection = new StringSelection(copyContent);
        
        // 获取系统剪贴板
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        
        // 设置剪贴板内容
        clipboard.setContents(selection, null);
        
        // 提示用户
        JOptionPane.showMessageDialog(this, "已复制: " + copyContent, "复制成功", JOptionPane.INFORMATION_MESSAGE);
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "复制失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
    }
}
```

##### 2.3.2 粘贴功能实现

`CalculatorFrame`类中的`pasteFromClipboard`和`processPastedText`方法实现了粘贴功能：

```java
private void pasteFromClipboard() {
    try {
        // 获取系统剪贴板
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        
        // 检查剪贴板是否包含文本
        if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
            // 获取剪贴板内容
            String clipboardText = (String) clipboard.getData(DataFlavor.stringFlavor);
            
            // 如果内容不为空
            if (clipboardText != null && !clipboardText.isEmpty()) {
                // 处理粘贴内容
                processPastedText(clipboardText);
            }
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "粘贴失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
    }
}

private void processPastedText(String pastedText) {
    // 去除所有空格
    pastedText = pastedText.replaceAll("\\s+", "");
    
    // 检查内容是否包含"="
    if (pastedText.contains("=")) {
        // 如果包含等号，只取等号前的内容作为表达式
        String[] parts = pastedText.split("=");
        if (parts.length > 0) {
            // 设置表达式
            controller.setExpressionDirectly(parts[0]);
            // 显示提示
            JOptionPane.showMessageDialog(this, "已粘贴表达式: " + parts[0], "粘贴成功", JOptionPane.INFORMATION_MESSAGE);
        }
    } else {
        // 如果不包含等号，整个内容作为表达式
        controller.setExpressionDirectly(pastedText);
        // 显示提示
        JOptionPane.showMessageDialog(this, "已粘贴表达式: " + pastedText, "粘贴成功", JOptionPane.INFORMATION_MESSAGE);
    }
}
```

在控制器中，`setExpressionDirectly`方法用于处理粘贴的表达式：

```java
public void setExpressionDirectly(String expression) {
    // 首先清除当前状态
    model.clear();
    
    // 设置新的表达式到模型
    for (int i = 0; i < expression.length(); i++) {
        char c = expression.charAt(i);
        
        if (Character.isDigit(c)) {
            // 数字
            model.addDigit(String.valueOf(c));
        } else if (c == '.') {
            // 小数点
            model.addDecimalPoint();
        } else if (c == '+') {
            model.setOperation(CalculatorOperation.ADD);
        } else if (c == '-') {
            model.setOperation(CalculatorOperation.SUBTRACT);
        } else if (c == '*' || c == '×') {
            model.setOperation(CalculatorOperation.MULTIPLY);
        } else if (c == '/' || c == '÷') {
            model.setOperation(CalculatorOperation.DIVIDE);
        } else if (c == '(') {
            model.addLeftParenthesis();
        } else if (c == ')') {
            model.addRightParenthesis();
        } else if (c == '%') {
            model.calculatePercent();
        }
        // 忽略其他字符
    }
    
    // 更新视图，但不触发计算
    updateViewWithoutCalculation();
}
```

这种设计允许用户方便地在计算器和其他应用程序之间传递表达式和结果，增强了计算器的实用性。

#### 2.4 事件处理机制

计算器的事件处理机制通过按钮事件和键盘事件两种方式实现，确保用户可以通过多种方式与应用交互：

##### 2.4.1 按钮事件处理

通过Lambda表达式简化了事件处理代码：

```java
// 添加数字按钮
addButton(panel, "7", e -> controller.addDigit("7"), ColorScheme.NUMBER_BUTTON_BACKGROUND);

// 添加操作符按钮
addButton(panel, "+", e -> controller.setOperation(CalculatorOperation.ADD), 
          ColorScheme.OPERATION_BUTTON_BACKGROUND);

// 添加功能按钮
addButton(panel, "=", e -> controller.calculateResult(), 
          ColorScheme.EQUALS_BUTTON_BACKGROUND, ColorScheme.EQUALS_BUTTON_TEXT);
```

##### 2.4.2 键盘事件处理

通过`KeyAdapter`实现了键盘事件的处理：

```java
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
            // ... 其他数字键处理 ...
            
            case KeyEvent.VK_PLUS:
            case KeyEvent.VK_ADD:
                controller.setOperation(CalculatorOperation.ADD);
                break;
            // ... 其他操作符键处理 ...
            
            case KeyEvent.VK_ENTER:
            case KeyEvent.VK_EQUALS:
                controller.calculateResult();
                break;
            
            case KeyEvent.VK_BACK_SPACE:
                controller.backspace();
                break;
            
            // ... 其他功能键处理 ...
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
        }
        // ... 其他特殊字符处理 ...
    }
});
```

这种设计使计算器既可以通过鼠标点击按钮操作，也可以通过键盘进行高效输入，提高了用户体验。

## 三、实验总结

### 技术亮点

1. **MVC架构的灵活应用**：
   - 实现了视图和逻辑的有效分离
   - 便于功能扩展和界面定制
   - 通过Controller实现了Model和View的松耦合

2. **计算引擎的可扩展性**：
   - 支持自定义函数和运算符
   - 表达式解析和计算模块相互独立
   - 采用策略模式实现不同运算符的计算逻辑

3. **用户体验优化**：
   - 键盘快捷键支持提升操作速度
   - 支持复制粘贴功能，便于与其他应用交互
   - Windows风格的暗黑主题减轻视觉疲劳

4. **错误处理机制**：
   - 完善的异常捕获和处理
   - 友好的错误信息提示
   - 自动恢复机制防止程序崩溃

### 难点与解决方法

1. **表达式解析难点**：
   - 科学计数法的识别和处理
   - 负数与减法符号的区分
   - 括号嵌套的正确解析
   
   解决方法：使用有限状态机进行词法分析，确保正确识别各类标记

2. **实时计算与最终计算的平衡**：
   - 用户希望看到实时反馈
   - 但不希望表达式被自动替换
   
   解决方法：分离显示计算结果和修改表达式的操作，只在用户明确要求时才替换表达式

3. **UI一致性实现**：
   - Windows计算器风格的实现
   - 确保在Java Swing中实现接近原生应用的视觉效果
   
   解决方法：使用自定义Look and Feel，精确控制按钮、文本和背景的颜色和圆角效果

### 学习收获

1. 深入理解了MVC架构的应用实践
2. 掌握了复杂表达式解析和计算的算法
3. 提高了Java Swing界面设计与实现能力
4. 学会了处理用户交互中的各种边界情况
5. 培养了代码重构和性能优化的能力
6. 学习了事件驱动编程模型的设计模式应用

### 改进方向

1. **性能优化**：对大量操作和复杂表达式的计算进行优化
2. **界面美化**：添加动画效果，提升用户体验
3. **功能扩展**：支持更多科学计算功能，如微积分、矩阵运算等
4. **跨平台适配**：改进UI适配不同操作系统的外观风格
5. **无障碍设计**：添加屏幕阅读器支持和键盘导航增强
6. **单元测试**：增加更全面的单元测试覆盖，提高代码质量

本项目通过实现一个功能完善、界面友好的计算器应用，综合运用了Java编程、UI设计、算法实现等多方面的知识和技能，成功解决了表达式解析等关键难点，为未来开发更复杂的应用程序积累了宝贵经验。 
本项目的UI设计成功模仿了Windows计算器的经典暗色主题，在保留其简洁美观特点的同时，增加了更强大的计算功能。 