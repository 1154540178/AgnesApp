# AgnesApp

一个基于 Agnes AI API 的安卓客户端，支持 AI 对话、图片生成和视频生成。

## 功能

- **AI 对话**：与 Agnes AI 模型进行文字对话
- **图片生成**：通过文字描述生成高质量图片
- **视频生成**：通过文字描述生成短视频

## 技术栈

- **语言**：Kotlin
- **架构**：MVVM + Repository 模式
- **网络**：Retrofit + OkHttp
- **图片加载**：Glide
- **UI**：Material Design 3
- **异步**：Kotlin Coroutines

## API 配置

Agnes AI API 端点：
- 对话：`/v1/chat/completions`
- 图片：`/v1/images/generations`
- 视频：`/v1/videos/generations`

默认 API 地址：`https://api.agnes-ai.com/v1/`

## 构建

需要 Android Studio Arctic Fox 或更高版本。

1. 用 Android Studio 打开项目
2. 等待 Gradle 同步完成
3. 连接设备或启动模拟器
4. 运行 `app` 模块

## 目录结构

```
AgnesApp/
├── app/
│   ├── src/main/
│   │   ├── java/com/agnes/app/
│   │   │   ├── ui/              # UI 组件 (Activity/Fragment/Adapter)
│   │   │   ├── api/             # API 客户端和 Repository
│   │   │   ├── model/           # 数据模型
│   │   │   └── util/            # 工具类
│   │   └── res/                 # 资源文件
│   └── build.gradle             # 模块级构建配置
├── build.gradle                 # 项目级构建配置
└── settings.gradle              # 项目设置
```

## 依赖

- AndroidX Core KTX
- Material Design 3
- Retrofit + Gson Converter
- OkHttp + Logging Interceptor
- Glide (图片加载)
- Kotlin Coroutines
