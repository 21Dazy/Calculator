# Windows 11 风格计算器

基于Java Swing实现的仿Windows 11风格计算器应用程序，具有现代化UI设计和完整的计算功能。

## 功能特点

### 基本功能
- 加、减、乘、除四则运算
- 百分比计算
- 正负号切换
- 小数点处理
- 退格和清除(CE/C)功能

### 高级功能
- 平方、开方、倒数计算
- 内存功能(MC, MR, M+, M-)
- 计算历史记录
- 实时表达式显示
- 键盘操作支持

## 界面特点
- Windows 11 Fluent Design风格
- 深色主题
- 圆角按钮和面板
- 平滑动画效果

## 技术实现
- 采用MVC设计模式
- Java Swing界面库
- FlatLaf主题增强
- 自定义圆角组件

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
    │   │       ├── model       # 模型层
    │   │       ├── view        # 视图层
    │   │       ├── utils       # 工具类
    │   │       └── App.java    # 应用程序入口
    │   └── resources
    │       └── calculator_icon.png  # 应用图标
    └── test
        └── java
            └── com.win11calc  # 单元测试
```

## 开发环境
- JDK 11+
- Maven 3.6+ 