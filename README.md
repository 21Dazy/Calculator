# Windows 11 风格计算器

基于Java Swing实现的现代化计算器应用程序，支持Windows风格和macOS风格UI，具有完整的计算功能。

## 功能特点

### 基本功能
- 加、减、乘、除四则运算
- 百分比计算
- 正负号切换
- 小数点处理
- 退格和清除(AC)功能

### 科学计算功能
- 标准计算器和科学计算器模式切换
- 三角函数(sin、cos、tan)及反三角函数(arcsin、arccos、arctan)
- 平方、开方、倒数计算
- 绝对值、模运算、阶乘计算
- 弧度制/角度制切换
- 科学记数法支持

### 进阶功能
- 内置常数π和e
- 完整的括号支持
- 实时表达式显示
- 键盘操作支持

## 界面特点
- 支持Windows和macOS界面风格切换
- 适应性UI设计，显示所有功能按钮
- 深浅色主题
- 圆角按钮和面板
- 2nd功能模式，高亮显示当前激活的功能

### UI风格特点
- **Windows风格**：略微圆角按钮(4px)、深色背景、Segoe UI字体、蓝色和黄色高亮
- **macOS风格**：圆形按钮(20px)、浅色背景、SF Pro Display字体、蓝色和橙色高亮

## 技术实现
- 采用MVC设计模式
- Java Swing界面库
- 自定义圆角组件
- 主题管理和配置持久化
- UI配置参数集中管理

## 构建与运行

### 构建项目
```bash
mvn clean package
```

### 运行应用
```bash
java -jar target/Win11Calculator-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## 项目结构
```
└── src
    ├── main
    │   ├── java
    │   │   └── com.win11calc
    │   │       ├── controller  # 控制器层
    │   │       │   └── CalculatorController.java  # 处理计算逻辑
    │   │       ├── model       # 模型层
    │   │       │   └── CalculatorModel.java       # 数据模型和计算实现
    │   │       ├── view        # 视图层
    │   │       │   └── CalculatorFrame.java       # 主界面实现
    │   │       ├── utils       # 工具类
    │   │       │   ├── ColorScheme.java          # 颜色方案
    │   │       │   ├── RoundedButton.java        # 圆角按钮组件
    │   │       │   ├── RoundedPanel.java         # 圆角面板组件
    │   │       │   ├── ThemeManager.java         # 主题管理
    │   │       │   └── UIConfig.java             # UI配置
    │   │       └── App.java    # 应用程序入口
    │   └── resources
    │       └── calculator_icon.png  # 应用图标
    └── test
        └── java
            └── com.win11calc  # 单元测试
```

## 特色功能使用指南

### 风格切换
1. 点击菜单栏 `设置 > 界面风格`
2. 选择 `macOS风格` 或 `Windows风格`
3. 界面会立即切换，无需重启应用

### 2nd功能模式
1. 点击 `2nd` 按钮激活反三角函数模式
2. sin、cos、tan 按钮将变为 arcsin、arccos、arctan
3. 按钮会高亮显示，表示当前处于2nd模式
4. 使用后自动切回标准模式

### 弧度/角度切换
1. 点击 `rad` 或 `deg` 按钮切换弧度制和角度制
2. 角度制模式下按钮将显示 `deg` 并高亮显示

## 开发环境
- JDK 11+
- Maven 3.6+ 