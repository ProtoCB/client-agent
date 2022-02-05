package com.protocb.clientagent.heartbeat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static com.protocb.clientagent.config.EnvironmentVariables.AGENT_SECRET;
import static com.protocb.clientagent.config.EnvironmentVariables.AGENT_URL;

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

    @PostConstruct
    public void postConstruct() {
        ip = AGENT_URL;
        agentSecret = AGENT_SECRET;
        experimentSession = "Uninitialized";
        experimentStatus = "Uninitialized";
    }

}