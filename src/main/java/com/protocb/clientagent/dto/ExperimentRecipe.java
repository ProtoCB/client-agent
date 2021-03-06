package com.protocb.clientagent.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@ToString
public class ExperimentRecipe {

    @JsonProperty
    @NonNull
    private String experimentSession;

    @JsonProperty
    @NonNull
    private List<String> eventsToLog;

    @JsonProperty
    @NonNull
    private String serverUrl;

    @JsonProperty
    @NonNull
    private Float tfProbability;

    @JsonProperty
    @NonNull
    private String circuitBreakerType;

    @JsonProperty
    @NonNull
    private Map<String, Integer> circuitBreakerParameters;

    @JsonProperty
    @NonNull
    private Integer minLatency;

    @JsonProperty
    @NonNull
    private Integer failureInferenceTime;

    @JsonProperty
    @NonNull
    private ExperimentSchedule experimentSchedule;

    @JsonProperty
    @NonNull
    private List<NetworkPartitionEvent> networkPartitionSchedule;

    @JsonProperty
    @NonNull
    private List<ActivityChangeEvent> serverAvailabilitySchedule;

    @JsonProperty
    @NonNull
    private List<ActivityChangeEvent> clientLifeSchedule;

    @JsonProperty
    @NonNull
    private List<RequestRateChangeEvent> requestRateSchedule;

}
