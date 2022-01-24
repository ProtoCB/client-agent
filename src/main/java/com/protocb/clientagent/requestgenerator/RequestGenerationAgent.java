package com.protocb.clientagent.requestgenerator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Component
public class RequestGenerationAgent {

    @Autowired
    private ScheduledExecutorService scheduledExecutorService;

    @Autowired
    private RequestGenerator requestGenerator;

    private ScheduledFuture generatorTask;

    public void generateRequestsAtDelay(int delayInMilliseconds) {
        disableRequestGeneration();
        generatorTask = scheduledExecutorService.scheduleWithFixedDelay(requestGenerator, 0, delayInMilliseconds, TimeUnit.MILLISECONDS);
    }

    public void disableRequestGeneration() {
        if(isGeneratorActive()) {
            generatorTask.cancel(false);
        }
    }

    private boolean isGeneratorActive() {
        return generatorTask != null && !generatorTask.isCancelled();
    }
}
