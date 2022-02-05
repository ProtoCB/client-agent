package com.protocb.clientagent.circuitbreaker.gedcb;

import com.protocb.clientagent.circuitbreaker.CircuitBreaker;
import com.protocb.clientagent.circuitbreaker.CircuitBreakerState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.protocb.clientagent.circuitbreaker.CircuitBreakerState.*;
import static com.protocb.clientagent.circuitbreaker.CircuitBreakerState.CLOSED;

@Component
public class GEDCircuitBreaker implements CircuitBreaker {

    @Autowired
    private GEDCBClientRegister gedcbClientRegister;

    private CircuitBreakerState circuitBreakerState;

    private List<Integer> window;

    private int softFailureThreshold;

    private int hardFailureThreshold;

    private int suspicionSuccessThreshold;

    private int halfOpenFailureThreshold;

    private int halfOpenSuccessThreshold;

    private long openDuration;

    private long lastOpenAt;

    private int index;

    private void clearWindow() {
        for(int i = 0; i<window.size(); i++) {
            window.set(i, -1);
        }
    }

    private void monitorForStateTransition() {
        int failures = 0;
        int successes = 0;
        for(int i = 0; i<window.size(); i++) {
            if(window.get(i) == 2) failures++;
            if(window.get(i) == 1) successes++;
        }

        if(circuitBreakerState == CLOSED && failures > softFailureThreshold) {
            circuitBreakerState = SUSPICION;
            gedcbClientRegister.updateSelfOpinion(NOT_CLOSED);
            if(gedcbClientRegister.isConsensusOnSuspicion()) {
                circuitBreakerState = OPEN;
                clearWindow();
                lastOpenAt = Instant.now().toEpochMilli() % 100000;
            }
        } else if(circuitBreakerState == SUSPICION && (failures > hardFailureThreshold || gedcbClientRegister.isConsensusOnSuspicion())) {
            circuitBreakerState = OPEN;
            clearWindow();
            lastOpenAt = Instant.now().toEpochMilli() % 100000;
        } else if(circuitBreakerState == SUSPICION && successes > suspicionSuccessThreshold) {
            circuitBreakerState = CLOSED;
            clearWindow();
            gedcbClientRegister.updateSelfOpinion(CLOSED);
        } else if(circuitBreakerState == HALF_OPEN) {
            if(failures > halfOpenFailureThreshold) {
                circuitBreakerState = OPEN;
                clearWindow();
                lastOpenAt = Instant.now().toEpochMilli() % 100000;
            } else if(successes > halfOpenSuccessThreshold){
                circuitBreakerState = CLOSED;
                clearWindow();
                gedcbClientRegister.updateSelfOpinion(CLOSED);
            }
        }
    }

    private void registerResponse(int response) {
        window.set(index, response);
        index = (index + 1) % window.size();
        monitorForStateTransition();
    }

    @Override
    public void registerSuccess() {
        registerResponse(1);
    }

    @Override
    public void registerFailure() {
        registerResponse(2);
    }

    @Override
    public boolean isCircuitBreakerOpen() {
        System.out.println(circuitBreakerState + " | " + window.toString());
        if(circuitBreakerState == OPEN) {
            long timeElapsedSinceOpen = Instant.now().toEpochMilli() % 100000 - lastOpenAt;
            if(timeElapsedSinceOpen >= openDuration) {
                circuitBreakerState = HALF_OPEN;
                return false;
            }
            return true;
        } else if(circuitBreakerState == SUSPICION && gedcbClientRegister.isConsensusOnSuspicion()) {
            circuitBreakerState = OPEN;
            clearWindow();
            lastOpenAt = Instant.now().toEpochMilli() % 100000;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void initialize(Map<String, Integer> parameters) {
        softFailureThreshold = parameters.get("SFT");
        hardFailureThreshold = parameters.get("HFT");
        suspicionSuccessThreshold = parameters.get("SST");
        halfOpenFailureThreshold = parameters.get("HOFT");
        halfOpenSuccessThreshold = parameters.get("HOST");
        openDuration = parameters.get("OD");
        int windowSize = parameters.get("WS");
        lastOpenAt = 0;
        index = 0;
        window = Arrays.asList(new Integer[windowSize]);
        clearWindow();
        circuitBreakerState = CLOSED;

        int maxAge = parameters.get("maxAge");
        int gossipPeriod = parameters.get("gossipPeriod");
        int gossipCount = parameters.get("gossipCount");
        boolean pushPullGossip = parameters.get("pushPullGossip") == 1;

        gedcbClientRegister.initialize(maxAge, gossipPeriod, gossipCount, pushPullGossip);

    }

    @Override
    public void reset() {
        softFailureThreshold = 0;
        hardFailureThreshold = 0;
        suspicionSuccessThreshold = 0;
        halfOpenSuccessThreshold = 0;
        halfOpenFailureThreshold = 0;
        openDuration = 0;
        lastOpenAt = 0;
        index = 0;
        window = new ArrayList<>();
        circuitBreakerState = CLOSED;
        gedcbClientRegister.reset();
    }
}
