package com.protocb.clientagent.config;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static com.protocb.clientagent.config.AgentConstants.SCHEDULER_POOL_SIZE;

@Component
public class DefaultExecutorService {
    @Bean
    public ScheduledExecutorService getDefaultExecuterService() {
        return Executors.newScheduledThreadPool(SCHEDULER_POOL_SIZE);
    }
}
