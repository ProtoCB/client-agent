package com.protocb.clientagent.circuitbreaker;

import lombok.ToString;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.protocb.clientagent.circuitbreaker.CircuitBreakerState.*;

public class StaticCircuitBreaker implements CircuitBreaker {

    private CircuitBreakerState circuitBreakerState;

    private List<Integer> window;

    private int failureThreshold;

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

        if(circuitBreakerState == CLOSED) {
            if(failures > failureThreshold) {
                circuitBreakerState = OPEN;
                clearWindow();
                lastOpenAt = Instant.now().toEpochMilli() % 100000;
            }
        } else if(circuitBreakerState == HALF_OPEN) {
            if(failures > halfOpenFailureThreshold) {
                circuitBreakerState = OPEN;
                clearWindow();
                lastOpenAt = Instant.now().toEpochMilli() % 100000;
            } else if(successes > halfOpenSuccessThreshold){
                circuitBreakerState = CLOSED;
                clearWindow();
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
        } else {
            return false;
        }
    }

    @Override
    public void initialize(Map<String, Integer> parameters) {
        failureThreshold = parameters.get("FT");
        halfOpenFailureThreshold = parameters.get("HOFT");
        halfOpenSuccessThreshold = parameters.get("HOST");
        openDuration = parameters.get("OD");
        int windowSize = parameters.get("WS");
        lastOpenAt = 0;
        index = 0;
        window = Arrays.asList(new Integer[windowSize]);
        clearWindow();
        circuitBreakerState = CLOSED;
    }
}
