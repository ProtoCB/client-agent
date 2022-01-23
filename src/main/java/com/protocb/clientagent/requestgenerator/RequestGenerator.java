package com.protocb.clientagent.requestgenerator;

import com.protocb.clientagent.requestpool.RequestPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RequestGenerator implements Runnable {

    @Autowired
    private RequestPool requestPool;

    @Override
    public void run() {
        requestPool.addRequestToPool();
    }
}
