package com.example.myapplication.model.collidables.active;

import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.myapplication.model.Vector;
import com.example.myapplication.model.collidables.Collidable;
import com.example.myapplication.model.collidables.Field;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public abstract class ActiveObject extends Thread implements Collidable {

    private static final String COLLISION_TAG = "Active collision";
    private static final String STATE_TAG = "Active state";

    protected static final int MOVING_DELAY = 15; //15; //ms
    protected static final double MOVING_INCREMENT = 0.03; //0.03;
    private static final double SPEED_ROUND_LIMIT = 0.05;

    protected double mass;
    private double radius;
    protected Vector center;
    protected Vector speed;

    private boolean active;
    private Field field;

    private HashMap<String, Double> collision_in_process;
    private HashMap<Integer, ActiveObject> old;

    private int id;

    public ActiveObject(double mass, Vector center, Vector speed, @NotNull Field field) {
        setMass(mass);
        setCenter(center);
        setSpeed(speed);
        setField(field);
        collision_in_process = new HashMap<>();
        old = new HashMap<>();
        id = field.getNextId();
    }

    public ActiveObject(ActiveObject active) {
        if (active == null) return;
        setMass(active.mass);
        setCenter(new Vector(active.center));
        setSpeed(new Vector(active.speed));
        setField(active.getField());
        collision_in_process = new HashMap<>(active.collision_in_process);
        old = new HashMap<>(active.old);
        id = field.getNextId();
    }

    public ActiveObject(double mass, Vector center, Field field) {
        this(mass, center, new Vector(0, 0), field);
    }

    public synchronized ActiveObject getIdenticalCopy() {
        ActiveObject copy = getCopy();
        copy.id = id;
        field.decrementId();
        return copy;
    }

    public int getActiveId() {
        return id;
    }

    public synchronized double getMass() {
        return mass;
    }

    public synchronized void setMass(double mass) {
        this.mass = mass;
    }

    public synchronized Field getField() {
        return field;
    }

    public synchronized void setField(Field field) {
        this.field = field;
    }

    public synchronized Vector getCenter() {
        return center;
    }

    public synchronized void setCenter(Vector center) {
        this.center = center;
    }

    public synchronized Vector getSpeed() {
        return speed;
    }

    public synchronized void setSpeed(Vector speed) {
        this.speed = speed;
        if (speed.intensity() < SPEED_ROUND_LIMIT)
            speed.clear();
        notifyAll();
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getRadius() {
        return radius;
    }

    public synchronized double getDistance(ActiveObject active) {
        if (active == null) return 1;

        return center.sub(active.center).intensity() - (getRadius() + active.getRadius());
    }

    public synchronized double getDistance(Vector dot) {
        if (dot == null) return 1;

        return center.sub(dot).intensity() - getRadius();
    }

    public boolean isInside(Vector dot) {
        return getDistance(dot) <= 0;
    }

    public void clearSpeed() {
        setSpeed(new Vector(0, 0));
    }

    public synchronized Collidable beforeCollision(ActiveObject active) {
        if (active == null) return this;

        ActiveObject old_collided = active.old.get(id);
        if (old_collided != null)
            return old_collided;
        else
            return this;
    }

    public synchronized void duringCollision(ActiveObject active) {
        if (active == null) return;

        ActiveObject old_collided = active.old.get(id);
        if (old_collided == null) {

            ActiveObject copy = active.getIdenticalCopy();
            old.put(active.id, copy);
            notifyAll();

        } else
            active.old.remove(id);
    }

    private void collision(Collidable collided) {
        if (collided == null) return;

        synchronized (field.getActiveCollidables()) {
            collided = collided.beforeCollision(this);
            double distance = collided.getDistance(this);
            Double old_distance = collision_in_process.get(collided.toString());

            if (distance <= 0) {
                collided.duringCollision(this);
                collision_in_process.put(collided.toString(), distance);

                if (old_distance == null || (old_distance != null && distance < old_distance)) {
                    collided.collisionUpdateSpeed(this);
                    Log.d(COLLISION_TAG, this + " collided " + collided);

                } else if (old_distance != null)// (distance >= old_distance) {
                    Log.d(COLLISION_TAG, this + " recovering from collision with " + collided);

            } else {
                if (old_distance != null) {
                    collision_in_process.remove(collided.toString());
                    Log.d(COLLISION_TAG, "Collision status for " + this + " and " + collided + " reset");
                }
            }
        }
    }

    public synchronized void collisionUpdateSpeed(ActiveObject collided) {
        if (collided == null) return;
        if (speed.isZeroVector() && collided.speed.isZeroVector()) return;

        collided.setSpeed(collided.speed.sub(
                collided.center.sub(center).mul(
                        2 * mass / (collided.mass + mass) *
                                ((collided.speed.sub(speed).dotProduct(collided.center.sub(center))) / Math.pow(collided.center.sub(center).intensity(), 2))
                )
        ));
    }

    private void checkCollision() {
        for (Collidable collidable : field.getCollidables()) {
            if (this != collidable) {
                collision(collidable);
            }
        }
    }

    private synchronized void checkSpeed() throws InterruptedException {

        if (field.barrierStopped(this)) {
            Log.d(STATE_TAG, this + " stopped");
        }

        if (speed.isZeroVector()) {
            wait();
        }

        if (field.barrierStarted(this)) {
            Log.d(STATE_TAG, this + " is moving");
        }
    }

    private synchronized void move() {
        center = center.add(speed);
        setSpeed(speed.mul(1 - field.getFrictionCoefficient()));
    }

    public synchronized void finish() {
        active = false;
    }

    protected void work() {}

    @Override
    public void run() {
        try {
            active = true;
            while (active) {
                checkCollision();
                checkSpeed();
                if (!speed.isZeroVector()) {
                    move();
                    work();
                    sleep(MOVING_DELAY);
                    field.barrier(this);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public abstract void draw(ImageView view);

    public abstract ActiveObject getCopy();
}
