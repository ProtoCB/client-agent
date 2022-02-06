package com.protocb.clientagent;

import com.protocb.clientagent.interaction.Observer;
import com.protocb.clientagent.interaction.Subject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Getter
@NoArgsConstructor
public class AgentState implements Subject {

    private String experimentSession;

    private List<String> eventsToLog;

    private boolean experimentUnderProgress;

    private String experimentStatus;

    private boolean alive;

    private boolean networkPartitioned;

    private List<String> partitionMembers;

    private boolean serverAvailable;

    private String serverUrl;

    private float tfProbability;

    private ArrayList<Observer> observers;

    private float requestsPerSecond;

    private String circuitBreakerType;

    private Map<String, Integer> circuitBreakerParameters;

    private int minLatency;

    private int failureInferenceTime;

    @PostConstruct
    private void postContruct() {
        this.observers = new ArrayList<>();
        this.alive = false;
        this.networkPartitioned = false;
        this.experimentSession = "Uninitialized";
        this.partitionMembers = new ArrayList<>();
        this.eventsToLog = new ArrayList<>();
        this.serverUrl = "Uninitialized";
        this.tfProbability = 0;
        this.requestsPerSecond = 0;
        this.experimentUnderProgress = false;
        this.experimentStatus = "Uninitialized";
        this.circuitBreakerType = "Closed";
        this.circuitBreakerParameters = new HashMap<>();
        this.minLatency = 0;
        this.failureInferenceTime = 0;
    }

    @Override
    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for(Observer observer : observers) {
            observer.update();
        }
    }

    public void resetAgent() {
        this.alive = false;
        this.networkPartitioned = false;
        this.experimentSession = "Uninitialized";
        this.partitionMembers = new ArrayList<>();
        this.eventsToLog = new ArrayList<>();
        this.serverUrl = "Uninitialized";
        this.tfProbability = 0;
        this.requestsPerSecond = 0;
        this.experimentUnderProgress = false;
        this.experimentStatus = "Uninitialized";
        this.circuitBreakerType = "Closed";
        this.circuitBreakerParameters = new HashMap<>();
        this.minLatency = 0;
        this.failureInferenceTime = 0;
        this.notifyObservers();
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
        this.notifyObservers();
    }

    public void setNetworkPartition(boolean networkPartitioned, List<String> partitionMembers) {
        this.networkPartitioned = networkPartitioned;
        this.partitionMembers = partitionMembers;
        this.notifyObservers();
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
        this.notifyObservers();
    }

    public void setTfProbability(float tfProbability) {
        this.tfProbability = tfProbability;
        this.notifyObservers();
    }

    public void setExperimentSession(String experimentSession) {
        this.experimentSession = experimentSession;
        this.experimentStatus = "Scheduled";
    }

    public void setServerAvailable(boolean serverAvailable) {
        this.serverAvailable = serverAvailable;
        this.notifyObservers();
    }

    public void setRequestsPerSecond(float requestsPerSecond) {
        this.requestsPerSecond = requestsPerSecond;
        this.notifyObservers();
    }

    public void setExperimentUnderProgress(boolean experimentUnderProgress) {
        this.experimentUnderProgress = experimentUnderProgress;

        if(experimentUnderProgress) {
            this.experimentStatus = "In progress";
        } else {
            this.experimentStatus = "Completed";
        }

        this.notifyObservers();
    }

    public void setCircuitBreakerType(String circuitBreakerType) {
        this.circuitBreakerType = circuitBreakerType;
    }

    public void setCircuitBreakerParameters(Map<String, Integer> circuitBreakerParameters) {
        this.circuitBreakerParameters = circuitBreakerParameters;
    }

    public void setMinLatency(int minLatency) {
        this.minLatency = minLatency;
        this.notifyObservers();
    }

    public void setFailureInferenceTime(int failureInferenceTime) {
        this.failureInferenceTime = failureInferenceTime;
        this.notifyObservers();
    }

    public void setExperimentStatus(String experimentStatus) {
        this.experimentStatus = experimentStatus;
    }

    public void setEventsToLog(List<String> eventsToLog) {
        this.eventsToLog = eventsToLog;
    }
}
