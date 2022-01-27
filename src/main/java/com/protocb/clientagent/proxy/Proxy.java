package com.protocb.clientagent.proxy;

import com.protocb.clientagent.AgentState;
import com.protocb.clientagent.interaction.Observer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;

@Component
public class Proxy implements Observer {

    @Autowired
    private AgentState agentState;

    private boolean networkPartitioned;

    private List<String> allowList;

    private boolean serverAvailable;

    private String serverUrl;

    private float tfProbability;

    @PostConstruct
    private void postContruct() {
        allowList = new ArrayList<>();
        serverAvailable = false;
        networkPartitioned = false;
        serverUrl = "Uninitialized";
        tfProbability = 0;
        agentState.registerObserver(this);
    }

    @PreDestroy
    private void preDestroy() {
        agentState.removeObserver(this);
    }

    @Override
    public void update() {
        this.serverUrl = agentState.getServerUrl();
        this.serverAvailable = agentState.isServerAvailable();
        this.networkPartitioned = agentState.isNetworkPartitioned();
        this.allowList = agentState.getPartitionMembers();
        this.tfProbability = agentState.getTfProbability();
    }

    public ResponseType sendRequestToServer() {
        System.out.println("REQUEST - " + serverUrl + ", " + tfProbability);
        return ResponseType.SUCCESS;
    }

}
