package com.protocb.clientagent.heartbeat;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.protocb.clientagent.config.EnvironementVariables;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Getter
@Setter
public class HeartbeatPayload {

    @JsonProperty
    private String ip;

    @JsonProperty
    private String agentSecret;

    @JsonProperty
    private String experimentSession;

    @JsonProperty
    private String experimentStatus;

    @Autowired
    private EnvironementVariables environementVariables;

    @PostConstruct
    public void postConstruct() {
        ip = environementVariables.getAgentIp();
        agentSecret = environementVariables.getAgentSecret();
        experimentSession = "Uninitialized";
        experimentStatus = "Uninitialized";
    }

}
