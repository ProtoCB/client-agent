package com.protocb.clientagent.scheduler;

import com.protocb.clientagent.AgentState;
import com.protocb.clientagent.config.ActivityState;
import com.protocb.clientagent.dto.ActivityChangeEvent;
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
public class ServerAvailabilityScheduler {

    @Autowired
    private AgentState agentState;

    @Autowired
    private ScheduledExecutorService scheduledExecutorService;

    private List<ActivityState> activityStates;

    private List<ScheduledFuture> schedule;

    private int nextEventIndex;

    @NoArgsConstructor
    private class ServerAvailabilitySchedulingTask implements Runnable {
        @Override
        public void run() {
            ActivityState activityState = getNextState();
            if(activityState == ActivityState.ACTIVE) {
                agentState.setServerAvailable(true);
            } else {
                agentState.setServerAvailable(false);
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
                System.out.println("Not scheduling past event sa");
                //TODO: Log error to file
                continue;
            }

            activityStates.add(event.getState());

            scheduledExecutorService.schedule(new ServerAvailabilitySchedulingTask(), delay, TimeUnit.SECONDS);
        }
        nextEventIndex = activityStates.size() != 0 ? 0 : -1;
    }

    private ActivityState getNextState() {

        if(nextEventIndex < 0) {
            System.out.println("Something wrong with schedule");
            //TODO: Log error to file
            return ActivityState.INACTIVE;
        }

        return activityStates.get(nextEventIndex++);

    }

}