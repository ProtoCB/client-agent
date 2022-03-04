package com.protocb.clientagent.circuitbreaker;

import com.protocb.clientagent.circuitbreaker.gedcb.GEDCircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CircuitBreakerFactory {

    @Autowired
    private StaticCircuitBreaker staticCircuitBreaker;

    @Autowired
    private GEDCircuitBreaker gedCircuitBreaker;

    @Autowired
    private ClosedCircuit closedCircuit;

    public CircuitBreaker getCircuitBreaker(String type) {
        if(type.equals("Static")) {
            return staticCircuitBreaker;
        } else if(type.equals("GEDCB")) {
            return gedCircuitBreaker;
        } else {
            return closedCircuit;
        }
    }

    public void resetAllCircuitBreakers() {
        staticCircuitBreaker.reset();
        gedCircuitBreaker.reset();
        closedCircuit.reset();
    }


}
