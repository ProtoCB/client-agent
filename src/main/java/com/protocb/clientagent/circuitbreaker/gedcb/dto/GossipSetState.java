package com.protocb.clientagent.circuitbreaker.gedcb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.protocb.clientagent.circuitbreaker.CircuitBreakerState;
import lombok.*;

import java.util.Map;

import static com.protocb.clientagent.circuitbreaker.CircuitBreakerState.CLOSED;

@Data
@NoArgsConstructor
@AllArgsConstructor
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

    @Override
    public String toString() {
        String s = "(" + version + ")";
        for(String clientId:opinion.keySet()) {
            s += "[" + clientId + ":(";
            if(this.opinion.get(clientId) == CLOSED) {
                s += "C-";
            } else {
                s += "S-";
            }
            s += this.age.get(clientId) + ")]";
        }
        return s;
    }
}
