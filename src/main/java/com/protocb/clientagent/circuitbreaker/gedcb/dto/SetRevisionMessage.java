package com.protocb.clientagent.circuitbreaker.gedcb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SetRevisionMessage {

    @JsonProperty
    private Long version;

    @JsonProperty
    private List<String> clientIds;

}
