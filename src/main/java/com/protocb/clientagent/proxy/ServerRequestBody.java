package com.protocb.clientagent.proxy;

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
public class ServerRequestBody {

    @JsonProperty
    private String ip;

    @JsonProperty
    private int minLatency;

    @JsonProperty
    private long timestamp;

    @Autowired
    private EnvironementVariables environementVariables;

    @PostConstruct
    public void postConstruct() {
        ip = environementVariables.getAgentIp();
        minLatency = 0;
        timestamp = 0;
    }
}
