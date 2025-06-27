package org.dante.springai.controller;

import org.dante.springai.tool.DateTimeTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    private final ChatClient ollamaChatClient;
    private final ChatClient llama3ChatClient;

    public HelloController(@Qualifier("ollamaChatClient") ChatClient ollamaChatClient,
                           @Qualifier("llama3ChatClient") ChatClient llama3ChatClient) {
        this.ollamaChatClient = ollamaChatClient;
        this.llama3ChatClient = llama3ChatClient;
    }

    @GetMapping("/ai/{userInput}")
    public String generation(@PathVariable("userInput") String userInput) {
        return ollamaChatClient.prompt()
                .user(userInput)
                .tools(new DateTimeTools())
                .call()
                .content();
    }

    @GetMapping("/hello")
    public String hello() {
        return llama3ChatClient.prompt()
               .user("你好")
               .call()
               .content();
    }

    @GetMapping("/img")
    public String img() {
        return llama3ChatClient
                .prompt()
                .user(u -> u.text("说明你在这张照片上看到什么？")
                        .media(MimeTypeUtils.IMAGE_PNG, new ClassPathResource("/multimodal.test.png")))
                .call()
                .content();
    }
}
