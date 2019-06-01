package com.example.myapplication.model.collidables;

import android.util.Log;

import com.example.myapplication.model.collidables.active.Circle;
import com.example.myapplication.model.collidables.inactive.InactiveObject;
import com.example.myapplication.model.collidables.inactive.Wall;
import com.example.myapplication.model.Vector;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;

public abstract class Field {

    private static final String BARRIER_TAG = "Barrier";

    private Integer nextCircleId = 0;
    private int time = 0;

    protected Wall walls[];
    protected double friction;

    private ArrayList<InactiveObject> inactives = new ArrayList<>();
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

    public ArrayList<InactiveObject> getInactives() {
        return inactives;
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

        if (collidable instanceof Circle)
            circles.add((Circle) collidable);
        else if (collidable instanceof InactiveObject)
            inactives.add((InactiveObject) collidable);

    }

    public synchronized Circle getCircle(Vector dot) {
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

    public synchronized void barrier(@NotNull Circle circle) throws InterruptedException {

        if (!circle.getSpeed().isZeroVector()) {
            if (!moving.contains(circle)) {
                Log.e(BARRIER_TAG, "Barrier doesn't contain " + circle + ", barrier: " + barrier);
                return;
            }

            Log.d(BARRIER_TAG, "E " + circle); //+ " time: " + time);
            barrier.add(circle);
            Log.d(BARRIER_TAG, "barrier: " + barrier + " moving: " + moving);
            if (barrier.size() != moving.size()) {
                int oldTime = time;
                while (oldTime == time)
                    wait();
            }
        }
        ///////NAJBOLJE JE OVDE DA IZBACUJES IZ BARRIER CARE AKO JE SPEED == ZERO
        checkStopped(circle);

        if (barrier.size() == moving.size()) {
            barrierRelease();
        }
    }

    public synchronized void checkStarted(@NotNull Circle circle) {
        synchronized (circle) {
            if (!circle.getSpeed().isZeroVector() && circles.contains(circle) && !moving.contains(circle)) {
                moving.add(circle);
                Log.d(BARRIER_TAG, circle + " is moving");
            }
        }
    }

    private synchronized void checkStopped(@NotNull Circle circle) {
        if (circle.getSpeed().isZeroVector()) {
            if (moving.contains(circle)) {
                moving.remove(circle);
                if (barrier.contains(circle))
                    barrier.remove(circle);
                Log.d(BARRIER_TAG, circle + " stopped");
                if (moving.size() == 0) {
                    allStopped();
                }
            } else
                Log.e(BARRIER_TAG, "Circle stopped, but was not considered moving");
        }
    }

    public synchronized void barrierRelease() {
        time++;
        Log.d(BARRIER_TAG, "Released, time: " + time);
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

    public synchronized HashSet<Circle> getMoving() {
        return moving;
    }
}
