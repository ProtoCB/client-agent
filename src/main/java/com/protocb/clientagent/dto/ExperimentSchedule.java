package com.protocb.clientagent.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExperimentSchedule {

    private long start;
    private long end;
    private String experimentSession;

}
