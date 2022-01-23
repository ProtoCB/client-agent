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

    public boolean isGeneratorActive() {
        if(generatorTask != null && !generatorTask.isCancelled()) {
            return true;
        } else {
            return false;
        }
    }

    public void generateRequestsAtDelay(int delayInMilliseconds) {
        if(isGeneratorActive()) {
            generatorTask.cancel(false);
        }

        generatorTask = scheduledExecutorService.scheduleAtFixedRate(requestGenerator, 0, delayInMilliseconds, TimeUnit.MILLISECONDS);
    }

}
