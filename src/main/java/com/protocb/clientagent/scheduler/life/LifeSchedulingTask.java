package com.protocb.clientagent.scheduler.life;

import com.protocb.clientagent.AgentState;
import com.protocb.clientagent.config.ActivityState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LifeSchedulingTask implements Runnable {

    @Autowired
    private LifeScheduler lifeScheduler;

    @Autowired
    private AgentState agentState;

    @Override
    public void run() {
        ActivityState activityState = lifeScheduler.getNextState();
        if(activityState == ActivityState.ACTIVE) {
            agentState.setAlive(true);
        } else {
            agentState.setAlive(false);
        }
    }
}
