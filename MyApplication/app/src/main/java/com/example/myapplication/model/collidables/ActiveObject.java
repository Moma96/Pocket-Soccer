package com.example.myapplication.model.collidables;

import android.util.Log;

import com.example.myapplication.model.Vector;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class ActiveObject extends Thread implements Collidable {

    private static final String COLLISION_TAG = "Active collision";
    private static final String STATE_TAG = "Active state";

    private static final int MOVING_DELAY = 15; //15; //ms
    private static final double MOVING_INCREMENT = 0.03; //0.03;
    private static final double FRICTION_COEFFICIENT = 1;// 0.99;
    private static final double SPEED_ROUND_LIMIT = 0.5;

    private static ArrayList<Collidable> collidables = new ArrayList<>();
    private static ArrayList<ActiveObject> activeCollidables = new ArrayList<>();
    private static Field field;

    protected static int next_id = 0;
    protected int id;

    protected double mass;
    protected Vector center;
    protected Vector speed;

    private HashMap<String, Double> collision_in_process;
    private HashMap<Integer, ActiveObject> old;

    private Boolean running = true;

    public static void setField(Field f) {
        synchronized(activeCollidables) {
            field = f;
            activeCollidables.notifyAll();
        }
    }

    public ActiveObject getIdenticalCopy() {
        ActiveObject copy = getCopy();
        copy.id = id;
        return copy;
    }
    
    public static ArrayList<Collidable> getCollidables() {
        return collidables;
    }

    public static ArrayList<ActiveObject> getActiveCollidables() {
        return activeCollidables;
    }

    public static void addCollidable(Collidable collidable) {
        if (collidable == null) return;
        collidables.add(collidable);
        if (collidable instanceof ActiveObject)
            activeCollidables.add((ActiveObject) collidable);
    }

    public static Collidable getCollidable(Vector dot) { ////////////////////OBRATI PAZNJU
        for (Collidable collidable : collidables) {
            if (collidable.isInside(dot)) {
                return collidable;
            }
        }
        return null;
    }

    public static ActiveObject getActive(Vector dot) {
        for (ActiveObject active : activeCollidables) {
            if (active.isInside(dot)) {
                return  active;
            }
        }
        return null;
    }

    public ActiveObject(double mass, Vector center, Vector speed) {
        id = next_id++;
        this.mass = mass;
        this.center = center;
        this.speed = speed;
        collision_in_process = new HashMap<>();
        old = new HashMap<>();
    }

    public ActiveObject(ActiveObject active) {
        id = next_id++;
        if (active == null) return;
        mass = active.mass;
        center = new Vector(active.center);
        speed = new Vector(active.speed);
        collision_in_process = new HashMap<>(active.collision_in_process);
        old = new HashMap<>(active.old);
    }

    public ActiveObject(double mass, Vector center) {
        this(mass, center, new Vector(0, 0));
    }

    public int getActiveId() {
        return id;
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

    public synchronized Vector getSpeed() {
        return speed;
    }

    public synchronized void setSpeed(Vector speed) {
        this.speed = speed;
        if (speed.intensity() < SPEED_ROUND_LIMIT)
            speed.clear();
        notifyAll();
    }

    public double getDistance(ActiveObject active) {
        return center.sub(active.center).intensity() - (getRadius() + active.getRadius());
    }

    public double getDistance(Vector dot) {
        return center.sub(dot).intensity() - getRadius();
    }

    public boolean isInside(Vector dot) {
        return getDistance(dot) <= 0;
    }

    private ActiveObject preCollision(ActiveObject collided) {
        ActiveObject old_collided = old.get(collided.id);

        if (old_collided != null) {
            old.remove(collided.id);
            return old_collided;
        } else {
            ActiveObject copy = getIdenticalCopy();
            collided.old.put(id, copy);
            synchronized (collided) {
                collided.notifyAll();
            }
            return collided;
        }
    }

    private void collision(Collidable collided) {
        if (collided == null) return;

        if (collided instanceof ActiveObject) {
            collided = preCollision((ActiveObject) collided);
        }

        double distance = collided.getDistance(this);
        Double old_distance = collision_in_process.get(collided.toString());

        if (distance <= 0) {

            if (old_distance == null || (old_distance != null && distance < old_distance)) {

                collision_in_process.put(collided.toString(), distance);

                collided.collisionUpdateSpeed(this);

                Log.d(COLLISION_TAG, this + " collided " + collided);

            } else if (old_distance != null) // (distance >= old_distance) {
                Log.d(COLLISION_TAG, this + " recovering from collision with " + collided);

        } else {
            if (old_distance != null) {
                collision_in_process.remove(collided.toString());
                Log.d(COLLISION_TAG, "Collision status for " + this + " and " + collided + " reset");
            }
        }
    }

    public synchronized void collisionUpdateSpeed(ActiveObject collided) {
        if (speed.isZeroVector() && collided.speed.isZeroVector()) return;

        if (collided instanceof Circle) {
            collided.setSpeed(collided.speed.sub(
                    collided.center.sub(center).mul(
                            2 * mass / (collided.mass + mass) *
                                    ((collided.speed.sub(speed).dotProduct(collided.center.sub(center))) / Math.pow(collided.center.sub(center).intensity(), 2))
                    )
            ));
        }
    }

    private void checkCollision() {
        synchronized (activeCollidables) {
            for (Collidable collidable : collidables) {
                if (this != collidable) {
                    collision(collidable);
                }
            }
        }
    }

    private synchronized  void checkSpeed() throws InterruptedException {
        if (speed.isZeroVector()) { //////////////////////POPRAVI OVO
            if (running) {
                Log.d(STATE_TAG, this + " stopped");
                running = false;
            }
            wait();
        }
        if (!running) {
            Log.d(STATE_TAG, this + " is moving");
            running = true;
        }
    }

    private synchronized void move() {
        center = center.add(speed.mul(MOVING_INCREMENT));
        speed = speed.mul(FRICTION_COEFFICIENT);
    }

    private void waitField() throws InterruptedException {
        while (field == null) {
            synchronized (activeCollidables) {
                collidables.wait();
            }
        }
    }

    @Override
    public void run() {
        setPriority(MAX_PRIORITY);
        try {
            waitField();

            while (true) {  ///////////////////////////NE MOZE OVAKO!!!!!!!!!!!!!!!!!!!!
                checkCollision();
                checkSpeed();
                if (!speed.isZeroVector()) {
                    move();
                    sleep(MOVING_DELAY);
                };
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String toString() {
        return "Circle " + id;
    }

    public abstract double getRadius(); ///////////////////////////////NAZOVI DRUGACIJE

    public abstract ActiveObject getCopy();
}
