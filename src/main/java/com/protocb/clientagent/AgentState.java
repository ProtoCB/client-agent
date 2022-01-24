package com.protocb.clientagent;

import com.protocb.clientagent.interaction.Observer;
import com.protocb.clientagent.interaction.Subject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
@Getter
@NoArgsConstructor
public class AgentState implements Subject {

    private String experimentSession;

    private boolean experimentUnderProgress;

    private boolean alive;

    private boolean networkPartitioned;

    private List<String> partitionMembers;

    private boolean serverAvailable;

    private String serverUrl;

    private float tfProbability;

    private ArrayList<Observer> observers;

    private float requestsPerSecond;

    @PostConstruct
    private void postContruct() {
        this.alive = false;
        this.networkPartitioned = false;
        this.experimentSession = "Uninitialized";
        this.partitionMembers = new ArrayList<>();
        this.serverUrl = "Uninitialized";
        this.tfProbability = 0;
        this.observers = new ArrayList<>();
        this.requestsPerSecond = 0;
        this.experimentUnderProgress = false;
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

    public void setAlive(boolean alive) {
        System.out.println("Alive = " + alive);
        this.alive = alive;
        this.notifyObservers();
    }

    public void setNetworkPartition(boolean networkPartitioned, List<String> partitionMembers) {
        System.out.println("Partition = " + networkPartitioned);
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
        System.out.println("Experiment = " + experimentUnderProgress);
        this.experimentUnderProgress = experimentUnderProgress;
        this.notifyObservers();
    }
}
