package com.protocb.clientagent.proxy;

import com.protocb.clientagent.AgentState;
import com.protocb.clientagent.circuitbreaker.gedcb.dto.GossipSetState;
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
import java.util.HashMap;
import java.util.List;

import static com.protocb.clientagent.proxy.ResponseType.FAILURE;
import static com.protocb.clientagent.proxy.ResponseType.SUCCESS;

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

    private int failureInferenceTime;

    private WebClient client;

    @PostConstruct
    private void postContruct() {
        allowList = new ArrayList<>();
        serverAvailable = false;
        networkPartitioned = false;
        serverUrl = "Uninitialized";
        tfProbability = 0;
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
        this.failureInferenceTime = agentState.getFailureInferenceTime();

        if(!this.serverUrl.equals(agentState.getServerUrl())) {
            this.serverUrl = agentState.getServerUrl();
            client = WebClient.create("http://" + serverUrl);
        }
    }

    public ResponseType sendRequestToServer(ServerRequestBody serverRequestBody) {
        try {

            boolean shouldFailTransiently = Math.random() < tfProbability;
            boolean serverReachable = !networkPartitioned || allowList.contains(serverUrl);

            if(serverAvailable && !shouldFailTransiently && serverReachable) {
                return sendActualRequest(serverRequestBody);
            } else {
                Thread.sleep(failureInferenceTime);
                return FAILURE;
            }
        } catch (Exception e) {
            System.out.println("Proxy Error!");
            logger.logErrorEvent("Proxy Error! - " + e.getMessage());
            return FAILURE;
        }
    }

    private ResponseType sendActualRequest(ServerRequestBody serverRequestBody) {
        try {

            client.post()
                    .uri("/api/v1/westbound/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(serverRequestBody))
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofMillis(failureInferenceTime))
                    .block();

            return SUCCESS;

        } catch(Exception e) {
            return FAILURE;
        }
    }

    public GossipSetState sendGossipMessage(String clientUrl, GossipSetState gossipSetState) {
        try {

            boolean shouldFailTransiently = Math.random() < tfProbability;
            boolean clientReachable = !networkPartitioned || allowList.contains(clientUrl);

            if(!shouldFailTransiently && clientReachable) {
                return WebClient.create(clientUrl)
                        .post()
                        .uri("/api/v1/gedcb/gossip")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(gossipSetState))
                        .retrieve()
                        .bodyToMono(GossipSetState.class)
                        .timeout(Duration.ofMillis(failureInferenceTime))
                        .block();

            } else {
                Thread.sleep(failureInferenceTime);
                return GossipSetState.builder()
                        .age(new HashMap<>())
                        .opinion(new HashMap<>())
                        .version(-1l)
                        .build();
            }

        } catch(Exception e) {
            logger.logErrorEvent("Failed to send gossip message");
            return GossipSetState.builder()
                    .age(new HashMap<>())
                    .opinion(new HashMap<>())
                    .version(-1l)
                    .build();
        }
    }

}
