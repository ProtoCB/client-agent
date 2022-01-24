package com.protocb.clientagent.scheduler.networkpartition;

import com.protocb.clientagent.dto.NetworkPartition;
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
public class NetworkPartitionScheduler {

    @Autowired
    private ScheduledExecutorService scheduledExecutorService;

    @Autowired
    private NetworkPartitionSchedulingTask networkPartitionSchedulingTask;

    private List<NetworkPartition> partitions;

    private List<ScheduledFuture> schedule;

    private int nextEventIndex;

    @PostConstruct
    public void postConstruct() {
        partitions = new ArrayList<>();
        schedule = new ArrayList<>();
    }

    public void cancelExperiment() {
        for(ScheduledFuture scheduledEvent : schedule) {
            if(scheduledEvent != null) {
                scheduledEvent.cancel(false);
            }
        }
        schedule.clear();
        partitions.clear();
    }

    public void scheduleExperiment(List<NetworkPartition> events) {
        for(NetworkPartition event : events) {
            long delay = event.getTime() - Instant.now().toEpochMilli();
            if(delay <= 0) {
                System.out.println("Not scheduling past event");
                //TODO: Log error to file
                continue;
            }
            partitions.add(event);
            scheduledExecutorService.schedule(networkPartitionSchedulingTask, delay, TimeUnit.MILLISECONDS);
        }
        nextEventIndex = partitions.size() != 0 ? 0 : -1;
    }

    public NetworkPartition getNextPartition() {

        if(nextEventIndex < 0) {
            System.out.println("Something wrong with schedule");
            //TODO: Log error to file
            return NetworkPartition.builder().networkPartitioned(false).partition(new ArrayList<String>()).build();
        }

        return partitions.get(nextEventIndex++);

    }


}
