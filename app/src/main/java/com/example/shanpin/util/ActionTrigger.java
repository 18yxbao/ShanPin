package com.example.shanpin.util;

public class ActionTrigger {

    private long lastTriggerTime = 0;
    private final long controlTime;

    public ActionTrigger(long controlTime) {
        super();
        this.controlTime = controlTime;
    }

    public boolean canTrigger() {
        if (System.currentTimeMillis() - lastTriggerTime > controlTime) {
            lastTriggerTime = System.currentTimeMillis();
            return true;
        } else {
            lastTriggerTime = System.currentTimeMillis();
            return false;
        }
    }
}
