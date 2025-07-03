package org.dante.springai.rag.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Configuration
public class RagOllamaConfig {

    /**
     * 创建并初始化一个向量存储实例，用于存储文档的语义嵌入。
     *
     * @param embeddingModel 用于生成文本嵌入的模型
     * @return 初始化完成的向量存储实例
     */
    @Bean("vectorStore")
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
     * 创建并初始化一个向量存储实例，用于存储文档的语义嵌入。
     * 从指定路径下的文件中读取文档，并将其分割成文本块，然后将这些文本块转换为文档并存储到向量存储中。
     *
     * @param embeddingModel 用于生成文本嵌入的模型
     * @return 初始化完成的向量存储实例
     * @throws IOException 如果在读取文件时发生 I/O 错误
     */
    @Bean("vectorStore4File")
    public VectorStore vectorStore4File(OllamaEmbeddingModel embeddingModel) throws IOException {
        var splitter = new TokenTextSplitter();
        VectorStore store = SimpleVectorStore.builder(embeddingModel).build();
        try (var paths = Files.walk(Path.of("spring-ai-rag/docs"))) {

            List<Document> documents = paths
                    .filter(Files::isRegularFile)
                    .map(path -> {
                        try {
                            var resource = new FileSystemResource(path);
                            var reader = new TikaDocumentReader(resource);
                            return reader.get();
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to read file: " + path, e);
                        }
                    })
                    .flatMap(List::stream)
                    .flatMap(doc -> splitter.split(doc).stream())
                    .toList();

            store.accept(documents);
        }

        return store;
    }

    /**
     * 问答Advisor - 直接针对用户问题提供答案，通常基于预定义的规则、模型内部知识或结构化知识库（如 FAQ 库）。
     * 可能不涉及动态检索外部信息，而是依赖内置的问答对或模型的参数化知识。
     * <p>
     * 限制向量检索阈值为0.5，只返回前5个结果。如果检索不到，就不给上下文，配合“只能根据上下文”prompt，模型就会老实回答“我不知道”
     * 典型应用场景：
     * 1. 封闭领域的标准问答（例如客服机器人回答常见问题）。
     * 2. 需要快速响应且答案范围固定的场景。
     *
     * @param store 用于存储和检索向量的VectorStore对象
     * @return 配置好的QuestionAnswerAdvisor实例
     */
    @Bean("qaAdvisor")
    public QuestionAnswerAdvisor qaAdvisor(@Qualifier("vectorStore") VectorStore store) {
        return QuestionAnswerAdvisor.builder(store)
                .searchRequest(SearchRequest.builder()
                        .topK(5)                             // 返回返回前 5 个结果
                        .similarityThreshold(0.5d)           // 相似度阈值，控制只返回相似文段
                        .build())
                .build();
    }

    /**
     * 检索增强Advisor - 专注于通过检索外部知识源（如数据库、文档集、知识图谱或互联网）来增强问答或对话系统的回答质量。
     * 它会在生成答案前，先检索相关信息作为上下文，再结合检索到的内容生成或优化回答
     * <p>
     * 典型应用场景：
     * 1. 需要动态补充最新或外部知识的任务（例如，基于企业文档、研究论文或实时数据的问答）。
     * 2. 解决大语言模型（LLM）的“知识截止”问题（如 ChatGPT 的知识过期时，通过检索最新数据增强回答）。
     *
     * @param vectorStore 用于存储和检索向量的VectorStore对象
     * @return 返回检索增强Advisor实例
     */
    @Bean("retrievalAugmentationAdvisor")
    public RetrievalAugmentationAdvisor retrievalAugmentationAdvisor(@Qualifier("vectorStore4File") VectorStore vectorStore) {
        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .topK(5)
                        .similarityThreshold(0.5d)
                        .vectorStore(vectorStore)
                        .build())
                .queryAugmenter(ContextualQueryAugmenter.builder()
                        .allowEmptyContext(true)
                        .build())
                .build();
    }

    @Bean
    public ChatClient chatClient(OllamaChatModel model) {
        return ChatClient.builder(model).build();
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
