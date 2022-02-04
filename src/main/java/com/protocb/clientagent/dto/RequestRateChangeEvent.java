package com.protocb.clientagent.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class RequestRateChangeEvent {

    @NonNull
    private Float rate;

    @NonNull
    private Long time;

}
