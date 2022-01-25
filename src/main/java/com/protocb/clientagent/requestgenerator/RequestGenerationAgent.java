package com.protocb.clientagent.requestgenerator;

import com.protocb.clientagent.AgentState;
import com.protocb.clientagent.interaction.Observer;
import com.protocb.clientagent.logger.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Component
public class RequestGenerationAgent implements Observer {

    @Autowired
    private Logger logger;

    @Autowired
    private ScheduledExecutorService scheduledExecutorService;

    @Autowired
    private RequestGenerator requestGenerator;

    @Autowired
    private AgentState agentState;

    private ScheduledFuture generatorTask;

    private float requestsPerSecond;

    @PostConstruct
    private void postContruct() {
        agentState.registerObserver(this);
    }

    @PreDestroy
    private void preDestroy() {
        agentState.removeObserver(this);
    }

    public void generateRequestsAtDelay(int delayInMilliseconds) {
        disableRequestGeneration();
        System.out.println("Starting request generation @ " + delayInMilliseconds + " milSec");
        logger.logSchedulingEvent("Starting request generation at " + requestsPerSecond + " per sec");
        generatorTask = scheduledExecutorService.scheduleWithFixedDelay(requestGenerator, 0, delayInMilliseconds, TimeUnit.MILLISECONDS);
    }

    public void disableRequestGeneration() {
        if(isGeneratorActive()) {
            logger.logSchedulingEvent("Disabling running request generator");
            generatorTask.cancel(false);
        }
    }

    private boolean isGeneratorActive() {
        return generatorTask != null && !generatorTask.isCancelled();
    }

    @Override
    public void update() {
        float newRate = agentState.getRequestsPerSecond();
        if(requestsPerSecond == newRate) {
            return;
        } else if(newRate == 0) {
            disableRequestGeneration();
            requestsPerSecond = 0;
            return;
        } else {
            requestsPerSecond = newRate;
            int delay = Math.round(1000/newRate);
            generateRequestsAtDelay(delay);
        }

    }
}
