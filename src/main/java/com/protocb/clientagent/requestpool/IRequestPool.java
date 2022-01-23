package com.protocb.clientagent.requestpool;

public interface IRequestPool {
    void fetchRequestOrWait();
    void addRequestToPool();
}
