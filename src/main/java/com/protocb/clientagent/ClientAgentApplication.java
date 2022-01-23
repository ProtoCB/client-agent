package com.protocb.clientagent;

import com.protocb.clientagent.interaction.Observer;
import com.protocb.clientagent.interaction.Subject;
import com.protocb.clientagent.requestgenerator.RequestGenerationAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class ClientAgentApplication {

    @Autowired
    private RequestGenerationAgent r;

	public static void main(String[] args) {
		SpringApplication.run(ClientAgentApplication.class, args);
	}

    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        r.generateRequestsAtDelay(300);
    }

}
