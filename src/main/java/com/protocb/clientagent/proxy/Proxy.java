package com.protocb.clientagent.proxy;

import com.protocb.clientagent.AgentState;
import com.protocb.clientagent.interaction.Observer;
import com.protocb.clientagent.logger.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class Proxy implements Observer {

    @Autowired
    private AgentState agentState;

    @Autowired
    private Logger logger;

    @Autowired
    private ServerRequestBody serverRequestBody;

    private boolean networkPartitioned;

    private List<String> allowList;

    private boolean serverAvailable;

    private String serverUrl;

    private float tfProbability;

    private int minLatency;

    private int failureInferenceTime;

    private WebClient client;

    @PostConstruct
    private void postContruct() {
        allowList = new ArrayList<>();
        serverAvailable = false;
        networkPartitioned = false;
        serverUrl = "Uninitialized";
        tfProbability = 0;
        minLatency = 0;
        failureInferenceTime = 0;
        client = WebClient.create("http://" + serverUrl);
        agentState.registerObserver(this);
    }

    @PreDestroy
    private void preDestroy() {
        agentState.removeObserver(this);
    }

    @Override
    public void update() {
        this.serverAvailable = agentState.isServerAvailable();
        this.networkPartitioned = agentState.isNetworkPartitioned();
        this.allowList = agentState.getPartitionMembers();
        this.tfProbability = agentState.getTfProbability();
        this.minLatency = agentState.getMinLatency();
        this.failureInferenceTime = agentState.getFailureInferenceTime();

        if(!this.serverUrl.equals(agentState.getServerUrl())) {
            this.serverUrl = agentState.getServerUrl();
            client = WebClient.create("http://" + serverUrl);
        }
    }

    public ResponseType sendRequestToServer() {
        try {

            boolean shouldFailTransiently = Math.random() < tfProbability;
            boolean serverReachable = !networkPartitioned || allowList.contains(serverUrl);

            if(serverAvailable && !shouldFailTransiently && serverReachable) {
                return sendActualRequest();
            } else {
                return failRequest();
            }
        } catch (Exception e) {
            logger.logErrorEvent("Proxy Error! - " + e.getMessage());
            return ResponseType.FAILURE;
        }
    }

    private ResponseType sendActualRequest() {
        try {

            serverRequestBody.setMinLatency(minLatency);
            serverRequestBody.setTimestamp(Instant.now().toEpochMilli() % 1000000);

            client.post()
                    .uri("/api/v1/eastbound/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(serverRequestBody))
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofMillis(failureInferenceTime))
                    .block();
            return ResponseType.SUCCESS;
        } catch(Exception e) {
            return ResponseType.FAILURE;
        }
    }

    private ResponseType failRequest() throws InterruptedException {
        System.out.println("SINK");
        Thread.sleep(failureInferenceTime);
        return ResponseType.FAILURE;
    }

}
