package com.protocb.clientagent.dto;

import com.protocb.clientagent.config.ActivityState;
import lombok.Data;

@Data
public class ActivityEvent {

    private long time;
    private ActivityState state;

}
