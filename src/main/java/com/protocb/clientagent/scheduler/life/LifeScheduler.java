package com.protocb.clientagent.scheduler.life;

import com.protocb.clientagent.config.ActivityState;
import com.protocb.clientagent.dto.ActivityEvent;
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
    private ScheduledExecutorService scheduledExecutorService;

    @Autowired
    private LifeSchedulingTask lifeSchedulingTask;

    private List<ActivityState> activityStates;

    private List<ScheduledFuture> schedule;

    private int nextEventIndex;

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

    public void scheduleExperiment(List<ActivityEvent> events) {
        for(ActivityEvent event : events) {
            long delay = event.getTime() - Instant.now().toEpochMilli();
            if(delay <= 0) {
                System.out.println("Not scheduling past event");
                //TODO: Log error to file
                continue;
            }
            activityStates.add(event.getState());
            scheduledExecutorService.schedule(lifeSchedulingTask, delay, TimeUnit.MILLISECONDS);
        }
        nextEventIndex = activityStates.size() != 0 ? 0 : -1;
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
