package com.protocb.clientagent.dto;

import com.protocb.clientagent.config.ActivityState;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class ActivityChangeEvent {

    @NonNull
    private Long time;

    @NonNull
    private ActivityState state;

}
