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

    private void generateRequestsAtDelay(int delayInMilliseconds) {
        generatorTask = scheduledExecutorService.scheduleWithFixedDelay(requestGenerator, 0, delayInMilliseconds, TimeUnit.MILLISECONDS);
    }

    private void disableRequestGeneration() {
        if(isGeneratorActive()) {
            logger.logSchedulingEvent("Disabling Request Generator");
            generatorTask.cancel(true);
        }
    }

    private boolean isGeneratorActive() {
        return generatorTask != null && !generatorTask.isCancelled();
    }

    @Override
    public void update() {
        float newRate = agentState.getRequestsPerSecond();
        boolean alive = agentState.isAlive();

        if(!alive || newRate == 0) {
            disableRequestGeneration();
            requestsPerSecond = 0;
        } else if(newRate != requestsPerSecond){
            requestsPerSecond = newRate;
            int delay = Math.round(1000/newRate);
            disableRequestGeneration();
            generateRequestsAtDelay(delay);
        }

    }
}
