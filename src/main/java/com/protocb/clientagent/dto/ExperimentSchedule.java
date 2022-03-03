package com.protocb.clientagent.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.ToString;

@Data
@Builder
@ToString
public class ExperimentSchedule {

    @NonNull
    private Long start;

    @NonNull
    private Long end;

}
