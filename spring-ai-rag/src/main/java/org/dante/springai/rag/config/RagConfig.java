package org.dante.springai.rag.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class RagConfig {

    /**
     * 创建并初始化一个向量存储实例，用于存储文档的语义嵌入。
     *
     * @param embeddingModel 用于生成文本嵌入的模型
     * @return 初始化完成的向量存储实例
     */
    @Bean
    public VectorStore vectorStore(OllamaEmbeddingModel embeddingModel) {
        // 创建基于给定嵌入模型的向量存储实例
        VectorStore store = SimpleVectorStore.builder(embeddingModel).build();

        // 向向量存储中添加预定义文档集合
        store.accept(List.of(
                new Document("Spring Boot 是一个开源 Java 框架，用于快速构建应用。"),
                new Document("Weaviate 是一个向量数据库，支持语义搜索和嵌入存储。"),
                new Document("Ollama 支持在本地运行大型语言模型，例如 LLaMA3、Phi3 等。")
        ));
        return store;
    }

    /**
     * 创建并配置一个QuestionAnswerAdvisor实例。
     * 限制向量检索阈值为0.5，只返回前5个结果。如果检索不到，就不给上下文，配合“只能根据上下文”prompt，模型就会老实回答“我不知道”
     *
     * @param store 用于存储和检索向量的VectorStore对象
     * @return 配置好的QuestionAnswerAdvisor实例
     */
    @Bean
    public QuestionAnswerAdvisor qaAdvisor(VectorStore store) {
        return QuestionAnswerAdvisor.builder(store)
                .searchRequest(SearchRequest.builder()
                        .topK(5)                            // 返回返回前 5 个结果
                        .similarityThreshold(0.5d)           // 相似度阈值，控制只返回相似文段
                        .build())
                .build();
    }

    @Bean
    public ChatClient chatClient(OllamaChatModel model, QuestionAnswerAdvisor advisor) {
        return ChatClient.builder(model)
                .defaultAdvisors(advisor)
                .build();
    }

    /**
     * 创建并配置一个PromptTemplate实例。
     *
     * @return 配置好的PromptTemplate实例
     */
    @Bean
    public PromptTemplate promptTemplate() {
        return new PromptTemplate("""
            请根据以下上下文回答用户问题：
            {documents}

            问题：{query}
            回答：
        """);
    }

}
