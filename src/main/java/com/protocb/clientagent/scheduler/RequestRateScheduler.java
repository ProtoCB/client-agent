package com.protocb.clientagent.scheduler;

import com.protocb.clientagent.AgentState;
import com.protocb.clientagent.dto.RequestRateChangeEvent;
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
public class RequestRateScheduler {

    @Autowired
    private AgentState agentState;

    @Autowired
    private ScheduledExecutorService scheduledExecutorService;

    private List<Float> requestRates;

    private List<ScheduledFuture> schedule;

    private int nextEventIndex;

    @NoArgsConstructor
    private class RequestRateSchedulingTask implements Runnable {
        @Override
        public void run() {
            float newRate = getNextRate();
            agentState.setRequestsPerSecond(newRate);
        }
    }

    @PostConstruct
    public void postConstruct() {
        requestRates = new ArrayList<>();
        schedule = new ArrayList<>();
    }

    public void cancelExperiment() {
        for(ScheduledFuture scheduledEvent : schedule) {
            if(scheduledEvent != null) {
                scheduledEvent.cancel(false);
            }
        }
        schedule.clear();
        requestRates.clear();
    }

    public void scheduleExperiment(List<RequestRateChangeEvent> events) {
        for(RequestRateChangeEvent event : events) {
            long delay = event.getTime() - Instant.now().getEpochSecond();

            if(delay <= 0) {
                System.out.println("Not scheduling past event reqrate");
                //TODO: Log error to file
                continue;
            }

            requestRates.add(event.getRate());

            scheduledExecutorService.schedule(new RequestRateSchedulingTask(), delay, TimeUnit.SECONDS);
        }
        nextEventIndex = requestRates.size() != 0 ? 0 : -1;
    }

    private float getNextRate() {

        if(nextEventIndex < 0) {
            System.out.println("Something wrong with schedule");
            //TODO: Log error to file
            return 0;
        }

        return requestRates.get(nextEventIndex++);

    }

}
