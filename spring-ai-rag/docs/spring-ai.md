# Spring AI 项目简介

## 概述

Spring AI 是 Spring 生态系统中专门用于构建 AI 应用程序的框架。它提供了一个统一的抽象层，使开发者能够轻松地集成各种 AI 服务和模型到 Spring 应用程序中。

## 主要功能

### 1. 多模型支持
- **大语言模型 (LLM)**：支持 OpenAI GPT、Azure OpenAI、Google PaLM、Anthropic Claude 等
- **嵌入模型**：支持文本向量化，用于语义搜索和相似性匹配
- **图像生成模型**：集成 DALL-E、Stability AI 等图像生成服务

### 2. 统一的 API 接口
- 提供一致的编程模型，屏蔽不同 AI 服务提供商的差异
- 简化模型切换和多模型对比的复杂性
- 支持异步和同步调用模式

### 3. 向量数据库集成
- 原生支持多种向量数据库：Weaviate、Pinecone、Chroma、Qdrant
- 提供 VectorStore 抽象层，简化向量操作
- 支持文档分块、嵌入存储和相似性搜索

### 4. RAG (检索增强生成) 支持
- 内置 RAG 模式实现
- 文档加载器和文本分割器
- 上下文检索和问答链构建

## 核心组件

### ChatClient
```java
@Autowired
private ChatClient chatClient;

public String chat(String message) {
    return chatClient.call(message);
}
```

### EmbeddingClient
```java
@Autowired
private EmbeddingClient embeddingClient;

public List<Double> embed(String text) {
    return embeddingClient.embed(text);
}
```

### VectorStore
```java
@Autowired
private VectorStore vectorStore;

public void storeDocument(String content) {
    Document doc = new Document(content);
    vectorStore.add(List.of(doc));
}
```

## 配置示例

### application.yml
```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      chat:
        model: gpt-3.5-turbo
      embedding:
        model: text-embedding-ada-002
    vectorstore:
      weaviate:
        url: http://localhost:8080
        api-key: ${WEAVIATE_API_KEY}
```

## 应用场景

1. **智能客服系统**：结合 RAG 技术，基于企业知识库回答用户问题
2. **文档问答**：对大量文档进行智能检索和问答
3. **代码助手**：集成代码生成和解释功能
4. **内容生成**：自动化文案、报告生成
5. **语义搜索**：基于向量相似性的搜索系统

## 优势特点

- **Spring 生态集成**：无缝集成 Spring Boot、Spring Security 等
- **企业级特性**：支持监控、配置管理、安全性
- **可扩展性**：插件化架构，易于扩展新的 AI 服务
- **生产就绪**：提供错误处理、重试机制、限流等企业级功能

## 快速开始

1. 添加依赖：
```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-openai-spring-boot-starter</artifactId>
</dependency>
```

2. 配置服务：
```properties
spring.ai.openai.api-key=your-api-key
```

3. 使用服务：
```java
@RestController
public class ChatController {
    
    @Autowired
    private ChatClient chatClient;
    
    @PostMapping("/chat")
    public String chat(@RequestBody String message) {
        return chatClient.call(message);
    }
}
```

Spring AI 为企业级 AI 应用开发提供了强大而灵活的解决方案，大大简化了 AI 技术的集成和应用。
