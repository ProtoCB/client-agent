package com.protocb.clientagent.proxy;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.protocb.clientagent.config.EnvironementVariables;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Data
public class ServerResponseBody {

    @JsonProperty
    private Boolean serverAvailable;

}
