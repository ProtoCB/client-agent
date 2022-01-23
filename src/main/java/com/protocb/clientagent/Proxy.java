package com.protocb.clientagent;

import com.protocb.clientagent.interaction.Observer;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;

public class Proxy implements Observer {

    @Autowired
    private AgentState agentState;

    private List<String> allowList;

    private boolean serverAvailable;

    private String serverUrl;

    private float tfProbability;

    @PostConstruct
    private void postContruct() {
        allowList = new ArrayList<>();
        serverAvailable = false;
        agentState.registerObserver(this);
        this.update();
    }

    @PreDestroy
    private void preDestroy() {
        agentState.removeObserver(this);
    }

    @Override
    public void update() {
        this.serverUrl = agentState.getServerUrl();
        this.tfProbability = agentState.getTfProbability();
    }

    public void sendRequestToServer() {
        System.out.println("REQUEST");
    }

}
