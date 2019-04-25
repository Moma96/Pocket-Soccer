package com.example.myapplication.model.collidables;

import android.util.Log;

import com.example.myapplication.model.Vector;
import com.example.myapplication.model.collidables.active.ActiveObject;
import com.example.myapplication.model.collidables.inactive.Wall;

import java.util.ArrayList;
import java.util.HashSet;

public abstract class Field {

    public static final String BARRIER_TAG = "Barrier";

    private int nextActiveId = 0;

    protected Wall walls[];
    protected double friction;

    private ArrayList<Collidable> collidables = new ArrayList<>();
    private ArrayList<ActiveObject> activeCollidables = new ArrayList<>();

    private HashSet<ActiveObject> moving = new HashSet<>();
    private HashSet<ActiveObject> barrier = new HashSet<>();


    public ArrayList<Collidable> getCollidables() {
        return collidables;
    }

    public ArrayList<ActiveObject> getActiveCollidables() {
        return activeCollidables;
    }

    public Wall[] getWalls() {
        return walls;
    }

    public void setWalls(Wall[] walls) {
        this.walls = walls;
    }

    public double getFrictionCoefficient() {
        return friction;
    }

    public void addCollidable(Collidable collidable) {
        if (collidable == null) return;

        synchronized (collidables) {
            collidables.add(collidable);
        }

        synchronized (activeCollidables) {
            if (collidable instanceof ActiveObject)
                activeCollidables.add((ActiveObject) collidable);
        }
    }

    public ActiveObject getActive(Vector dot) {
        if (dot == null) return null;

        synchronized (activeCollidables) {
            for (ActiveObject active : activeCollidables) {
                if (active.isInside(dot)) {
                    return active;
                }
            }
            return null;
        }
    }

    public int getNextId() {
        synchronized (activeCollidables) {
            return nextActiveId++;
        }
    }

    public void decrementId() {
        synchronized (activeCollidables) {
            nextActiveId--;
        }
    }

    public void barrier(ActiveObject active) throws InterruptedException {
        synchronized (barrier) {
            if (!barrier.contains(active)) {
                barrier.add(active);
                if (barrier.size() == moving.size()) {
                    barrier.notifyAll();
                    barrier.clear();
                } else
                    barrier.wait();
            } else {
                Log.e(BARRIER_TAG, "Barrier is not working properly");
            }
        }
    }

    public boolean barrierStopped(ActiveObject active) {
        synchronized (barrier) {
            if (active.getSpeed().isZeroVector() && moving.contains(active)) {
                moving.remove(active);
                return true;
            }
        }
        return false;
    }

    public boolean barrierStarted(ActiveObject active) {
        synchronized (barrier) {
            if (!moving.contains(active)) {
                moving.add(active);
                return true;
            }
        }
        return false;
    }
}
