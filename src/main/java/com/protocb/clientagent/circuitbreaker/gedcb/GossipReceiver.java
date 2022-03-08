package com.protocb.clientagent.circuitbreaker.gedcb;

import com.protocb.clientagent.AgentState;
import com.protocb.clientagent.circuitbreaker.gedcb.dto.GossipSetState;
import com.protocb.clientagent.circuitbreaker.gedcb.dto.SetRevisionMessage;
import com.protocb.clientagent.logger.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

import static com.protocb.clientagent.config.AgentConstants.GEDCB_ENDPOINT;

@RestController
@RequestMapping(GEDCB_ENDPOINT)
public class GossipReceiver {

    @Autowired
    private AgentState agentState;

    @Autowired
    private GEDCBClientRegister gedcbClientRegister;

    @Autowired
    private Logger logger;

    @PostMapping("/gossip")
    public ResponseEntity receiveGossipMessage(@RequestBody GossipSetState gossipSetState) {
        try {

            if(!agentState.isAlive() || !agentState.getCircuitBreakerType().equals("GEDCB")) {
                throw new Exception("Cannot accept gossip messages");
            }

            GossipSetState response = gedcbClientRegister.consumeIncomingInformation(gossipSetState);
            return ResponseEntity.ok().body(response);

        } catch(Exception e) {
            System.out.println(e.getMessage());
            logger.logErrorEvent("Recevied gossip message when not accepting");
            GossipSetState response = new GossipSetState(-1l, new HashMap<>(), new HashMap<>());
            return ResponseEntity.ok().body(response);
        }
    }

    @PostMapping("/gsr")
    public ResponseEntity receiveGSRMessage(@RequestBody SetRevisionMessage setRevisionMessage) {
        try {

            if(!agentState.isAlive() || !agentState.getCircuitBreakerType().equals("GEDCB")) {
                throw new Exception("Cannot accept GSR messages");
            }

            gedcbClientRegister.processSetRevisionMessage(setRevisionMessage);

            return ResponseEntity.ok().body(null);

        } catch(Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            logger.logErrorEvent("Exception in GSR - " + e.getMessage());
            return ResponseEntity.ok().body(null);
        }
    }

}


