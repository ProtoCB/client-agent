package com.protocb.clientagent.scheduler.requestrate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledExecutorService;

@Component
public class RequestRateScheduler {

    @Autowired
    private ScheduledExecutorService scheduledExecutorService;

//    @Autowired

}
