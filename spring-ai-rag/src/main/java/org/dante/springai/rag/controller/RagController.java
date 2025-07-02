package org.dante.springai.rag.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rag")
public class RagController {

    private final ChatClient chatClient;

    public RagController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping
    public String ask(@RequestParam("query") String query) {
        return chatClient.prompt()
                .user(query)
                .call()
                .content();
    }
}
