# 简单的GUI计算器

基于Java Swing实现的现代化计算器应用程序，支持多种主题风格UI，具有完整的计算功能及主题切换功能。

## 功能特点

### 基本功能
- 加、减、乘、除四则运算
- 百分比计算
- 正负号切换
- 小数点处理
- 退格和清除(AC)功能
- 复制和粘贴功能，支持与其他应用程序交互

### 科学计算功能
- 标准计算器和科学计算器模式切换
- 三角函数(sin、cos、tan)及反三角函数(arcsin、arccos、arctan)
- 平方、开方、倒数计算
- 绝对值、模运算、阶乘计算
- 弧度制/角度制切换
- 科学记数法支持
- 含括号的复杂表达式计算

### 进阶功能
- 内置常数π和e
- 完整的括号支持
- 实时表达式显示
- 键盘操作支持
- 自动调整字体大小，适应长表达式显示

## 界面特点
- 支持三种界面风格：Windows风格、macOS风格、小米风格
- 主题设置自动保存和加载，下次启动时保持用户偏好
- 自适应UI设计，显示所有功能按钮
- 深色主题，减轻视觉疲劳
- 圆角按钮和面板，符合现代UI设计风格
- 2nd功能模式，高亮显示当前激活的功能

### UI风格特点
- **Windows风格**：小圆角按钮(4px)、深色背景、Segoe UI字体、蓝色按钮和黄色高亮
- **macOS风格**：中等圆角按钮(20px)、浅色背景、SF Pro Display字体、蓝色按钮和橙色高亮
- **小米风格**：大圆角按钮(24px)、纯黑背景、Arial字体、橙色按钮和高亮

## 技术实现
- 采用MVC设计模式，实现关注点分离
- Java Swing界面库，自定义UI组件
- 中缀表达式转后缀表达式算法（Shunting Yard算法）
- 自定义圆角组件，模拟现代UI风格
- 完善的主题管理系统和配置持久化
- UI配置参数集中管理

## 构建与运行

### 系统要求
- JDK 17 或更高版本
- Maven 3.6+ (如需构建)

### 构建项目
```bash
mvn clean package
```

### 运行应用
```bash
java -jar target/Win11Calculator-1.0-SNAPSHOT-jar-with-dependencies.jar
```

### 开发环境设置
1. 克隆仓库到本地
2. 使用支持Maven的IDE导入项目（如IntelliJ IDEA、Eclipse或VSCode）
3. 确保JDK设置正确（要求JDK 17+）
4. 运行`App.java`的main方法启动应用

## 项目结构
```
└── src
    ├── main
    │   ├── java
    │   │   └── com.win11calc
    │   │       ├── controller  # 控制器层
    │   │       │   └── CalculatorController.java  # 处理计算逻辑和视图更新
    │   │       ├── model       # 模型层
    │   │       │   ├── CalculatorModel.java       # 数据模型和计算实现
    │   │       │   └── CalculatorOperation.java   # 运算符枚举
    │   │       ├── view        # 视图层
    │   │       │   └── CalculatorFrame.java       # 主界面实现
    │   │       ├── utils       # 工具类
    │   │       │   ├── ColorScheme.java          # 颜色方案管理
    │   │       │   ├── RoundedButton.java        # 圆角按钮组件
    │   │       │   ├── RoundedPanel.java         # 圆角面板组件
    │   │       │   ├── ThemeManager.java         # 主题管理系统
    │   │       │   └── UIConfig.java             # UI全局配置
    │   │       └── App.java    # 应用程序入口
    │   └── resources
    │       └── calculator_icon.png  # 应用图标
    └── test
        └── java
            └── com.win11calc  # 单元测试
```

## 特色功能使用指南

### 主题风格切换
1. 点击菜单栏 `设置 > 界面风格`
2. 选择需要的风格:
   - `小米风格`: 黑色背景，橙色按钮，大圆角设计
   - `macOS风格`: 浅灰色背景，半透明按钮，中等圆角
   - `Windows风格`: 深灰色背景，蓝色按钮，小圆角设计
3. 界面会立即切换，无需重启应用
4. 主题设置会自动保存，下次启动应用时会恢复上次选择

### 计算器模式切换
1. 点击菜单栏 `模式` 选择:
   - `标准计算器`: 显示基本算术功能
   - `科学计算器`: 显示更多高级数学功能
2. 或使用快捷键切换模式

### 2nd功能模式
1. 点击 `2nd` 按钮激活反三角函数模式
2. sin、cos、tan 按钮将变为 arcsin、arccos、arctan
3. 按钮会高亮显示，表示当前处于2nd模式
4. 使用后自动切回标准模式

### 弧度/角度切换
1. 点击 `rad` 或 `deg` 按钮切换弧度制和角度制
2. 角度制模式下按钮将显示 `deg` 并高亮显示
3. 弧度制模式下按钮将显示 `rad`

### 复制粘贴功能
1. 使用菜单 `编辑 > 复制` 或快捷键 `Ctrl+C` 复制当前表达式或结果
2. 使用菜单 `编辑 > 粘贴` 或快捷键 `Ctrl+V` 粘贴表达式

## 错误处理
- 无效输入会在结果区域显示错误提示
- 除零错误、括号不匹配等会有明确提示
- 应用会自动恢复并允许用户继续输入

## 许可证
该项目采用MIT许可证 - 详情请参阅 [LICENSE](LICENSE) 文件 
