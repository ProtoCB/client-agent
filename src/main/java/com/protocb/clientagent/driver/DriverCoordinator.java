package com.protocb.clientagent.driver;

import com.protocb.clientagent.AgentState;
import com.protocb.clientagent.interaction.Observer;
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
        disableDriver();
        driverTask = scheduledExecutorService.schedule(driver, 0, TimeUnit.MILLISECONDS);
    }

    public void disableDriver() {
        if(isDriverActive()) {
            driverTask.cancel(true);
        }
    }

    private boolean isDriverActive() {
        return driverTask != null && !driverTask.isCancelled();
    }

    @Override
    public void update() {
        boolean experimentUnderProgress = agentState.isExperimentUnderProgress();
        if(isDriverActive() && !experimentUnderProgress) {
            disableDriver();
        } else if(!isDriverActive() && experimentUnderProgress) {
            enableDriver();
        }
    }
}
