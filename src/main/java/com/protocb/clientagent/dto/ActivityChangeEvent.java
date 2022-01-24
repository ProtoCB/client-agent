package com.protocb.clientagent.dto;

import com.protocb.clientagent.config.ActivityState;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ActivityChangeEvent {

    private long time;
    private ActivityState state;

}
