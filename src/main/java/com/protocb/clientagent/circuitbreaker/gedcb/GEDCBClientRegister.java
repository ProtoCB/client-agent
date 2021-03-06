package com.protocb.clientagent.circuitbreaker.gedcb;

import com.protocb.clientagent.circuitbreaker.CircuitBreakerState;
import com.protocb.clientagent.circuitbreaker.gedcb.dto.GossipSetState;
import com.protocb.clientagent.circuitbreaker.gedcb.dto.SetRevisionMessage;
import com.protocb.clientagent.config.EnvironementVariables;
import com.protocb.clientagent.logger.Logger;
import com.protocb.clientagent.proxy.Proxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.protocb.clientagent.circuitbreaker.CircuitBreakerState.*;

@Component
public class GEDCBClientRegister {

    @Autowired
    private Logger logger;

    @Autowired
    private ScheduledExecutorService scheduledExecutorService;

    @Autowired
    private Proxy proxy;

    @Autowired
    private EnvironementVariables environementVariables;

    private Integer maxAge;

    private boolean pushPullGossip;

    private Integer gossipCount;

    private Integer suspicionGossipCount;

    private Integer gossipMessageCount;

    private String selfId;

    private Long version;

    private Map<String, CircuitBreakerState> opinion;

    private Map<String, Integer> age;

    private ScheduledFuture gossipTask;

    public void initialize(Integer maxAge, Integer gossipPeriod, Integer gossipCount, Integer suspicionGossipCount, boolean pushPullGossip) {
        this.maxAge = maxAge;
        this.version = 0l;
        this.opinion = new HashMap<>();
        this.age = new HashMap<>();
        this.selfId = environementVariables.getAgentIp();
        this.pushPullGossip = pushPullGossip;
        this.gossipCount = gossipCount;
        gossipMessageCount = gossipCount;
        this.suspicionGossipCount = suspicionGossipCount;

        opinion.put(selfId, CLOSED);
        age.put(selfId, 0);

        if(gossipTask != null && !gossipTask.isCancelled() && !gossipTask.isDone()) {
            gossipTask.cancel(false);
        }

        gossipTask = scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                sendGossipMessages();
            }
        }, 0, gossipPeriod, TimeUnit.MILLISECONDS);

    }

    public void reset() {
        if(gossipTask != null && !gossipTask.isCancelled() && !gossipTask.isDone()) {
            gossipTask.cancel(false);
        }
    }

    private void sendGossipMessages() {

        List<String> clientIds = new ArrayList<>(this.opinion.keySet());
        List<String> selectedClients = new ArrayList<>();

        while(selectedClients.size() < gossipMessageCount && selectedClients.size() < clientIds.size() - 1) {
            int randomNumber = (int)(Math.random() * 1000);
            int index = randomNumber % clientIds.size();
            String clientId = clientIds.get(index);

            if(!clientId.equals(selfId) && !selectedClients.contains(clientId)) {
                selectedClients.add(clientId);
            }
        }

        this.incrementOpinionAge();

        for(String clientId : selectedClients) {
            GossipSetState gossipSetState = this.getGossipSetState();
            GossipSetState response = proxy.sendGossipMessage(clientId, gossipSetState);
            this.consumeIncomingInformation(response);
            logger.log("GSSENT", clientId);
        }
    }

    public synchronized GossipSetState consumeIncomingInformation(GossipSetState incomingState) {

        if(this.version > incomingState.getVersion()) {
            logger.log("GSREJECT", "Rejecting old version message - " + incomingState.getVersion());
            return new GossipSetState(-1l, new HashMap<>(), new HashMap<>());
        }

        if(this.version < incomingState.getVersion()) {

            Set<String> receivedClientIds = incomingState.getOpinion().keySet();
            Set<String> clientIds = new HashSet<>(this.opinion.keySet());

            for(String clientId : clientIds) {
                if(!receivedClientIds.contains(clientId)) {
                    this.opinion.remove(clientId);
                    this.age.remove(clientId);
                }
            }

            this.version = incomingState.getVersion();

        }

        Map<String, CircuitBreakerState> receivedOpinion = incomingState.getOpinion();
        Map<String, Integer> receivedAge = incomingState.getAge();

        for(String clientId : receivedOpinion.keySet()) {

            if(!this.opinion.keySet().contains(clientId)) {
                this.opinion.put(clientId, CLOSED);
                this.age.put(clientId, maxAge);
            }

            if(receivedAge.get(clientId) < this.age.get(clientId)) {
                this.age.put(clientId, receivedAge.get(clientId));
                this.opinion.put(clientId, receivedOpinion.get(clientId));
            }

        }

        GossipSetState updatedGossipSetState = new GossipSetState(this.version, this.opinion, this.age);

        logger.log("GSUPDT", updatedGossipSetState.toString());

        if(pushPullGossip) {
            return updatedGossipSetState;
        } else {
            return new GossipSetState(-1l, new HashMap<>(), new HashMap<>());
        }

    }

    public synchronized void updateSelfOpinion(CircuitBreakerState circuitBreakerState) {
        CircuitBreakerState newCircuitBreakerState = circuitBreakerState == CLOSED ? CLOSED : NOT_CLOSED;
        this.opinion.put(selfId, newCircuitBreakerState);
        this.age.put(selfId, 0);

        if(circuitBreakerState == SUSPICION) {
            gossipMessageCount = suspicionGossipCount;
        } else {
            gossipMessageCount = gossipCount;
        }

    }

    private void incrementOpinionAge() {
        for(String clientId : this.opinion.keySet()) {
            Integer currentAge = this.age.get(clientId);
            if(currentAge < maxAge) {
                this.age.put(clientId, currentAge + 1);
            }
        }

        this.age.put(selfId, 0);
    }

    private GossipSetState getGossipSetState() {
        return new GossipSetState(this.version, this.opinion, this.age);
    }

    public boolean isConsensusOnSuspicion() {

        if(this.opinion.size() == 1) return false;

        int majorityMark = this.opinion.size()/2;
        int suspicionCount = 0;

        for(String clientId : this.opinion.keySet()) {
            if(!this.age.get(clientId).equals(maxAge) && this.opinion.get(clientId) == NOT_CLOSED) {
                suspicionCount++;
            }
        }

        return suspicionCount > majorityMark;
    }

    public void processSetRevisionMessage(SetRevisionMessage setRevisionMessage) {

        if(this.version > setRevisionMessage.getVersion()) return;

        Map<String, CircuitBreakerState> opinion = new HashMap<>();
        Map<String, Integer> age = new HashMap<>();

        for(String clientId : setRevisionMessage.getClientIds()) {
            opinion.put(clientId, CLOSED);
            age.put(clientId, maxAge);
        }

        GossipSetState gossipSetState = new GossipSetState(setRevisionMessage.getVersion(), opinion, age);

        this.consumeIncomingInformation(gossipSetState);

        logger.log("GSR", getGossipSetState().toString());

    }

}
