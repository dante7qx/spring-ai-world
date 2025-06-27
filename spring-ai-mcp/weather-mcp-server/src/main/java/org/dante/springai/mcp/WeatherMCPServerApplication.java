package org.dante.springai.mcp;

import org.dante.springai.mcp.service.WeatherService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class WeatherMCPServerApplication {

    public static void main(String[] args) { SpringApplication.run(WeatherMCPServerApplication.class, args); }

    @Bean
    public ToolCallbackProvider weatherTools(WeatherService weatherService) {
        return  MethodToolCallbackProvider.builder().toolObjects(weatherService).build();
    }

}
