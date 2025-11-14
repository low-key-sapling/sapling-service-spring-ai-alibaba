# Implementation Plan

- [x] 1. 项目结构和依赖配置


  - 创建 sapling-spring-boot-starter-ai-alibaba 模块目录结构
  - 配置 pom.xml，添加 Spring AI Alibaba 和 DashScope SDK 依赖
  - 更新父 pom.xml，添加新模块和版本管理
  - 创建基础包结构（config、core、service、tool、exception、util）
  - _Requirements: 1.1, 1.2_

- [x] 2. 配置组件实现

- [x] 2.1 实现配置属性类



  - 创建 AlibabaAiProperties 配置类，定义所有配置项
  - 实现 ChatProperties、EmbeddingProperties、ImageProperties 内部类
  - 实现 ToolProperties、CacheProperties、MetricsProperties 内部类
  - 添加配置验证和默认值
  - _Requirements: 1.1, 1.2, 1.4_

- [x] 2.2 实现自动配置类


  - 创建 AlibabaAiAutoConfiguration 自动配置类
  - 配置 DashScopeClient Bean
  - 配置 ChatModel、EmbeddingModel、ImageModel Bean
  - 配置 ToolRegistry 和相关 Bean
  - 创建 META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports 文件
  - _Requirements: 1.1, 1.2_

- [x] 3. 核心模型实现


- [x] 3.1 实现 ChatModel




  - 创建 ChatModel 接口，定义 call 和 stream 方法
  - 实现 AlibabaAiChatModel 类，封装 DashScope 对话 API
  - 实现同步调用逻辑，处理请求构建和响应转换
  - 实现流式调用逻辑，使用 Flux 处理流式响应
  - 添加错误处理和重试机制
  - _Requirements: 2.1, 2.2, 2.3, 2.5, 2.6_

- [x] 3.2 实现 EmbeddingModel




  - 创建 EmbeddingModel 接口，定义 embed 和 embedBatch 方法
  - 实现 AlibabaAiEmbeddingModel 类，封装 DashScope 嵌入 API
  - 实现单文本嵌入逻辑
  - 实现批量文本嵌入逻辑
  - 添加文本长度验证和截断策略
  - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5_

- [x] 3.3 实现 ImageModel




  - 创建 ImageModel 接口，定义 generate 和 generateAsync 方法
  - 实现 AlibabaAiImageModel 类，封装 DashScope 图像生成 API
  - 实现同步图像生成逻辑
  - 实现异步图像生成逻辑
  - 添加图像生成参数配置和错误处理
  - _Requirements: 4.1, 4.2, 4.3, 4.4, 4.5_

- [ ] 4. 数据模型定义



  - 创建 ChatRequest、ChatResponse 数据模型
  - 创建 Message、ToolCall、ToolResult 数据模型
  - 创建 EmbeddingRequest、EmbeddingResponse 数据模型
  - 创建 ImageRequest、ImageResponse 数据模型
  - 创建 Usage 数据模型，记录 Token 使用量
  - _Requirements: 2.1, 3.1, 4.1_

- [x] 5. 工具调用机制实现

- [x] 5.1 实现工具注解


  - 创建 @AiTool 注解，标记可调用的工具方法
  - 创建 @ToolParam 注解，标记工具参数
  - 创建 @ToolDescription 注解，提供工具描述
  - _Requirements: 10.2, 11.1, 11.2_

- [x] 5.2 实现工具注册中心


  - 创建 ToolRegistry 类，管理所有可用工具
  - 实现工具扫描逻辑，自动发现 @AiTool 标记的方法
  - 实现工具注册逻辑，提取工具元数据
  - 实现工具查询方法，支持按名称和分类查询
  - 添加工具启用/禁用配置支持
  - _Requirements: 10.2, 11.2, 11.5_

- [x] 5.3 实现工具执行器


  - 创建 ToolExecutor 类，负责执行工具调用
  - 实现参数验证逻辑，检查必需参数
  - 实现参数准备逻辑，将 Map 转换为方法参数
  - 实现方法调用逻辑，使用反射执行工具方法
  - 添加工具调用的错误处理和重试机制
  - _Requirements: 10.6, 10.7_

- [x] 5.4 实现工具调用处理器


  - 创建 ToolCallHandler 类，处理 AI 模型的工具调用请求
  - 实现工具调用识别逻辑，解析 AI 响应中的工具调用
  - 实现工具调用执行逻辑，调用 ToolExecutor
  - 实现结果回传逻辑，将工具结果返回给 AI 模型
  - 添加工具调用链的日志记录
  - _Requirements: 10.1, 10.8, 13.1, 13.2_

- [x] 6. 内置工具实现


- [x] 6.1 实现天气查询工具

  - 创建 WeatherTool 类，提供天气查询功能
  - 实现 getWeather 方法，查询实时天气
  - 实现 getWeatherForecast 方法，查询天气预报
  - 集成第三方天气 API（如和风天气）
  - 添加天气数据缓存
  - _Requirements: 10.3, 12.1_

- [x] 6.2 实现火车路线查询工具

  - 创建 TrainScheduleTool 类，提供火车信息查询功能
  - 实现 queryTrainSchedule 方法，查询车次时刻表
  - 实现 searchTrains 方法，搜索两地之间的车次
  - 集成第三方火车票 API
  - 添加查询结果缓存
  - _Requirements: 10.4, 12.2_

- [x] 6.3 实现地理位置工具

  - 创建 GeoLocationTool 类，提供地理位置功能
  - 实现 geocodeAddress 方法，地址转坐标
  - 实现 reverseGeocode 方法，坐标转地址
  - 集成地图 API（如高德地图）
  - _Requirements: 12.3_

- [x] 6.4 实现其他实用工具

  - 创建 DateTimeTool 类，提供时间日期功能
  - 创建 CalculatorTool 类，提供数学计算功能
  - 创建 WebSearchTool 类，提供网络搜索功能
  - 为每个工具添加配置选项
  - _Requirements: 12.4, 12.5, 12.6, 12.7_

- [x] 7. 服务层实现

- [x] 7.1 实现统一服务门面


  - 创建 AlibabaAiService 类，提供统一的 AI 服务接口
  - 实现 chat 方法，支持单轮对话
  - 实现 chat 方法重载，支持多轮对话
  - 实现 chatStream 方法，支持流式对话
  - 实现 chatWithTools 方法，支持工具调用的对话
  - _Requirements: 5.1, 5.2_

- [x] 7.2 实现工具调用集成

  - 在 AlibabaAiService 中集成 ToolRegistry 和 ToolExecutor
  - 实现 handleToolCalls 方法，处理 AI 的工具调用请求
  - 实现工具结果回传逻辑
  - 添加工具调用的日志和追踪
  - _Requirements: 5.1, 10.1, 10.8_

- [x] 7.3 实现其他服务方法

  - 实现 embed 方法，提供文本嵌入功能
  - 实现 generateImage 方法，提供图像生成功能
  - 添加服务方法的统一异常处理
  - _Requirements: 5.2_

- [x] 8. 异常处理实现




  - 创建 AiException 基础异常类
  - 创建 ToolExecutionException、ModelNotFoundException 等具体异常类
  - 创建 ApiKeyInvalidException、RateLimitException 等 API 异常类
  - 实现全局异常处理器 AiExceptionHandler
  - 添加异常日志记录
  - _Requirements: 5.3_

- [x] 9. 拦截器和监控实现

- [x] 9.1 实现请求响应拦截器


  - 创建 AiRequestInterceptor 接口
  - 创建 AiResponseInterceptor 接口
  - 实现 LoggingInterceptor，记录请求和响应日志
  - 集成拦截器到核心模型中
  - _Requirements: 5.4_

- [x] 9.2 实现指标收集器


  - 创建 MetricsCollector 类，收集 AI 服务调用指标
  - 实现调用次数、成功率、耗时统计
  - 实现 Token 使用量统计
  - 实现工具调用统计
  - 创建 AiMetricsEndpoint，暴露指标查询接口
  - _Requirements: 9.1, 9.2, 9.3, 9.4_

- [x] 10. 缓存实现


  - 创建 AiCacheManager 类，管理 AI 响应缓存
  - 集成 Redis，实现分布式缓存
  - 实现缓存键生成策略
  - 实现缓存 TTL 配置
  - 在 AlibabaAiService 中集成缓存
  - _Requirements: 8.4_

- [ ] 11. 与现有组件集成
- [ ] 11.1 集成 TLog 链路追踪
  - 创建 AiTracingAspect 切面，记录 TraceId
  - 在工具调用中添加链路追踪
  - 在 AI 服务调用中添加链路追踪
  - _Requirements: 8.2, 13.6_

- [ ] 11.2 集成 Resilience4j 容错
  - 创建 ResilienceConfiguration 配置类
  - 配置 CircuitBreaker，实现熔断机制
  - 配置 RateLimiter，实现限流机制
  - 创建 ResilientAiService，封装容错逻辑
  - _Requirements: 8.3_

- [ ] 11.3 集成日志框架
  - 配置 Logback，遵循项目日志规范
  - 实现日志脱敏，保护敏感信息
  - 添加结构化日志输出
  - _Requirements: 8.5_

- [ ] 12. 工具调用追踪实现
  - 实现工具调用决策过程记录
  - 实现工具调用参数和返回值记录
  - 实现工具调用耗时和状态记录
  - 实现工具调用链可视化数据结构
  - _Requirements: 13.1, 13.2, 13.3, 13.4_

- [ ] 13. 配置文件和文档
- [ ] 13.1 创建配置文件
  - 创建 application-ai-alibaba.yaml 默认配置文件
  - 添加所有配置项和默认值
  - 添加配置项注释说明
  - _Requirements: 1.3, 7.2_

- [ ] 13.2 创建 README 文档
  - 编写模块介绍和功能特性
  - 编写快速开始指南
  - 编写配置说明和示例
  - 编写使用示例（对话、嵌入、图像、工具）
  - 编写常见问题解答
  - _Requirements: 7.1, 7.2, 7.3, 7.4_

- [ ] 13.3 创建 API 文档
  - 为所有公开接口添加 JavaDoc
  - 创建 API 使用示例
  - 创建工具开发指南
  - _Requirements: 7.5_

- [ ] 14. 测试实现
- [ ] 14.1 实现 Mock 支持
  - 创建 MockChatModel、MockEmbeddingModel、MockImageModel
  - 创建 MockAiConfiguration 测试配置类
  - 实现工具调用的 Mock 支持
  - _Requirements: 14.1, 14.6_

- [ ] 14.2 实现测试工具类
  - 创建测试数据构建器
  - 创建测试断言工具
  - 创建测试辅助方法
  - _Requirements: 14.3_

- [ ] 14.3 编写单元测试
  - 为 AlibabaAiChatModel 编写单元测试
  - 为 AlibabaAiEmbeddingModel 编写单元测试
  - 为 AlibabaAiImageModel 编写单元测试
  - 为 ToolRegistry 和 ToolExecutor 编写单元测试
  - 为 AlibabaAiService 编写单元测试
  - _Requirements: 14.4_

- [ ] 14.4 编写集成测试
  - 编写 ChatModel 集成测试
  - 编写工具调用集成测试
  - 编写缓存集成测试
  - 编写容错机制集成测试
  - _Requirements: 14.4_

- [ ] 15. 示例应用
  - 在 sapling-server 中添加示例 Controller
  - 创建对话接口示例
  - 创建工具调用接口示例
  - 创建图像生成接口示例
  - 添加示例配置文件
  - _Requirements: 7.3_

- [ ] 16. 最终集成和验证
  - 更新项目根 pom.xml，添加新模块
  - 更新 sapling-dependencies，添加版本管理
  - 更新 sapling-framework pom.xml，添加模块引用
  - 执行完整构建，确保无编译错误
  - 执行所有测试，确保测试通过
  - 验证自动配置是否正常工作
  - 验证与现有组件的集成是否正常
  - _Requirements: 1.1, 8.1, 8.2, 8.3, 8.4, 8.5_
