package com.example.myapplication.model.collidables;

import android.util.Log;

import com.example.myapplication.model.Vector;
import com.example.myapplication.model.collidables.active.Circle;
import com.example.myapplication.model.collidables.inactive.Wall;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;

public abstract class Field {

    public static final String BARRIER_TAG = "Barrier";

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

        synchronized (circles) {
            if (collidable instanceof Circle)
                circles.add((Circle) collidable);
        }
    }

    public Circle getActive(Vector dot) {
        if (dot == null) return null;

        synchronized (circles) {
            for (Circle active : circles) {
                if (active.isInside(dot)) {
                    return active;
                }
            }
            return null;
        }
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

    public void barrier(Circle active) throws InterruptedException {
        synchronized (barrier) {
            if (!barrier.contains(active)) {
                barrier.add(active);
                if (barrier.size() == moving.size()) {
                    barrier.notifyAll();
                    barrier.clear();
                    time++;
                    checkTime();
                } else
                    barrier.wait();
            } else {
                Log.e(BARRIER_TAG, "Barrier is not working properly");
            }
        }
    }

    public boolean checkStopped(@NotNull Circle active) {
        synchronized (barrier) {
            if (active.getSpeed().isZeroVector() && moving.contains(active)) {
                moving.remove(active);
                if (moving.size() == 0) {
                    allStopped();
                }
                return true;
            }
        }
        return false;
    }

    public boolean checkStarted(@NotNull Circle active) {
        synchronized (barrier) {
            if (!moving.contains(active)) {
                moving.add(active);
                return true;
            }
        }
        return false;
    }

    public int getTime() {
        return time;
    }

    protected void checkTime() {}

    public boolean allNotMoving() {
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
}
