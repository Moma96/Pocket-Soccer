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

    public static final double DISTANCE_PRECISSION = 1.0E-11;

    private static final String BARRIER_TAG = "Barrier";
    private static final String STATE_TAG = "Circle state";

    private Integer nextCircleId = 0;
    private double time = 0;
    private double timeSpeed = 1;

    protected Wall walls[];
    protected final double friction;

    private ArrayList<Collidable> collidables = new ArrayList<>();
    private ArrayList<InactiveObject> inactives = new ArrayList<>();
    private ArrayList<Circle> circles = new ArrayList<>();

    private HashSet<Circle> moving = new HashSet<>();
    private HashSet<Circle> barrier = new HashSet<>();

    public Field(final double friction) {
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

        collidables.add(collidable);
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

    public double getTimeSpeed() {
        return timeSpeed;
    }

    public void decrementId() {
        synchronized (nextCircleId) {
            nextCircleId--;
        }
    }

    public synchronized void barrier(@NotNull Circle circle) throws InterruptedException {

        if (!moving.contains(circle)) {
            Log.e(BARRIER_TAG, "Barrier doesn't contain " + circle + ", barrier: " + barrier);
            return;
        }

        Log.d(BARRIER_TAG,  circle + " entered barrier"); //+ " time: " + time);
        barrier.add(circle);
        Log.d(BARRIER_TAG, "barrier: " + barrier + " moving: " + moving);

        if (barrier.size() < moving.size()) {
            double oldTime = time;
            while (oldTime == time)
                wait();
        } else if (barrier.size() == moving.size()) {

            checkCollisions();
            calculateMinTime();

            barrierRelease();

        } else Log.e(BARRIER_TAG, "Barrier size bigger than moving size");
    }

    private synchronized void checkCollisions() {
        for (Circle circle: barrier) {
            for (Collidable collidable: collidables) {
                if (circle != collidable)
                    circle.collision(collidable);
            }
        }
    }

    private boolean timeSpeedInRange(double ts) {
        return ts < 1 - DISTANCE_PRECISSION && ts > DISTANCE_PRECISSION;
    }

    private synchronized void calculateMinTime() {
        timeSpeed = 1;
        for (Circle circle: moving) {

            double st = circle.stoppingTime();
            if (st < timeSpeed) timeSpeed = st;

            for (Collidable collidable: collidables) {
                if (circle != collidable) {
                    if (collidable.isClose(circle)) {

                        double ts = collidable.nextCollisionTime(circle);
                        if (timeSpeedInRange(ts) && ts < timeSpeed)
                            timeSpeed = ts;

                    }
                }
            }
        }

        if (timeSpeed < 1)
            Log.e(BARRIER_TAG, "time speed in next round will be " + timeSpeed);
    }

    public synchronized void checkStarted(@NotNull Circle circle) {
        if (!circle.getSpeed().isZeroVector() && !moving.contains(circle) && circles.contains(circle)) {
            moving.add(circle);

            Log.d(BARRIER_TAG, circle + " entered moving");
            Log.d(STATE_TAG, circle + " is moving");
        }
    }

    public synchronized void checkStopped(@NotNull Circle circle) {
        if (circle.getSpeed().isZeroVector()) {
            if (moving.contains(circle)) {
                moving.remove(circle);
                if (barrier.contains(circle))
                    barrier.remove(circle);

                Log.d(BARRIER_TAG, circle + " left moving");
                Log.d(STATE_TAG, circle + " stopped");

                if (moving.size() == 0) {
                    allStopped();
                }
            } else
                Log.e(BARRIER_TAG, "Circle stopped, but was not considered moving");
        }
    }

    public synchronized void barrierRelease() {
        time += timeSpeed;
        Log.d(BARRIER_TAG, "Released, time: " + time);
        checkTime();
        barrier.clear();
        notifyAll();
    }

    public synchronized void reset() {
        barrierRelease();
    }

    public double getTime() {
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
}
