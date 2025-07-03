package org.dante.springai.rag.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/rag")
public class RagController {

    private final ChatClient chatClient;
    private final Advisor qaAdvisor;
    private final Advisor retrievalAugmentationAdvisor;

    public RagController(ChatClient chatClient,
                         @Qualifier("qaAdvisor") Advisor qaAdvisor,
                         @Qualifier("retrievalAugmentationAdvisor") Advisor retrievalAugmentationAdvisor) {
        this.chatClient = chatClient;
        this.qaAdvisor = qaAdvisor;
        this.retrievalAugmentationAdvisor = retrievalAugmentationAdvisor;
    }

    @GetMapping
    public String ask(@RequestParam("query") String query) {
        log.info("QAAdvisor query: {}", query);
        return chatClient.prompt()
                .advisors(qaAdvisor)
                .user(query)
                .call()
                .content();
    }

    @GetMapping("/ask2")
    public String ask2(@RequestParam("query") String query) {
        log.info("RetrievalAugmentationAdvisor query: {}", query);
        return chatClient.prompt()
                .advisors(retrievalAugmentationAdvisor)
                .user(query)
                .call()
                .content();
    }
}
