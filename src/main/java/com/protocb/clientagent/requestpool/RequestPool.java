package com.protocb.clientagent.requestpool;

import com.protocb.clientagent.AgentState;
import com.protocb.clientagent.interaction.Observer;
import com.protocb.clientagent.logger.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.Semaphore;

import static com.protocb.clientagent.config.GlobalVariables.BUFFER_SIZE;

@Component
public class RequestPool implements Observer {

    @Autowired
    private Logger logger;

    @Autowired
    private AgentState agentState;

    private boolean enabled;

    private Semaphore availableRequests;

    private Semaphore emptyRequestSlots;

    @PostConstruct
    private void postContruct() {
        this.enabled = false;
        this.availableRequests = new Semaphore(0, true);
        this.emptyRequestSlots = new Semaphore(BUFFER_SIZE, true);
        agentState.registerObserver(this);
    }

    @PreDestroy
    private void preDestroy() {
        agentState.removeObserver(this);
    }

    public void fetchRequestOrWait(){
        try {

            availableRequests.acquire();
            emptyRequestSlots.release();
            logger.log("FETCH", "Fetched Request. " + availableRequests.availablePermits() + " requests left");

        } catch (Exception e) {
            System.out.println("Fetch request failed");
            logger.logErrorEvent("Fetching request failed - " + e.getMessage());
        }
    }

    @Override
    public void update() {
        this.enabled = agentState.isAlive();
    }

    public void addRequestToPool() {
        try {
            if(enabled) {
                emptyRequestSlots.acquire();
                availableRequests.release();
                System.out.println("Added Request");
                logger.log("REQ", "Added Request. " + availableRequests.availablePermits() + " requests available");
            } else {
                resetPool();
            }
        } catch (Exception e) {
            System.out.printf("Adding request failed");
            logger.logErrorEvent("Adding request failed - " + e.getMessage());
        }
    }

    public void resetPool() {
        try {
            availableRequests.drainPermits();
            int occupiedSlots = BUFFER_SIZE - emptyRequestSlots.availablePermits();
            if(occupiedSlots > 0) {
                emptyRequestSlots.release(occupiedSlots);
            } else {
                emptyRequestSlots.acquire(-1 * occupiedSlots);
            }
        } catch(Exception e) {
            logger.logErrorEvent("Resetting request pool failed - " + e.getMessage());

        }
    }
}
