package org.dante.springai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    @Bean
    ChatClient ollamaChatClient(OllamaChatModel chatModel) {
        ChatClient.Builder builder = ChatClient.builder(chatModel);
        builder.defaultAdvisors(new SimpleLoggerAdvisor());
        return builder.build();
    }

    @Bean("llama3ChatClient")
    ChatClient chatClient(ChatClient.Builder builder) {
        builder.defaultSystem("""
                        You are a great assistant, and you answer questions in Chinese.
                        """)
                .defaultOptions(OllamaOptions.builder()
                        .model("llama3.2-vision:11b")
                        .temperature(0.3)
                        .build())
                .defaultAdvisors(new SimpleLoggerAdvisor());


        return builder.build();
    }

}
