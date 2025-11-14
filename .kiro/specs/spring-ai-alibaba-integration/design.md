# Design Document

## Overview

本设计文档描述了 Spring AI Alibaba 集成模块的架构设计和实现方案。该模块将作为 `sapling-spring-boot-starter-ai-alibaba` 添加到 sapling-framework 中，提供开箱即用的阿里云 AI 能力。

### 设计目标

1. **开箱即用**：通过 Spring Boot 自动配置，简化集成流程
2. **功能完整**：支持对话、嵌入、图像生成和工具调用
3. **易于扩展**：提供清晰的扩展点，支持自定义工具和模型
4. **生产就绪**：集成监控、日志、缓存和容错机制
5. **符合规范**：遵循项目的 DDD 架构和编码规范

### 技术选型

- **Spring AI Alibaba**: 1.0.0-M3（最新稳定版）
- **DashScope SDK**: 2.16.0（阿里云通义千问 SDK）
- **Spring Boot**: 3.2.5（项目现有版本）
- **Java**: 17（项目现有版本）

## Architecture

### 模块结构

```
sapling-spring-boot-starter-ai-alibaba/
├── src/main/java/com/sapling/framework/ai/alibaba/
│   ├── config/                          # 配置类
│   │   ├── AlibabaAiAutoConfiguration.java
│   │   ├── AlibabaAiProperties.java
│   │   ├── ChatModelConfiguration.java
│   │   ├── EmbeddingModelConfiguration.java
│   │   ├── ImageModelConfiguration.java
│   │   └── ToolConfiguration.java
│   ├── core/                            # 核心实现
│   │   ├── chat/
│   │   │   ├── AlibabaAiChatModel.java
│   │   │   └── AlibabaAiStreamingChatModel.java
│   │   ├── embedding/
│   │   │   └── AlibabaAiEmbeddingModel.java
│   │   ├── image/
│   │   │   └── AlibabaAiImageModel.java
│   │   └── tool/
│   │       ├── ToolRegistry.java
│   │       ├── ToolExecutor.java
│   │       └── ToolCallHandler.java
│   ├── service/                         # 服务层
│   │   ├── AlibabaAiService.java
│   │   ├── ChatService.java
│   │   ├── EmbeddingService.java
│   │   ├── ImageService.java
│   │   └── ToolService.java
│   ├── tool/                            # 内置工具
│   │   ├── annotation/
│   │   │   ├── AiTool.java
│   │   │   ├── ToolParam.java
│   │   │   └── ToolDescription.java
│   │   ├── builtin/
│   │   │   ├── WeatherTool.java
│   │   │   ├── TrainScheduleTool.java
│   │   │   ├── GeoLocationTool.java
│   │   │   ├── DateTimeTool.java
│   │   │   ├── CalculatorTool.java
│   │   │   └── WebSearchTool.java
│   │   └── support/
│   │       ├── ToolDefinition.java
│   │       ├── ToolParameter.java
│   │       └── ToolResult.java
│   ├── interceptor/                     # 拦截器
│   │   ├── AiRequestInterceptor.java
│   │   ├── AiResponseInterceptor.java
│   │   └── LoggingInterceptor.java
│   ├── metrics/                         # 监控指标
│   │   ├── AiMetrics.java
│   │   ├── MetricsCollector.java
│   │   └── TokenUsageTracker.java
│   ├── cache/                           # 缓存支持
│   │   ├── AiCacheManager.java
│   │   └── CacheKeyGenerator.java
│   ├── exception/                       # 异常处理
│   │   ├── AiException.java
│   │   ├── ToolExecutionException.java
│   │   ├── ModelNotFoundException.java
│   │   └── ApiKeyInvalidException.java
│   └── util/                            # 工具类
│       ├── PromptTemplateUtil.java
│       ├── TokenCountUtil.java
│       └── JsonUtil.java
├── src/main/resources/
│   ├── META-INF/
│   │   └── spring/
│   │       └── org.springframework.boot.autoconfigure.AutoConfiguration.imports
│   └── application-ai-alibaba.yaml      # 默认配置
└── pom.xml
```

### 分层架构

遵循项目的 DDD 分层架构：

```
┌─────────────────────────────────────────┐
│         Service Layer (服务层)           │  ← AlibabaAiService
├─────────────────────────────────────────┤
│         Core Layer (核心层)              │  ← ChatModel, EmbeddingModel
├─────────────────────────────────────────┤
│         Tool Layer (工具层)              │  ← ToolRegistry, ToolExecutor
├─────────────────────────────────────────┤
│         Infrastructure (基础设施层)       │  ← Config, Interceptor, Cache
└─────────────────────────────────────────┘
```

## Components and Interfaces

### 1. 配置组件

#### AlibabaAiProperties

配置属性类，管理所有 AI 相关配置：

```java
@ConfigurationProperties(prefix = "spring.ai.alibaba")
public class AlibabaAiProperties {
    private String apiKey;
    private String baseUrl = "https://dashscope.aliyuncs.com/api/v1";
    private ChatProperties chat = new ChatProperties();
    private EmbeddingProperties embedding = new EmbeddingProperties();
    private ImageProperties image = new ImageProperties();
    private ToolProperties tool = new ToolProperties();
    private CacheProperties cache = new CacheProperties();
    private MetricsProperties metrics = new MetricsProperties();
    
    public static class ChatProperties {
        private String model = "qwen-turbo";
        private Double temperature = 0.7;
        private Integer maxTokens = 2000;
        private Double topP = 0.8;
        private Boolean enableStream = true;
    }
    
    public static class EmbeddingProperties {
        private String model = "text-embedding-v1";
        private Integer dimensions = 1536;
    }
    
    public static class ImageProperties {
        private String model = "wanx-v1";
        private String size = "1024*1024";
        private Integer n = 1;
    }
    
    public static class ToolProperties {
        private Boolean enabled = true;
        private List<String> enabledTools = new ArrayList<>();
        private Integer maxRetries = 3;
    }
}
```

#### AlibabaAiAutoConfiguration

自动配置类，负责 Bean 的创建和初始化：

```java
@Configuration
@EnableConfigurationProperties(AlibabaAiProperties.class)
@ConditionalOnProperty(prefix = "spring.ai.alibaba", name = "enabled", havingValue = "true", matchIfMissing = true)
public class AlibabaAiAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public DashScopeClient dashScopeClient(AlibabaAiProperties properties) {
        return DashScopeClient.builder()
            .apiKey(properties.getApiKey())
            .baseUrl(properties.getBaseUrl())
            .build();
    }
    
    @Bean
    @ConditionalOnMissingBean
    public ChatModel chatModel(DashScopeClient client, AlibabaAiProperties properties) {
        return new AlibabaAiChatModel(client, properties.getChat());
    }
    
    @Bean
    @ConditionalOnMissingBean
    public EmbeddingModel embeddingModel(DashScopeClient client, AlibabaAiProperties properties) {
        return new AlibabaAiEmbeddingModel(client, properties.getEmbedding());
    }
    
    @Bean
    @ConditionalOnMissingBean
    public ImageModel imageModel(DashScopeClient client, AlibabaAiProperties properties) {
        return new AlibabaAiImageModel(client, properties.getImage());
    }
    
    @Bean
    @ConditionalOnMissingBean
    public ToolRegistry toolRegistry() {
        return new ToolRegistry();
    }
    
    @Bean
    @ConditionalOnMissingBean
    public AlibabaAiService alibabaAiService(
            ChatModel chatModel,
            EmbeddingModel embeddingModel,
            ImageModel imageModel,
            ToolRegistry toolRegistry) {
        return new AlibabaAiService(chatModel, embeddingModel, imageModel, toolRegistry);
    }
}
```

### 2. 核心模型组件

#### ChatModel 接口

对话模型接口，支持同步和流式调用：

```java
public interface ChatModel {
    /**
     * 同步调用对话模型
     */
    ChatResponse call(ChatRequest request);
    
    /**
     * 流式调用对话模型
     */
    Flux<ChatResponse> stream(ChatRequest request);
}

public class AlibabaAiChatModel implements ChatModel {
    private final DashScopeClient client;
    private final ChatProperties properties;
    private final MetricsCollector metricsCollector;
    
    @Override
    public ChatResponse call(ChatRequest request) {
        long startTime = System.currentTimeMillis();
        try {
            // 构建请求
            GenerationRequest genRequest = buildRequest(request);
            
            // 调用 API
            GenerationResponse response = client.generation(genRequest);
            
            // 记录指标
            metricsCollector.recordCall(
                System.currentTimeMillis() - startTime,
                response.getUsage().getTotalTokens()
            );
            
            return convertResponse(response);
        } catch (Exception e) {
            metricsCollector.recordError();
            throw new AiException("Chat model call failed", e);
        }
    }
    
    @Override
    public Flux<ChatResponse> stream(ChatRequest request) {
        return Flux.create(sink -> {
            try {
                GenerationRequest genRequest = buildRequest(request);
                genRequest.setStreamingMode(true);
                
                client.streamGeneration(genRequest, new StreamingResponseHandler() {
                    @Override
                    public void onEvent(GenerationResponse response) {
                        sink.next(convertResponse(response));
                    }
                    
                    @Override
                    public void onComplete() {
                        sink.complete();
                    }
                    
                    @Override
                    public void onError(Exception e) {
                        sink.error(new AiException("Stream failed", e));
                    }
                });
            } catch (Exception e) {
                sink.error(e);
            }
        });
    }
}
```

#### EmbeddingModel 接口

文本嵌入模型接口：

```java
public interface EmbeddingModel {
    /**
     * 单文本嵌入
     */
    EmbeddingResponse embed(String text);
    
    /**
     * 批量文本嵌入
     */
    List<EmbeddingResponse> embedBatch(List<String> texts);
}

public class AlibabaAiEmbeddingModel implements EmbeddingModel {
    private final DashScopeClient client;
    private final EmbeddingProperties properties;
    
    @Override
    public EmbeddingResponse embed(String text) {
        TextEmbeddingRequest request = TextEmbeddingRequest.builder()
            .model(properties.getModel())
            .input(text)
            .build();
            
        TextEmbeddingResponse response = client.textEmbedding(request);
        return convertResponse(response);
    }
    
    @Override
    public List<EmbeddingResponse> embedBatch(List<String> texts) {
        return texts.stream()
            .map(this::embed)
            .collect(Collectors.toList());
    }
}
```

#### ImageModel 接口

图像生成模型接口：

```java
public interface ImageModel {
    /**
     * 根据文本提示生成图像
     */
    ImageResponse generate(ImageRequest request);
    
    /**
     * 异步生成图像
     */
    CompletableFuture<ImageResponse> generateAsync(ImageRequest request);
}

public class AlibabaAiImageModel implements ImageModel {
    private final DashScopeClient client;
    private final ImageProperties properties;
    
    @Override
    public ImageResponse generate(ImageRequest request) {
        ImageSynthesisRequest imgRequest = ImageSynthesisRequest.builder()
            .model(properties.getModel())
            .prompt(request.getPrompt())
            .size(properties.getSize())
            .n(properties.getN())
            .build();
            
        ImageSynthesisResponse response = client.imageSynthesis(imgRequest);
        return convertResponse(response);
    }
    
    @Override
    public CompletableFuture<ImageResponse> generateAsync(ImageRequest request) {
        return CompletableFuture.supplyAsync(() -> generate(request));
    }
}
```

### 3. 工具调用组件

#### 工具注解

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AiTool {
    /**
     * 工具名称
     */
    String name();
    
    /**
     * 工具描述
     */
    String description();
    
    /**
     * 工具分类
     */
    String category() default "general";
    
    /**
     * 是否启用
     */
    boolean enabled() default true;
}

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ToolParam {
    /**
     * 参数名称
     */
    String name();
    
    /**
     * 参数描述
     */
    String description();
    
    /**
     * 是否必需
     */
    boolean required() default true;
}
```

#### ToolRegistry

工具注册中心，管理所有可用工具：

```java
@Component
public class ToolRegistry {
    private final Map<String, ToolDefinition> tools = new ConcurrentHashMap<>();
    private final ApplicationContext applicationContext;
    
    @PostConstruct
    public void scanTools() {
        // 扫描所有标记 @AiTool 的方法
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(Component.class);
        for (Object bean : beans.values()) {
            Method[] methods = bean.getClass().getDeclaredMethods();
            for (Method method : methods) {
                AiTool annotation = method.getAnnotation(AiTool.class);
                if (annotation != null && annotation.enabled()) {
                    registerTool(bean, method, annotation);
                }
            }
        }
    }
    
    private void registerTool(Object bean, Method method, AiTool annotation) {
        ToolDefinition definition = ToolDefinition.builder()
            .name(annotation.name())
            .description(annotation.description())
            .category(annotation.category())
            .bean(bean)
            .method(method)
            .parameters(extractParameters(method))
            .build();
            
        tools.put(annotation.name(), definition);
    }
    
    public ToolDefinition getTool(String name) {
        return tools.get(name);
    }
    
    public List<ToolDefinition> getAllTools() {
        return new ArrayList<>(tools.values());
    }
}
```

#### ToolExecutor

工具执行器，负责调用工具方法：

```java
@Component
public class ToolExecutor {
    private final ToolRegistry toolRegistry;
    private final MetricsCollector metricsCollector;
    
    public ToolResult execute(String toolName, Map<String, Object> parameters) {
        long startTime = System.currentTimeMillis();
        
        try {
            ToolDefinition tool = toolRegistry.getTool(toolName);
            if (tool == null) {
                throw new ToolExecutionException("Tool not found: " + toolName);
            }
            
            // 验证参数
            validateParameters(tool, parameters);
            
            // 准备方法参数
            Object[] args = prepareArguments(tool, parameters);
            
            // 执行方法
            Object result = tool.getMethod().invoke(tool.getBean(), args);
            
            // 记录指标
            metricsCollector.recordToolCall(
                toolName,
                System.currentTimeMillis() - startTime,
                true
            );
            
            return ToolResult.success(result);
            
        } catch (Exception e) {
            metricsCollector.recordToolCall(toolName, 
                System.currentTimeMillis() - startTime, false);
            return ToolResult.failure(e.getMessage());
        }
    }
}
```

#### 内置工具实现

##### WeatherTool - 天气查询工具

```java
@Component
public class WeatherTool {
    
    @AiTool(
        name = "get_weather",
        description = "获取指定城市的实时天气信息",
        category = "weather"
    )
    public WeatherInfo getWeather(
            @ToolParam(name = "city", description = "城市名称") String city) {
        // 调用天气 API
        return weatherApiClient.getCurrentWeather(city);
    }
    
    @AiTool(
        name = "get_weather_forecast",
        description = "获取指定城市的天气预报",
        category = "weather"
    )
    public List<WeatherForecast> getWeatherForecast(
            @ToolParam(name = "city", description = "城市名称") String city,
            @ToolParam(name = "days", description = "预报天数", required = false) Integer days) {
        int forecastDays = days != null ? days : 7;
        return weatherApiClient.getForecast(city, forecastDays);
    }
}
```

##### TrainScheduleTool - 火车路线查询工具

```java
@Component
public class TrainScheduleTool {
    
    @AiTool(
        name = "query_train_schedule",
        description = "查询火车车次信息和时刻表",
        category = "transportation"
    )
    public TrainSchedule queryTrainSchedule(
            @ToolParam(name = "trainNumber", description = "车次号") String trainNumber,
            @ToolParam(name = "date", description = "出发日期", required = false) String date) {
        LocalDate travelDate = date != null ? LocalDate.parse(date) : LocalDate.now();
        return trainApiClient.getSchedule(trainNumber, travelDate);
    }
    
    @AiTool(
        name = "search_trains",
        description = "搜索两地之间的火车车次",
        category = "transportation"
    )
    public List<TrainInfo> searchTrains(
            @ToolParam(name = "from", description = "出发城市") String from,
            @ToolParam(name = "to", description = "到达城市") String to,
            @ToolParam(name = "date", description = "出发日期", required = false) String date) {
        LocalDate travelDate = date != null ? LocalDate.parse(date) : LocalDate.now();
        return trainApiClient.searchTrains(from, to, travelDate);
    }
}
```

##### GeoLocationTool - 地理位置工具

```java
@Component
public class GeoLocationTool {
    
    @AiTool(
        name = "geocode_address",
        description = "将地址转换为经纬度坐标",
        category = "location"
    )
    public GeoCoordinate geocodeAddress(
            @ToolParam(name = "address", description = "地址") String address) {
        return geoApiClient.geocode(address);
    }
    
    @AiTool(
        name = "reverse_geocode",
        description = "将经纬度坐标转换为地址",
        category = "location"
    )
    public Address reverseGeocode(
            @ToolParam(name = "latitude", description = "纬度") Double latitude,
            @ToolParam(name = "longitude", description = "经度") Double longitude) {
        return geoApiClient.reverseGeocode(latitude, longitude);
    }
}
```

### 4. 服务层组件

#### AlibabaAiService

统一的 AI 服务门面：

```java
@Service
public class AlibabaAiService {
    private final ChatModel chatModel;
    private final EmbeddingModel embeddingModel;
    private final ImageModel imageModel;
    private final ToolRegistry toolRegistry;
    private final ToolExecutor toolExecutor;
    private final AiCacheManager cacheManager;
    
    /**
     * 单轮对话
     */
    public String chat(String message) {
        ChatRequest request = ChatRequest.builder()
            .message(message)
            .build();
        ChatResponse response = chatModel.call(request);
        return response.getContent();
    }
    
    /**
     * 多轮对话
     */
    public String chat(List<Message> history, String message) {
        ChatRequest request = ChatRequest.builder()
            .history(history)
            .message(message)
            .build();
        ChatResponse response = chatModel.call(request);
        return response.getContent();
    }
    
    /**
     * 流式对话
     */
    public Flux<String> chatStream(String message) {
        ChatRequest request = ChatRequest.builder()
            .message(message)
            .build();
        return chatModel.stream(request)
            .map(ChatResponse::getContent);
    }
    
    /**
     * 带工具调用的对话
     */
    public String chatWithTools(String message, List<String> enabledTools) {
        // 构建请求，包含工具定义
        List<ToolDefinition> tools = enabledTools.stream()
            .map(toolRegistry::getTool)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
            
        ChatRequest request = ChatRequest.builder()
            .message(message)
            .tools(tools)
            .build();
            
        ChatResponse response = chatModel.call(request);
        
        // 如果 AI 决定调用工具
        if (response.hasToolCalls()) {
            return handleToolCalls(response, message);
        }
        
        return response.getContent();
    }
    
    private String handleToolCalls(ChatResponse response, String originalMessage) {
        List<ToolResult> results = new ArrayList<>();
        
        for (ToolCall toolCall : response.getToolCalls()) {
            ToolResult result = toolExecutor.execute(
                toolCall.getName(),
                toolCall.getParameters()
            );
            results.add(result);
        }
        
        // 将工具调用结果返回给 AI
        ChatRequest followUpRequest = ChatRequest.builder()
            .message(originalMessage)
            .toolResults(results)
            .build();
            
        ChatResponse finalResponse = chatModel.call(followUpRequest);
        return finalResponse.getContent();
    }
    
    /**
     * 文本嵌入
     */
    public List<Double> embed(String text) {
        String cacheKey = "embed:" + text.hashCode();
        return cacheManager.get(cacheKey, () -> {
            EmbeddingResponse response = embeddingModel.embed(text);
            return response.getEmbedding();
        });
    }
    
    /**
     * 图像生成
     */
    public String generateImage(String prompt) {
        ImageRequest request = ImageRequest.builder()
            .prompt(prompt)
            .build();
        ImageResponse response = imageModel.generate(request);
        return response.getImageUrl();
    }
}
```

## Data Models

### 请求和响应模型

```java
@Data
@Builder
public class ChatRequest {
    private String message;
    private List<Message> history;
    private List<ToolDefinition> tools;
    private List<ToolResult> toolResults;
    private Double temperature;
    private Integer maxTokens;
}

@Data
public class ChatResponse {
    private String content;
    private List<ToolCall> toolCalls;
    private Usage usage;
    private String finishReason;
    
    public boolean hasToolCalls() {
        return toolCalls != null && !toolCalls.isEmpty();
    }
}

@Data
public class Message {
    private String role;  // user, assistant, system
    private String content;
}

@Data
public class ToolCall {
    private String id;
    private String name;
    private Map<String, Object> parameters;
}

@Data
public class ToolResult {
    private String toolCallId;
    private boolean success;
    private Object result;
    private String error;
    
    public static ToolResult success(Object result) {
        ToolResult tr = new ToolResult();
        tr.setSuccess(true);
        tr.setResult(result);
        return tr;
    }
    
    public static ToolResult failure(String error) {
        ToolResult tr = new ToolResult();
        tr.setSuccess(false);
        tr.setError(error);
        return tr;
    }
}

@Data
public class Usage {
    private Integer promptTokens;
    private Integer completionTokens;
    private Integer totalTokens;
}
```

## Error Handling

### 异常层次结构

```java
public class AiException extends RuntimeException {
    private String errorCode;
    
    public AiException(String message) {
        super(message);
    }
    
    public AiException(String message, Throwable cause) {
        super(message, cause);
    }
}

public class ToolExecutionException extends AiException {
    public ToolExecutionException(String message) {
        super(message);
    }
}

public class ModelNotFoundException extends AiException {
    public ModelNotFoundException(String model) {
        super("Model not found: " + model);
    }
}

public class ApiKeyInvalidException extends AiException {
    public ApiKeyInvalidException() {
        super("Invalid API key");
    }
}

public class RateLimitException extends AiException {
    public RateLimitException() {
        super("Rate limit exceeded");
    }
}
```

### 全局异常处理

```java
@ControllerAdvice
public class AiExceptionHandler {
    
    @ExceptionHandler(AiException.class)
    public ResponseEntity<ErrorResponse> handleAiException(AiException e) {
        log.error("AI exception occurred", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse.of(e.getMessage()));
    }
    
    @ExceptionHandler(ApiKeyInvalidException.class)
    public ResponseEntity<ErrorResponse> handleApiKeyInvalid(ApiKeyInvalidException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ErrorResponse.of("Invalid API key"));
    }
    
    @ExceptionHandler(RateLimitException.class)
    public ResponseEntity<ErrorResponse> handleRateLimit(RateLimitException e) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
            .body(ErrorResponse.of("Rate limit exceeded"));
    }
}
```

## Testing Strategy

### 单元测试

```java
@SpringBootTest
class AlibabaAiChatModelTest {
    
    @MockBean
    private DashScopeClient client;
    
    @Autowired
    private ChatModel chatModel;
    
    @Test
    void testChatCall() {
        // Given
        ChatRequest request = ChatRequest.builder()
            .message("Hello")
            .build();
            
        GenerationResponse mockResponse = createMockResponse();
        when(client.generation(any())).thenReturn(mockResponse);
        
        // When
        ChatResponse response = chatModel.call(request);
        
        // Then
        assertNotNull(response);
        assertEquals("Hello! How can I help you?", response.getContent());
    }
}
```

### 集成测试

```java
@SpringBootTest
@TestPropertySource(properties = {
    "spring.ai.alibaba.api-key=test-key",
    "spring.ai.alibaba.enabled=true"
})
class AlibabaAiServiceIntegrationTest {
    
    @Autowired
    private AlibabaAiService aiService;
    
    @Test
    void testChatWithTools() {
        String response = aiService.chatWithTools(
            "北京今天天气怎么样？",
            List.of("get_weather")
        );
        
        assertNotNull(response);
        assertTrue(response.contains("天气"));
    }
}
```

### Mock 支持

```java
@Configuration
@Profile("test")
public class MockAiConfiguration {
    
    @Bean
    @Primary
    public ChatModel mockChatModel() {
        return new MockChatModel();
    }
    
    @Bean
    @Primary
    public EmbeddingModel mockEmbeddingModel() {
        return new MockEmbeddingModel();
    }
}

public class MockChatModel implements ChatModel {
    @Override
    public ChatResponse call(ChatRequest request) {
        return ChatResponse.builder()
            .content("Mock response for: " + request.getMessage())
            .build();
    }
    
    @Override
    public Flux<ChatResponse> stream(ChatRequest request) {
        return Flux.just(call(request));
    }
}
```

## Integration with Existing Components

### 1. TLog 链路追踪集成

```java
@Aspect
@Component
public class AiTracingAspect {
    
    @Around("@annotation(aiTool)")
    public Object traceToolCall(ProceedingJoinPoint pjp, AiTool aiTool) throws Throwable {
        String traceId = TLogContext.getTraceId();
        log.info("<{}> Calling tool: {}", traceId, aiTool.name());
        
        long startTime = System.currentTimeMillis();
        try {
            Object result = pjp.proceed();
            log.info("<{}> Tool {} completed in {}ms", 
                traceId, aiTool.name(), System.currentTimeMillis() - startTime);
            return result;
        } catch (Exception e) {
            log.error("<{}> Tool {} failed", traceId, aiTool.name(), e);
            throw e;
        }
    }
}
```

### 2. Resilience4j 容错集成

```java
@Configuration
public class ResilienceConfiguration {
    
    @Bean
    public CircuitBreaker aiCircuitBreaker() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
            .failureRateThreshold(50)
            .waitDurationInOpenState(Duration.ofSeconds(30))
            .slidingWindowSize(10)
            .build();
            
        return CircuitBreaker.of("ai-service", config);
    }
    
    @Bean
    public RateLimiter aiRateLimiter() {
        RateLimiterConfig config = RateLimiterConfig.custom()
            .limitForPeriod(100)
            .limitRefreshPeriod(Duration.ofMinutes(1))
            .timeoutDuration(Duration.ofSeconds(5))
            .build();
            
        return RateLimiter.of("ai-service", config);
    }
}

@Service
public class ResilientAiService {
    private final AlibabaAiService aiService;
    private final CircuitBreaker circuitBreaker;
    private final RateLimiter rateLimiter;
    
    public String chat(String message) {
        return Decorators.ofSupplier(() -> aiService.chat(message))
            .withCircuitBreaker(circuitBreaker)
            .withRateLimiter(rateLimiter)
            .withRetry(Retry.ofDefaults("ai-retry"))
            .get();
    }
}
```

### 3. Redis 缓存集成

```java
@Component
public class AiCacheManager {
    private final RedissonClient redissonClient;
    private final AlibabaAiProperties properties;
    
    public <T> T get(String key, Supplier<T> loader) {
        if (!properties.getCache().isEnabled()) {
            return loader.get();
        }
        
        RMap<String, T> cache = redissonClient.getMap("ai:cache");
        T value = cache.get(key);
        
        if (value == null) {
            value = loader.get();
            cache.put(key, value, 
                properties.getCache().getTtl(), 
                TimeUnit.SECONDS);
        }
        
        return value;
    }
}
```

### 4. 日志集成

```java
@Slf4j
@Component
public class LoggingInterceptor implements AiRequestInterceptor, AiResponseInterceptor {
    
    @Override
    public void beforeRequest(ChatRequest request) {
        log.info("AI Request: model={}, message={}", 
            request.getModel(), 
            truncate(request.getMessage(), 100));
    }
    
    @Override
    public void afterResponse(ChatResponse response, long duration) {
        log.info("AI Response: tokens={}, duration={}ms, finish_reason={}", 
            response.getUsage().getTotalTokens(),
            duration,
            response.getFinishReason());
    }
    
    @Override
    public void onError(Exception e) {
        log.error("AI Error: {}", e.getMessage(), e);
    }
}
```

## Performance and Monitoring

### 指标收集

```java
@Component
public class MetricsCollector {
    private final AtomicLong totalCalls = new AtomicLong(0);
    private final AtomicLong successCalls = new AtomicLong(0);
    private final AtomicLong failedCalls = new AtomicLong(0);
    private final AtomicLong totalTokens = new AtomicLong(0);
    private final LongAdder totalDuration = new LongAdder();
    
    public void recordCall(long duration, int tokens) {
        totalCalls.incrementAndGet();
        successCalls.incrementAndGet();
        totalDuration.add(duration);
        totalTokens.addAndGet(tokens);
    }
    
    public void recordError() {
        totalCalls.incrementAndGet();
        failedCalls.incrementAndGet();
    }
    
    public AiMetrics getMetrics() {
        long calls = totalCalls.get();
        return AiMetrics.builder()
            .totalCalls(calls)
            .successCalls(successCalls.get())
            .failedCalls(failedCalls.get())
            .successRate(calls > 0 ? (double) successCalls.get() / calls : 0)
            .totalTokens(totalTokens.get())
            .avgDuration(calls > 0 ? totalDuration.sum() / calls : 0)
            .build();
    }
}

@RestController
@RequestMapping("/actuator/ai")
public class AiMetricsEndpoint {
    private final MetricsCollector metricsCollector;
    
    @GetMapping("/metrics")
    public AiMetrics getMetrics() {
        return metricsCollector.getMetrics();
    }
}
```

## Configuration Examples

### application-ai-alibaba.yaml

```yaml
spring:
  ai:
    alibaba:
      enabled: true
      api-key: ${AI_API_KEY:your-api-key}
      base-url: https://dashscope.aliyuncs.com/api/v1
      
      chat:
        model: qwen-turbo
        temperature: 0.7
        max-tokens: 2000
        top-p: 0.8
        enable-stream: true
        
      embedding:
        model: text-embedding-v1
        dimensions: 1536
        
      image:
        model: wanx-v1
        size: 1024*1024
        n: 1
        
      tool:
        enabled: true
        enabled-tools:
          - get_weather
          - get_weather_forecast
          - query_train_schedule
          - search_trains
          - geocode_address
        max-retries: 3
        
      cache:
        enabled: true
        ttl: 3600
        
      metrics:
        enabled: true
        export-interval: 60
```

## Security Considerations

1. **API Key 加密**: 使用 Jasypt 加密 API Key
2. **工具权限控制**: 限制敏感工具的访问
3. **速率限制**: 防止 API 滥用
4. **输入验证**: 验证用户输入，防止注入攻击
5. **日志脱敏**: 敏感信息不记录到日志

## Deployment Considerations

1. **环境变量**: API Key 通过环境变量配置
2. **配置分离**: 不同环境使用不同的配置文件
3. **健康检查**: 提供健康检查端点
4. **优雅关闭**: 确保请求完成后再关闭
5. **资源限制**: 配置合理的超时和并发限制
