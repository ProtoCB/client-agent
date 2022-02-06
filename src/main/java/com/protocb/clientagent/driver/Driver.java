package com.protocb.clientagent.driver;

import com.protocb.clientagent.AgentState;
import com.protocb.clientagent.proxy.Proxy;
import com.protocb.clientagent.circuitbreaker.CircuitBreaker;
import com.protocb.clientagent.circuitbreaker.CircuitBreakerFactory;
import com.protocb.clientagent.logger.Logger;
import com.protocb.clientagent.proxy.ResponseType;
import com.protocb.clientagent.requestpool.RequestPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Driver implements Runnable {

    @Autowired
    private Logger logger;

    @Autowired
    private Proxy proxy;

    @Autowired
    private RequestPool requestPool;

    @Autowired
    private AgentState agentState;

    @Autowired
    private CircuitBreakerFactory circuitBreakerFactory;

    @Override
    public void run() {
        try {

            requestPool.resetPool();
            CircuitBreaker circuitBreaker = circuitBreakerFactory.getCircuitBreaker(agentState.getCircuitBreakerType());
            circuitBreaker.initialize(agentState.getCircuitBreakerParameters());

            while(true) {

                if(!agentState.isAlive()) break;

                requestPool.fetchRequestOrWait();

                if(circuitBreaker.isCircuitBreakerOpen()) {
                    logger.log("CBOPEN", "Circuit Breaker Open");
                    continue;
                }

                ResponseType responseType = proxy.sendRequestToServer();

                if(responseType == ResponseType.SUCCESS) {
                    logger.log("S", "Successful Request");
                    circuitBreaker.registerSuccess();
                } else if(responseType == ResponseType.FAILURE) {
                    logger.log("F", "Failed Request");
                    circuitBreaker.registerFailure();
                }

            }

            circuitBreaker.reset();

        } catch (Exception e) {
            System.out.println("Driver Interrupted");
            System.out.println(e.getMessage());
            logger.logErrorEvent("Driver Interrupted - " + e.getMessage());
        }
    }
}
