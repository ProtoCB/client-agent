package com.protocb.clientagent.scheduler.networkpartition;

import com.protocb.clientagent.AgentState;
import com.protocb.clientagent.dto.NetworkPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NetworkPartitionSchedulingTask implements Runnable {

    @Autowired
    private NetworkPartitionScheduler networkPartitionScheduler;

    @Autowired
    private AgentState agentState;

    @Override
    public void run() {
        NetworkPartition networkPartition = networkPartitionScheduler.getNextPartition();
        agentState.setNetworkPartition(networkPartition.isNetworkPartitioned(), networkPartition.getPartition());
    }
}
