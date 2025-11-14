# Sapling Spring Boot Starter AI Alibaba

Spring AI Alibaba 集成模块，为 Sapling Framework 提供开箱即用的阿里云 AI 能力。

## 功能特性

- ✅ **对话模型**：支持通义千问对话模型，提供同步和流式调用
- ✅ **文本嵌入**：支持文本向量化，用于语义搜索和相似度计算
- ✅ **图像生成**：支持通义万相图像生成模型
- ✅ **工具调用**：支持 Function Calling，AI 可调用外部工具
- ✅ **内置工具**：提供天气查询、火车路线、地理位置等常用工具
- ✅ **自动配置**：Spring Boot 自动配置，开箱即用
- ✅ **缓存支持**：集成 Redis 缓存，提高响应速度
- ✅ **容错机制**：集成 Resilience4j，提供熔断和限流
- ✅ **链路追踪**：集成 TLog，支持分布式链路追踪
- ✅ **性能监控**：内置指标收集，监控 AI 服务使用情况

## 快速开始

### 1. 添加依赖

在 `pom.xml` 中添加依赖：

```xml
<dependency>
    <groupId>com.sapling</groupId>
    <artifactId>sapling-spring-boot-starter-ai-alibaba</artifactId>
</dependency>
```

### 2. 配置

在 `application.yaml` 中配置：

```yaml
spring:
  ai:
    alibaba:
      enabled: true
      api-key: ${AI_ALIBABA_API_KEY}  # 从环境变量读取
      
      chat:
        model: qwen-turbo
        temperature: 0.7
        max-tokens: 2000
        
      tool:
        enabled: true
        enabled-tools:
          - get_weather
          - calculate
```

### 3. 使用

```java
@RestController
@RequestMapping("/ai")
public class AiController {
    
    @Autowired
    private AlibabaAiService aiService;
    
    @GetMapping("/chat")
    public String chat(@RequestParam String message) {
        return aiService.chat(message);
    }
    
    @GetMapping("/chat-with-tools")
    public String chatWithTools(@RequestParam String message) {
        return aiService.chatWithTools(message, 
            List.of("get_weather", "calculate"));
    }
}
```

## 配置说明

### 基础配置

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| `spring.ai.alibaba.enabled` | 是否启用 | `true` |
| `spring.ai.alibaba.api-key` | API Key | 必填 |
| `spring.ai.alibaba.base-url` | API 地址 | `https://dashscope.aliyuncs.com/api/v1` |

### 对话模型配置

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| `spring.ai.alibaba.chat.model` | 模型名称 | `qwen-turbo` |
| `spring.ai.alibaba.chat.temperature` | 温度参数 | `0.7` |
| `spring.ai.alibaba.chat.max-tokens` | 最大 Token 数 | `2000` |
| `spring.ai.alibaba.chat.top-p` | Top-P 参数 | `0.8` |
| `spring.ai.alibaba.chat.enable-stream` | 启用流式响应 | `true` |

### 工具配置

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| `spring.ai.alibaba.tool.enabled` | 启用工具调用 | `true` |
| `spring.ai.alibaba.tool.enabled-tools` | 启用的工具列表 | `[]` |
| `spring.ai.alibaba.tool.max-retries` | 最大重试次数 | `3` |

## 使用示例

### 简单对话

```java
String response = aiService.chat("你好，请介绍一下自己");
```

### 多轮对话

```java
List<Message> history = new ArrayList<>();
history.add(Message.user("我叫张三"));
history.add(Message.assistant("你好张三，很高兴认识你"));

String response = aiService.chat(history, "我刚才说我叫什么？");
```

### 流式对话

```java
Flux<String> stream = aiService.chatStream("讲一个故事");
stream.subscribe(chunk -> System.out.print(chunk));
```

### 带工具调用的对话

```java
String response = aiService.chatWithTools(
    "北京今天天气怎么样？",
    List.of("get_weather")
);
```

### 文本嵌入

```java
List<Double> embedding = aiService.embed("这是一段文本");
```

### 图像生成

```java
String imageUrl = aiService.generateImage("一只可爱的猫咪");
```

## 内置工具

### 天气工具

- `get_weather`: 获取实时天气
- `get_weather_forecast`: 获取天气预报

### 交通工具

- `query_train_schedule`: 查询火车时刻表
- `search_trains`: 搜索火车车次

### 实用工具

- `calculate`: 数学计算
- `get_current_time`: 获取当前时间
- `geocode_address`: 地址转坐标
- `web_search`: 网络搜索

## 自定义工具

### 1. 创建工具类

```java
@Component
public class MyTool {
    
    @AiTool(
        name = "my_tool",
        description = "我的自定义工具",
        category = "custom"
    )
    public String myMethod(
            @ToolParam(name = "param1", description = "参数1") String param1) {
        return "处理结果";
    }
}
```

### 2. 启用工具

```yaml
spring:
  ai:
    alibaba:
      tool:
        enabled-tools:
          - my_tool
```

## 常见问题

### Q: 如何获取 API Key？

A: 访问阿里云 DashScope 控制台申请：https://dashscope.console.aliyun.com/

### Q: 支持哪些模型？

A: 
- 对话：qwen-turbo, qwen-plus, qwen-max
- 嵌入：text-embedding-v1, text-embedding-v2
- 图像：wanx-v1

### Q: 如何启用缓存？

A: 配置 Redis 并启用缓存：

```yaml
spring:
  ai:
    alibaba:
      cache:
        enabled: true
        ttl: 3600
```

## 许可证

Apache License 2.0
