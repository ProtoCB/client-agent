package com.protocb.clientagent.circuitbreaker;

public class CircuitBreakerFactory {
    public static CircuitBreaker getCircuitBreaker(String type) {
        if(type.equals("Static")) {
            return new StaticCircuitBreaker();
        } else {
            return new ClosedCircuit();
        }
    }
}
