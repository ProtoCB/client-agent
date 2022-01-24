package com.protocb.clientagent.scheduler.serveravailability;

import com.protocb.clientagent.AgentState;
import com.protocb.clientagent.Proxy;
import com.protocb.clientagent.config.ActivityState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServerAvailabilitySchedulingTask implements Runnable {

    @Autowired
    private ServerAvailabilityScheduler serverAvailabilityScheduler;

    @Autowired
    private AgentState agentState;

    @Override
    public void run() {
        ActivityState activityState = serverAvailabilityScheduler.getNextState();
        if(activityState == ActivityState.ACTIVE) {
            agentState.setServerAvailable(true);
        } else {
            agentState.setServerAvailable(false);
        }
    }
}
