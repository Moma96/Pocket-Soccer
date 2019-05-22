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

    public synchronized void barrier(Circle circle) throws InterruptedException {
        //if (!moving.contains(active)) return;

        //CHECK STARTED
        synchronized (circle) {
            if (!circle.getSpeed().isZeroVector() && !moving.contains(circle)) {
                moving.add(circle);
                Log.d(BARRIER_TAG, circle + " is moving");
            }
        }

        if (!circle.getSpeed().isZeroVector()) {
        //if (!barrier.contains(circle)) {
            Log.d(BARRIER_TAG, "E " + circle); //+ " time: " + time);
            barrier.add(circle);
            Log.d(BARRIER_TAG, "barrier: " + barrier + " moving: " + moving);
            //if (barrier.size() == moving.size()) {
            //    barrierRelease();
            //} else {
            if (barrier.size() != moving.size()) {
                int oldTime = time;
                while(oldTime == time) {
                    wait();
                }
            }
        } //else {
          //  Log.e(BARRIER_TAG, "Barrier is not working properly");
        //}

        ///at this point, barrier is empty!
        //CHECK STOPPED
        if (circle.getSpeed().isZeroVector() && moving.contains(circle)) {
            moving.remove(circle);
            Log.d(BARRIER_TAG, circle + " stopped");
            if (moving.size() == 0) {
                allStopped();
            }
        }

        /////RELEASING
        if (barrier.size() == moving.size()) {
            barrierRelease();
        }
        ///////NAJBOLJE JE OVDE DA IZBACUJES IZ BARRIER CARE AKO JE SPEED == ZERO
    }

    private synchronized void checkStarted(@NotNull Circle circle) {
        // speed check is not done because collided object is added
        // to moved list before his speed was updated!
        synchronized (circle) {
            if (!moving.contains(circle)) {
                moving.add(circle);
                Log.d(BARRIER_TAG, circle + " is moving");
                circle.notifyAll();
            }
        }
    }

    private synchronized void checkStopped(@NotNull Circle circle) {
        if (circle.getSpeed().isZeroVector() && moving.contains(circle)) {
            moving.remove(circle);
            if (barrier.contains(circle))
                barrier.remove(circle);
            if (moving.size() == 0) {
                allStopped();
            } else if (barrier.size() == moving.size()) {
                barrierRelease();
                //barrier.clear();
                //notifyAll();
            }
            Log.d(BARRIER_TAG, circle + " stopped");
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
