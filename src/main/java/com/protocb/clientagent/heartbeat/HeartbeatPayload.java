package com.protocb.clientagent.heartbeat;

import lombok.Data;
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

    private String ip;

    private String agentSecret;

    @PostConstruct
    public void postConstruct() {
        ip = AGENT_URL;
        agentSecret = AGENT_SECRET;
    }

}
