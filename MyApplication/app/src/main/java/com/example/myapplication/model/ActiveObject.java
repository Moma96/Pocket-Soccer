package com.example.myapplication.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class ActiveObject extends Thread implements Collidable {
/*
    private static final String COLLISION_TAG = "Collision";
    private static final String STATE_TAG = "State";

    private static final int MOVING_DELAY = 15; //15; //ms
    private static final double MOVING_INCREMENT = 0.03; //0.03;
    private static final double FRICTION_COEFFICIENT = 1;// 0.99;
    private static final double SPEED_ROUND_LIMIT = 0.5;

    private static ArrayList<Collidable> collidables = new ArrayList<>();

    private double mass;
    private Vector center;
    private Vector speed;

    private HashMap<String, Double> collision_in_process = new HashMap<>();

    private Boolean running = true;

    public static void addCollidable(Collidable collidable) {
        if (collidable == null) return;
        collidables.add(collidable);
    }

    public static Collidable getCollidable(Vector dot) {
        for (Collidable collidable : collidables) {
            if (collidable.isInside(dot)) {
                return collidable;
            }
        }
        return null;
    }

    public ActiveObject(double mass, Vector center, Vector speed) {
        this.mass = mass;
        this.center = center;
        this.speed = speed;
    }

    public ActiveObject(ActiveObject activeObject) {
        if (activeObject == null) return;
        mass = activeObject.mass;
        center = new Vector(activeObject.center);
        speed = new Vector(activeObject.speed);
    }

    public ActiveObject(double mass, Vector center) {
        this(mass, center, new Vector(0, 0));
    }

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public Vector getCenter() {
        return center;
    }

    public void setCenter(Vector center) {
        this.center = center;
    }

    public Vector getSpeed() {
        return speed;
    }

    public void setSpeed(Vector speed) {
        this.speed = speed;
        synchronized (collidables) {
            collidables.notifyAll();
        }
    }

    private void checkCollision() {
        for (Collidable collidable : collidables) {
            if (this != collidable)
                collision(collidable);
        }
    }

    private void collision(Collidable collided) {
        if (collided == null) return;

        double distance = getDistance(collided);
        Double old_distance = collision_in_process.get(collided.toString());

        if (distance <= 0) {
            if (old_distance == null || (old_distance != null && distance < old_distance)) {

                collision_in_process.put(collided.toString(), distance);

                collisionUpdateSpeed(collided);

                Log.d(COLLISION_TAG, toString() + " collided " + collided.toString());

            } else if (old_distance != null) // (distance >= old_distance) {
                Log.d(COLLISION_TAG, toString() + " recovering from collision with " + collided.toString());

        } else {
            if (old_distance != null) {
                collision_in_process.remove(collided.toString());
                Log.d(COLLISION_TAG, "Collision status for " + toString() + " and " + collided.toString() + " reset");
            }
        }
    }

    private void move() throws InterruptedException {

        while (speed.isZeroVector()) {
            if (running)
                Log.d(STATE_TAG, toString() + " stopped");
            running = false;
            synchronized (collidables) {
                collidables.wait();
            }
        }
        if (!running)
            Log.d(STATE_TAG, toString() + " is moving");
        running = true;

        synchronized (collidables) {
            center = center.add(speed.mul(MOVING_INCREMENT));
            speed = speed.mul(FRICTION_COEFFICIENT);

            if (speed.intensity() < SPEED_ROUND_LIMIT)
                speed.clear();

            checkCollision();
        }
        sleep(MOVING_DELAY);
    }

    @Override
    public void run() {
        setPriority(MAX_PRIORITY);
        try {
            preRun();
            while (true) { //////////////NE MOZE OVAKO!!!!!!!!!!!!!!!!!!!!
                move();
            }
            //postRun();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected abstract void preRun();
       /*     while (field == null) {
        synchronized (circles) {
            circles.wait();
        }
    }*/

    protected abstract void postRun();

    protected abstract void collisionUpdateSpeed(Collidable collidable);

}
