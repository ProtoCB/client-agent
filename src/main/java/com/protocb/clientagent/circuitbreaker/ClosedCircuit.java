package com.protocb.clientagent.circuitbreaker;

import java.util.Map;

public class ClosedCircuit implements CircuitBreaker {
    @Override
    public void registerSuccess() {

    }

    @Override
    public void registerFailure() {

    }

    @Override
    public boolean isCircuitBreakerOpen() {
        return false;
    }

    @Override
    public void initialize(Map<String, Integer> parameters) {

    }
}
