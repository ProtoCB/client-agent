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

            System.out.println("GOSS-1");

            if(!agentState.isAlive() || !agentState.getCircuitBreakerType().equals("GEDCB")) {
                throw new Exception("Cannot accept gossip messages");
            }

            System.out.println("GOSS-2");

            GossipSetState response = gedcbClientRegister.consumeIncomingInformation(gossipSetState);
            return ResponseEntity.ok().body(response);

        } catch(Exception e) {
            System.out.println(e.getMessage());
            logger.logErrorEvent("Recevied gossip message when not accepting");
            GossipSetState response = GossipSetState.builder()
                    .opinion(new HashMap<>())
                    .age(new HashMap<>())
                    .version(-1l)
                    .build();
            return ResponseEntity.ok().body(response);
        }
    }

    @PostMapping("/gsr")
    public ResponseEntity receiveGSRMessage(@RequestBody SetRevisionMessage setRevisionMessage) {
        try {

            System.out.println("GSR-1");

            if(!agentState.isAlive() || !agentState.getCircuitBreakerType().equals("GEDCB")) {
                throw new Exception("Cannot accept GSR messages");
            }

            System.out.println("GSR-2");

            gedcbClientRegister.processSetRevisionMessage(setRevisionMessage);

            return ResponseEntity.ok().body(null);

        } catch(Exception e) {
            System.out.println(e.getMessage());
            logger.logErrorEvent("Recevied GSR message when not accepting");
            return ResponseEntity.ok().body(null);
        }
    }

}


