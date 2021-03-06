package com.protocb.clientagent.scheduler;

import com.protocb.clientagent.AgentState;
import com.protocb.clientagent.dto.ActivityChangeEvent;
import com.protocb.clientagent.logger.Logger;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Component
public class LifeScheduler {

    @Autowired
    private Logger logger;

    @Autowired
    private AgentState agentState;

    @Autowired
    private ScheduledExecutorService scheduledExecutorService;

    private List<ActivityState> activityStates;

    private List<ScheduledFuture> schedule;

    private int nextEventIndex;

    @NoArgsConstructor
    private class LifeSchedulingTask implements Runnable {
        @Override
        public void run() {
            ActivityState activityState = getNextState();
            if(activityState == ActivityState.ACTIVE) {
                agentState.setAlive(true);
                logger.logSchedulingEvent("Agent - Alive");
            } else {
                agentState.setAlive(false);
                logger.logSchedulingEvent("Agent - Dead");
            }
        }
    }


    @PostConstruct
    public void postConstruct() {
        activityStates = new ArrayList<>();
        schedule = new ArrayList<>();
    }

    public void cancelExperiment() {
        for(ScheduledFuture scheduledEvent : schedule) {
            if(scheduledEvent != null) {
                scheduledEvent.cancel(false);
            }
        }
        schedule.clear();
        activityStates.clear();
    }

    public void scheduleExperiment(List<ActivityChangeEvent> events) {
        for(ActivityChangeEvent event : events) {
            long delay = event.getTime() - Instant.now().getEpochSecond();

            if(delay <= 0) {
                continue;
            }

            activityStates.add(event.getState());

            scheduledExecutorService.schedule(new LifeSchedulingTask(), delay, TimeUnit.SECONDS);
        }
        nextEventIndex = activityStates.size() != 0 ? 0 : -1;
    }

    private ActivityState getNextState() {

        if(nextEventIndex < 0) {
            logger.logErrorEvent("LifeScheduler - Trying to work on an empty schedule");
            return ActivityState.INACTIVE;
        }

        return activityStates.get(nextEventIndex++);

    }

}

