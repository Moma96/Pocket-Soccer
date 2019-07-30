package com.example.myapplication.model.collidables;

import android.util.Log;

import com.example.myapplication.model.Active;
import com.example.myapplication.model.collidables.active.Circle;
import com.example.myapplication.model.collidables.inactive.InactiveObject;
import com.example.myapplication.model.collidables.inactive.Wall;
import com.example.myapplication.model.Vector;
import com.example.myapplication.model.soccer.models.GoalPost;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

public abstract class Field extends Active implements Serializable {

    public static final double DISTANCE_PRECISSION = 1.0E-11;

    private static final String FIELD_TAG = "Field";
    private static final String STATE_TAG = "Circle state";

    public static final int MOVING_DELAY = 15; //15 ms

    private Integer nextCircleId = 0;
    private double time = 0;
    private double timeSpeed = 1;

    protected int moving_delay;

    transient protected Wall walls[];
    protected final double friction;

    transient private ArrayList<Collidable> collidables = new ArrayList<>();
    transient private ArrayList<InactiveObject> inactives = new ArrayList<>();
    transient private ArrayList<Circle> circles = new ArrayList<>();

    transient private HashSet<Circle> moving = new HashSet<>();

    public Field(double friction) {
        this(friction, MOVING_DELAY);
    }

    public Field(double friction, int moving_delay) {
        this.friction = friction;
        this.moving_delay = moving_delay;
    }

    public ArrayList<InactiveObject> getInactives() {
        return inactives;
    }

    public ArrayList<Circle> getCircles() {
        return circles;
    }

    public HashSet<Circle> getMoving() { return moving; }

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

    public synchronized boolean isMoving(Circle circle) {
        return moving.contains(circle);
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

    public double getTime() {
        return time;
    }

    public double getFriction() {
        return friction;
    }

    private boolean timeSpeedInRange(double ts) {
        return ts < 1 - DISTANCE_PRECISSION && ts > DISTANCE_PRECISSION;
    }

    public synchronized void checkStarted(@NotNull Circle circle) {
        if (!moving.contains(circle) && circles.contains(circle)) {
            moving.add(circle);

            Log.e(STATE_TAG, circle + " is moving");
        }
    }

    public synchronized void checkStopped(@NotNull Circle circle) {
        if (circle.getSpeed().isZeroVector() && moving.contains(circle) && circles.contains(circle)) {
            moving.remove(circle);

            if (moving.size() == 0) {
                allStopped();
            }

            Log.e(STATE_TAG, circle + " stopped");
        }
    }

    public synchronized boolean allNotMoving() {
        return moving.size() == 0;
    }

    protected void allStopped() {
        Log.d(FIELD_TAG, "All stopped!");
    }


    private synchronized void waitAtLeastOne() throws InterruptedException {
        while(allNotMoving()) {
            wait();
        }
    }

    private synchronized void checkCollisions() {
        ArrayList<Circle> tmoving = new ArrayList<>(moving);

        for (Circle circle: tmoving) {
            for (Collidable collidable: collidables) {
                if (circle != collidable)
                    circle.collision(collidable);
            }
        }
    }

    private synchronized void calculateMinTime() {
        timeSpeed = 1;
        for (Circle circle: moving) {

            double st = circle.stoppingTime();
            if (timeSpeedInRange(st) && st < timeSpeed)
                timeSpeed = st;

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
            Log.d(FIELD_TAG, "time speed is " + timeSpeed);
    }

    private void move() {
        ArrayList<Circle> tmoving = new ArrayList<>(moving);

        for (Circle circle: tmoving) {
            circle.move();
        }
    }

    private void updateTime() {
        time += timeSpeed;
        checkTime();
    }

    protected void checkTime() {}

    protected void delay() throws InterruptedException {
        sleep((int)((double)moving_delay * timeSpeed));
    }

    protected void iterationOver() {}

    @Override
    public void iterate() {
        try {
            waitAtLeastOne();
            checkCollisions();
            calculateMinTime();
            move();
            updateTime();
            iterationOver();
            delay();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
