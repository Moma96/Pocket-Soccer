package com.example.myapplication.model;

public abstract class Active extends Thread {

    private boolean terminated;
    private boolean active;

    public synchronized void terminate() {
        terminated = true;
    }

    public synchronized void inactive() {
        active = false;
    }

    public synchronized void active() {
        active = true;
        notifyAll();
    }

    private synchronized void waitActive() throws InterruptedException {
        while (!active) {
            wait();
        }
    }

    protected abstract void iterate();

    protected void before() {
    }

    protected void after() {
    }

    @Override
    public void run() {
        try {
            synchronized (this) {
                terminated = false;
                active = true;
            }

            before();
            while (!terminated) {
                iterate();
                waitActive();
            }
            after();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }
}