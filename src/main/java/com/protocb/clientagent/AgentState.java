package com.protocb.clientagent;

import com.protocb.clientagent.interaction.Observer;
import com.protocb.clientagent.interaction.Subject;
import com.protocb.clientagent.requestpool.IRequestPool;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@Getter
@NoArgsConstructor
public class AgentState implements Subject {

    private boolean alive;

    private boolean networkPartitioned;

    private ArrayList<String> partitionMembers = new ArrayList<>();

    private String serverUrl;

    private float tfProbability;

    private ArrayList<Observer> observers = new ArrayList<>();

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
        this.alive = alive;
        this.notifyObservers();
    }

    public void setNetworkPartitioned(boolean networkPartitioned) {
        this.networkPartitioned = networkPartitioned;
        this.notifyObservers();
    }

    public void setPartitionMembers(ArrayList<String> partitionMembers) {
        this.partitionMembers = partitionMembers;
        this.notifyObservers();
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public void setTfProbability(float tfProbability) {
        this.tfProbability = tfProbability;
        this.notifyObservers();
    }

}
