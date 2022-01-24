package com.protocb.clientagent.driver;

import com.protocb.clientagent.Proxy;
import com.protocb.clientagent.requestpool.RequestPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Driver implements Runnable {

    @Autowired
    private Proxy proxy;

    @Autowired
    private RequestPool requestPool;

    @Override
    public void run() {
        try {
            while(true) {
                requestPool.fetchRequestOrWait();
                proxy.sendRequestToServer();
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            System.out.println("Driver Interrupted");
            //TODO: Log to file
        }
    }
}
