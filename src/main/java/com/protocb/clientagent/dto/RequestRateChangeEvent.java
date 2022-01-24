package com.protocb.clientagent.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestRateChangeEvent {

    private float rate;

    private long time;

}
