package com.protocb.clientagent.heartbeat;

import com.protocb.clientagent.AgentState;
import com.protocb.clientagent.config.EnvironementVariables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.protocb.clientagent.config.AgentConstants.*;

@Component
public class HeartbeatDriver {

    @Autowired
    private AgentState agentState;

    @Autowired
    private HeartbeatPayload heartbeatPayload;

    @Autowired
    private ScheduledExecutorService scheduledExecutorService;

    @Autowired
    private EnvironementVariables environementVariables;

    private WebClient client;

    private void sendHeartbeat() {

        String experimentSession = agentState.getExperimentSession();
        String experimentStatus = agentState.getExperimentStatus();

        heartbeatPayload.setExperimentSession(experimentSession);
        heartbeatPayload.setExperimentStatus(experimentStatus);

        try {
            client.post()
                    .uri("/api/v1/heartbeat/client-agent")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(heartbeatPayload))
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofMillis(HEARTBEAT_TIMEOUT))
                    .block();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Heartbeat Error!");
        }
    }

    @PostConstruct
    public void scheduleHeartbeats() {
        client = WebClient.create("http://" + environementVariables.getControllerUrl());
        scheduledExecutorService.scheduleWithFixedDelay( () -> sendHeartbeat(), 2000, HEARTBEAT_DELAY, TimeUnit.MILLISECONDS);
    }

}
