package com.protocb.clientagent;

import com.protocb.clientagent.exchanges.ExperimentRecipe;
import com.protocb.clientagent.scheduler.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.protocb.clientagent.config.EnvironmentVariables.AGENT_SECRET;
import static com.protocb.clientagent.config.EnvironmentVariables.NORTHBOUND_ENDPOINT;

@RestController
@RequestMapping(NORTHBOUND_ENDPOINT)
public class NorthBoundAPI {

    @Autowired
    private AgentState agentState;

    @Autowired
    private RequestRateScheduler requestRateScheduler;

    @Autowired
    private LifeScheduler lifeScheduler;

    @Autowired
    private NetworkPartitionScheduler networkPartitionScheduler;

    @Autowired
    private ExperimentScheduler experimentScheduler;

    @Autowired
    private ServerAvailabilityScheduler serverAvailabilityScheduler;

    @PostMapping("/schedule-experiment")
    public ResponseEntity scheduleExperiment(@RequestHeader("agent-secret") String secret, @RequestBody ExperimentRecipe experimentRecipe) {
        try {

            if(!secret.equals(AGENT_SECRET)) {
                return ResponseEntity.status(401).body(null);
            }

            if(!agentState.getExperimentSession().equals("Uninitialized")) {
                return ResponseEntity.status(401).body(null);
            }

            System.out.println(experimentRecipe.toString());

            agentState.setExperimentSession(experimentRecipe.getExperimentSession());
            agentState.setServerUrl(experimentRecipe.getServerUrl());
            agentState.setTfProbability(experimentRecipe.getTfProbability());
            agentState.setCircuitBreakerType(experimentRecipe.getCircuitBreakerType());
            agentState.setCircuitBreakerParameters(experimentRecipe.getCircuitBreakerParameters());
            agentState.setMinLatency(experimentRecipe.getMinLatency());
            agentState.setFailureInferenceTime(experimentRecipe.getFailureInferenceTime());

            requestRateScheduler.scheduleExperiment(experimentRecipe.getRequestRateSchedule());
            lifeScheduler.scheduleExperiment(experimentRecipe.getClientLifeSchedule());
            networkPartitionScheduler.scheduleExperiment(experimentRecipe.getNetworkPartitionSchedule());
            serverAvailabilityScheduler.scheduleExperiment(experimentRecipe.getServerAvailabilitySchedule());
            experimentScheduler.scheduleExperiment(experimentRecipe.getExperimentSchedule());

            return ResponseEntity.ok().body(null);

        } catch(Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PatchMapping("/reset-agent")
    public ResponseEntity resetAgent(@RequestHeader("agent-secret") String secret) {
        try {

            if(!secret.equals(AGENT_SECRET)) {
                return ResponseEntity.status(401).body(null);
            }

            experimentScheduler.cancelExperiment();
            lifeScheduler.cancelExperiment();
            requestRateScheduler.cancelExperiment();
            networkPartitionScheduler.cancelExperiment();
            serverAvailabilityScheduler.cancelExperiment();

            agentState.resetAgent();

            return ResponseEntity.ok().body(null);

        } catch(Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

}
