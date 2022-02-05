package com.protocb.clientagent.circuitbreaker.gedcb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.protocb.clientagent.circuitbreaker.CircuitBreakerState;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.Map;

@Data
@Builder
public class GossipSetState {
    @NonNull
    @JsonProperty
    private Long version;

    @NonNull
    @JsonProperty
    private Map<String, CircuitBreakerState> opinion;

    @NonNull
    @JsonProperty
    private Map<String, Integer> age;
}
