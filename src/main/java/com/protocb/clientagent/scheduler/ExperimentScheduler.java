package com.protocb.clientagent.scheduler;

import com.protocb.clientagent.AgentState;
import com.protocb.clientagent.config.ActivityState;
import com.protocb.clientagent.dto.ActivityChangeEvent;
import com.protocb.clientagent.dto.ExperimentSchedule;
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
public class ExperimentScheduler {

    @Autowired
    private AgentState agentState;

    @Autowired
    private ScheduledExecutorService scheduledExecutorService;

    private List<ActivityState> activityStates;

    private List<ScheduledFuture> schedule;

    private int nextEventIndex;

    @NoArgsConstructor
    private class ExperimentSchedulingTask implements Runnable {
        @Override
        public void run() {
            ActivityState activityState = getNextState();
            if(activityState == ActivityState.ACTIVE) {
                agentState.setExperimentUnderProgress(true);
            } else {
                agentState.setExperimentUnderProgress(false);
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

    public void scheduleExperiment(ExperimentSchedule schedule) {

        long delay = schedule.getStart() - Instant.now().getEpochSecond();

        if(delay <= 0) {
            System.out.println("Not scheduling past event exp");
            //TODO: Log error to file
        } else {
            activityStates.add(ActivityState.ACTIVE);
            scheduledExecutorService.schedule(new ExperimentSchedulingTask(), delay, TimeUnit.SECONDS);
        }

        delay = schedule.getEnd() - Instant.now().getEpochSecond();

        if(delay <= 0) {
            System.out.println("Not scheduling past event exp");
            //TODO: Log error to file
        } else {
            activityStates.add(ActivityState.INACTIVE);
            scheduledExecutorService.schedule(new ExperimentSchedulingTask(), delay, TimeUnit.SECONDS);
        }

        nextEventIndex = 0;
    }

    public ActivityState getNextState() {

        if(nextEventIndex < 0) {
            System.out.println("Something wrong with schedule");
            //TODO: Log error to file
            return ActivityState.INACTIVE;
        }

        return activityStates.get(nextEventIndex++);

    }

}

