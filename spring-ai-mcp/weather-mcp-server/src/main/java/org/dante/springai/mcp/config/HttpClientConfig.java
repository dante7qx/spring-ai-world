package org.dante.springai.mcp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.Executors;

@Configuration
public class HttpClientConfig {

    @Bean
    public HttpClient httpClient() {
        return HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)  // HTTP/2支持
                .connectTimeout(Duration.ofSeconds(5))  // 连接超时
                .followRedirects(HttpClient.Redirect.NORMAL)  // 自动重定向
                .executor(Executors.newFixedThreadPool(20))  // 线程池
                .build();
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("https://je4ky33383.re.qweatherapi.com")
                .defaultHeader("Accept", "application/json")  // 默认请求头
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

}
