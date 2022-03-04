package com.protocb.clientagent.driver;

import com.protocb.clientagent.AgentState;
import com.protocb.clientagent.proxy.Proxy;
import com.protocb.clientagent.circuitbreaker.CircuitBreaker;
import com.protocb.clientagent.circuitbreaker.CircuitBreakerFactory;
import com.protocb.clientagent.logger.Logger;
import com.protocb.clientagent.proxy.ResponseType;
import com.protocb.clientagent.proxy.ServerRequestBody;
import com.protocb.clientagent.requestpool.RequestPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;

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
    private ServerRequestBody serverRequestBody;

    @Autowired
    private CircuitBreakerFactory circuitBreakerFactory;

    @Override
    public void run() {
        requestPool.resetPool();
        CircuitBreaker circuitBreaker = circuitBreakerFactory.getCircuitBreaker(agentState.getCircuitBreakerType());
        circuitBreaker.reset();
        circuitBreaker.initialize(agentState.getCircuitBreakerParameters());
        try {

            while(true) {

                requestPool.fetchRequestOrWait();

                if(circuitBreaker.isCircuitBreakerOpen()) {
                    logger.log("CBOPEN", "Circuit Breaker Open");
                    continue;
                }

                long requestStartTime = Instant.now().toEpochMilli() % 1000000;
                serverRequestBody.setMinLatency(agentState.getMinLatency());
                serverRequestBody.setTimestamp(requestStartTime);

                ResponseType responseType = proxy.sendRequestToServer(serverRequestBody);

                long requestEndTime = Instant.now().toEpochMilli() % 1000000;

                if(responseType == ResponseType.SUCCESS) {
                    logger.log("S", Long.toString(requestEndTime - requestStartTime));
                    circuitBreaker.registerSuccess();
                } else if(responseType == ResponseType.FAILURE) {
                    logger.log("F", Long.toString(requestEndTime - requestStartTime));
                    circuitBreaker.registerFailure();
                }

            }
        } catch (Exception e) {
            System.out.println("Driver Interrupted");
            logger.logErrorEvent("Driver Interrupted - " + e.getMessage());
        }
    }
}
