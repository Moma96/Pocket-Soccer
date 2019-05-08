package com.example.myapplication.model;

public abstract class Active extends Thread {

    private boolean active;

    public synchronized void terminate() {
        active = false;
    }

    protected abstract void iterate();

    protected void before() {}

    protected void after() {}

    @Override
    public void run() {
        synchronized (this) {
            active = true;
        }

        before();
        while (active) {
            iterate();
        }
        after();
    }
}
