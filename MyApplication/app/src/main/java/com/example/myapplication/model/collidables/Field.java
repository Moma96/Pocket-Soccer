package com.example.myapplication.model.collidables;

import android.util.Log;

import com.example.myapplication.model.collidables.active.Circle;
import com.example.myapplication.model.collidables.inactive.Wall;
import com.example.myapplication.model.Vector;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;

public abstract class Field {

    private static final String BARRIER_TAG = "Barrier";
    private static final String TIME_TAG = "Time";

    private Integer nextCircleId = 0;
    private int time = 0;

    protected Wall walls[];
    protected double friction;

    private ArrayList<Collidable> collidables = new ArrayList<>();
    private ArrayList<Circle> circles = new ArrayList<>();

    private HashSet<Circle> moving = new HashSet<>();
    private HashSet<Circle> barrier = new HashSet<>();

    public double getFriction() {
        return friction;
    }

    public void setFriction(double friction) {
        this.friction = friction;
    }

    public HashSet<Circle> getBarrier() {
        return barrier;
    }

    public ArrayList<Collidable> getCollidables() {
        return collidables;
    }

    public ArrayList<Circle> getCircles() {
        return circles;
    }

    public Wall[] getWalls() {
        return walls;
    }

    public double getFrictionCoefficient() {
        return friction;
    }

    public synchronized void addCollidable(Collidable collidable) {
        if (collidable == null) return;

        collidables.add(collidable);

        if (collidable instanceof Circle)
            circles.add((Circle) collidable);
    }

    public synchronized Circle getActive(Vector dot) {
        if (dot == null) return null;

        for (Circle active : circles) {
            if (active.isInside(dot)) {
                return active;
            }
        }
        return null;
    }

    public int getNextId() {
        synchronized (nextCircleId) {
            return nextCircleId++;
        }
    }

    public void decrementId() {
        synchronized (nextCircleId) {
            nextCircleId--;
        }
    }

    public synchronized void barrier(Circle active) throws InterruptedException {
        if (active.getSpeed().isZeroVector()) return;

        if (!barrier.contains(active)) {
            barrier.add(active);
            if (barrier.size() == moving.size()) {
                barrierRelease();
            } else {
                int oldTime = time;
                while(oldTime == time) {
                    wait();
                }
            }
        } else {
            Log.e(BARRIER_TAG, "Barrier is not working properly");
        }
    }

    public synchronized boolean checkStarted(@NotNull Circle active) {
        if (!moving.contains(active)) {
            moving.add(active);
            return true;
        }
        return false;
    }

    public synchronized boolean checkStopped(@NotNull Circle active) {
        if (active.getSpeed().isZeroVector() && moving.contains(active)) {
            moving.remove(active);
            if (moving.size() == 0) {
                allStopped();
            }
            return true;
        }
        return false;
    }

    public synchronized void barrierRelease() {
        time++;
        Log.d(TIME_TAG, "Time: " + time);
        checkTime();
        barrier.clear();
        notifyAll();
    }

    public int getTime() {
        return time;
    }

    protected void checkTime() {}

    public synchronized boolean allNotMoving() {
        for (Circle circle : circles) {
            if (!circle.getSpeed().isZeroVector()) {
                return false;
            }
        }
        return true;
    }

    protected void allStopped() {
        Log.d(BARRIER_TAG, "All stopped!");
    }

    public HashSet<Circle> getMoving() {
        return moving;
    }
}
