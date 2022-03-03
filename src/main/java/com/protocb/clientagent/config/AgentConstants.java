package com.protocb.clientagent.config;

public class AgentConstants {
    public static final int BUFFER_SIZE = 10;
    public static final int HEARTBEAT_DELAY = 1500;
    public static final int HEARTBEAT_TIMEOUT = 750;
    public static final String NORTHBOUND_ENDPOINT = "/api/v1/northbound";
    public static final String GEDCB_ENDPOINT = "/api/v1/gedcb";
    public static final String SCHEDULING_EVENT_ID = "SCH";
    public static final String ERROR_EVENT_ID = "ERR";
    public static final int SCHEDULER_POOL_SIZE = 4;
}
