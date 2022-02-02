package com.protocb.clientagent.config;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import static com.protocb.clientagent.config.EnvironmentVariables.CONTROLLER_URL;

@Component
public class ProtoCBController {
    @Bean
    public WebClient getControllerApiClient() {
        return WebClient.create("http://" + CONTROLLER_URL);
    }
}
