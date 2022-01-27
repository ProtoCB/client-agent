package com.protocb.clientagent.driver;

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
public class DriverCoordinator implements Observer {

    @Autowired
    private Logger logger;

    @Autowired
    private ScheduledExecutorService scheduledExecutorService;

    @Autowired
    private AgentState agentState;

    @Autowired
    private Driver driver;

    private ScheduledFuture driverTask;

    @PostConstruct
    private void postContruct() {
        agentState.registerObserver(this);
    }

    @PreDestroy
    private void preDestroy() {
        agentState.removeObserver(this);
    }

    public void enableDriver() {
        System.out.println("Enabling driver");
        logger.logSchedulingEvent("Enabling Driver");
        disableDriver();
        driverTask = scheduledExecutorService.schedule(driver, 0, TimeUnit.MILLISECONDS);
    }

    public void disableDriver() {
        if(isDriverActive()) {
            System.out.println("Disabling running driver");
            logger.logSchedulingEvent("Disabling running driver");
            driverTask.cancel(true);
        }
    }

    private boolean isDriverActive() {
        return driverTask != null && !driverTask.isCancelled();
    }

    @Override
    public void update() {
        boolean agentAlive = agentState.isAlive();
        if(isDriverActive() && !agentAlive) {
            disableDriver();
        } else if(!isDriverActive() && agentAlive) {
            enableDriver();
        }
    }
}
