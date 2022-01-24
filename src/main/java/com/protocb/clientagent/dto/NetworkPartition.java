package com.protocb.clientagent.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class NetworkPartition {

    private boolean networkPartitioned;

    private List<String> partition;

    private long time;

}
