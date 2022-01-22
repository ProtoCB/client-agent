package com.protocb.clientagent;

import com.protocb.clientagent.interaction.Observer;
import com.protocb.clientagent.interaction.Subject;
import org.springframework.beans.factory.annotation.Autowired;

public class DummyObserver implements Observer {

    @Autowired
    private Subject subject;

    public DummyObserver(Subject subject) {
        this.subject = subject;
        subject.registerObserver(this);
    }

    @Override
    public void update() {
        System.out.println("Updated");
    }
}
