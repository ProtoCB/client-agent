package com.protocb.clientagent.driver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Component
public class DriverCoordinator {

    @Autowired
    private ScheduledExecutorService scheduledExecutorService;

    @Autowired
    private Driver driver;

    private ScheduledFuture driverTask;

    public void enableDriver() {
        disableDriver();
        driverTask = scheduledExecutorService.schedule(driver, 0, TimeUnit.MILLISECONDS);
    }

    public void disableDriver() {
        if(isDriverActive()) {
            driverTask.cancel(true);
        }
    }

    private boolean isDriverActive() {
        return driverTask != null && !driverTask.isCancelled();
    }

}
