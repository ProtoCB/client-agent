package com.protocb.clientagent;

import com.protocb.clientagent.config.ActivityState;
import com.protocb.clientagent.driver.DriverCoordinator;
import com.protocb.clientagent.dto.ActivityChangeEvent;
import com.protocb.clientagent.dto.ExperimentSchedule;
import com.protocb.clientagent.dto.NetworkPartitionEvent;
import com.protocb.clientagent.dto.RequestRateChangeEvent;
import com.protocb.clientagent.logger.Logger;
import com.protocb.clientagent.requestgenerator.RequestGenerationAgent;
import com.protocb.clientagent.scheduler.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class ClientAgentApplication {

    @Autowired
    private RequestRateScheduler rs;

    @Autowired
    private LifeScheduler lifeScheduler;

    @Autowired
    private NetworkPartitionScheduler n;

    @Autowired
    private ExperimentScheduler e;

    @Autowired
    private ServerAvailabilityScheduler s;

    @Autowired
    private Logger logger;

	public static void main(String[] args) {
		SpringApplication.run(ClientAgentApplication.class, args);
	}

    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) throws Exception {

        long start = Instant.now().getEpochSecond() + 10;

        NetworkPartitionEvent networkPartitionEvent1 = NetworkPartitionEvent.builder().time(start + 5).networkPartitioned(true).build();
        NetworkPartitionEvent networkPartitionEvent2 = NetworkPartitionEvent.builder().time(start + 10).networkPartitioned(false).build();

        List<NetworkPartitionEvent> l = new ArrayList<>();
        l.add(networkPartitionEvent1);
        l.add(networkPartitionEvent2);

        RequestRateChangeEvent r1 = RequestRateChangeEvent.builder().rate(2).time(start + 2).build();
        RequestRateChangeEvent r2 = RequestRateChangeEvent.builder().rate(0.5f).time(start + 5).build();

        List<RequestRateChangeEvent> r = new ArrayList<>();

        r.add(r1);
        r.add(r2);

        ActivityChangeEvent a1 = ActivityChangeEvent.builder().state(ActivityState.ACTIVE).time(start + 2).build();

        ActivityChangeEvent a2 = ActivityChangeEvent.builder().state(ActivityState.INACTIVE).time(start + 7).build();

        List<ActivityChangeEvent> a = new ArrayList<>();
        a.add(a1);
        a.add(a2);

        ExperimentSchedule es = ExperimentSchedule.builder().start(start + 1).end(start + 12).experimentSession("test-ca").build();



        n.scheduleExperiment(l);
        rs.scheduleExperiment(r);
        lifeScheduler.scheduleExperiment(a);
        s.scheduleExperiment(a);
        e.scheduleExperiment(es);

    }

}
