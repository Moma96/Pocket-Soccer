package com.example.myapplication.model.collidables;

import android.util.Log;

import com.example.myapplication.model.collidables.active.Circle;
import com.example.myapplication.model.collidables.inactive.InactiveObject;
import com.example.myapplication.model.collidables.inactive.Wall;
import com.example.myapplication.model.Vector;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public abstract class Field {

    private static final String BARRIER_TAG = "Barrier";
    private static final String STATE_TAG = "Circle state";

    private Integer nextCircleId = 0;
    private double time = 0;
    private double timeSpeed = 1;

    protected Wall walls[];
    protected final double friction;

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

        if (!circle.getSpeed().isZeroVector()) {
            if (!moving.contains(circle)) {
                Log.e(BARRIER_TAG, "Barrier doesn't contain " + circle + ", barrier: " + barrier);
                return;
            }

            Log.d(BARRIER_TAG, "E " + circle); //+ " time: " + time);
            barrier.add(circle);
            Log.d(BARRIER_TAG, "barrier: " + barrier + " moving: " + moving);
            if (barrier.size() != moving.size()) {
                double oldTime = time;
                while (oldTime == time)
                    wait();
            }
        }

        checkStopped(circle);

        if (barrier.size() == moving.size()) {
            /////
            calculateMinTime();
            /////
            barrierRelease();
        }
    }

    private synchronized void calculateMinTime() {
        timeSpeed = 1;
        Circle[] circles = new Circle[getCircles().size()];
        int c = 0;
        for (Circle circle : getCircles())
            circles[c++] = circle;

        for (int i = 0; i < circles.length; i++) {
            for (int j = i + 1; j < circles.length; j++) {
                circles[i].collision(circles[j]);
            }

            for (InactiveObject inactive : getInactives()) {
                circles[i].collision(inactive);
            }

        }

        for (int i = 0; i < circles.length - 1; i++) {
            for (int j = i + 1; j < circles.length; j++) {
                if (circles[i].getDistance(circles[j]) <= circles[i].getCollisionZoneRadius() + circles[j].getCollisionZoneRadius() - (circles[i].getRadius() + circles[j].getRadius())) {
                    double ttimeSpeed = circles[i].nextCollisionTime(circles[j]);
                    if (ttimeSpeed < timeSpeed) {
                        timeSpeed = ttimeSpeed;
                    }
                }
            }

            for (InactiveObject inactive : getInactives()) {
                if (inactive.getDistance(circles[i]) <= circles[i].getCollisionZoneRadius() - circles[i].getRadius()) {
                    double ttimeSpeed = inactive.nextCollisionTime(circles[i]);
                    if (ttimeSpeed < timeSpeed) {
                        timeSpeed = ttimeSpeed;
                    }
                }
            }
        }


        // Log.d(STATE_TAG, "time speed in next round will be " + timeSpeed);
    }

    public synchronized void checkStarted(@NotNull Circle circle) {
        synchronized (circle) {
            if (!circle.getSpeed().isZeroVector() && circles.contains(circle) && !moving.contains(circle)) {
                moving.add(circle);
                Log.d(BARRIER_TAG, circle + " entered moving");
                Log.d(STATE_TAG, circle + " is moving");
            }
        }
    }

    private synchronized void checkStopped(@NotNull Circle circle) {
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
        time++;
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

    //public synchronized HashSet<Circle> getMoving() {
      //  return moving;
    //}
}
