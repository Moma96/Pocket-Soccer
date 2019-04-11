package com.example.myapplication.model.collidables.active;

import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.myapplication.model.Vector;
import com.example.myapplication.model.collidables.Collidable;
import com.example.myapplication.model.collidables.Field;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public abstract class ActiveObject extends Thread implements Collidable {

    private static final String COLLISION_TAG = "Active collision";
    private static final String STATE_TAG = "Active state";

    protected static final int MOVING_DELAY = 15; //15; //ms
    protected static final double MOVING_INCREMENT = 0.03; //0.03;
    private static final double FRICTION_COEFFICIENT = 0.01;// 0.01;
    private static final double SPEED_ROUND_LIMIT = 0.05;

    private static ArrayList<Collidable> collidables = new ArrayList<>();
    private static ArrayList<ActiveObject> activeCollidables = new ArrayList<>();
    private static HashSet<ActiveObject> moving = new HashSet<>();
    private static HashSet<ActiveObject> barrier = new HashSet<>();
    private static Field field;

    protected static int next_id = 0;
    protected int id;

    protected double mass;
    protected Vector center;
    protected Vector speed;

    private HashMap<String, Double> collision_in_process;
    private HashMap<Integer, ActiveObject> old;

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
        setMass(mass);
        setCenter(center);
        setSpeed(speed);
        collision_in_process = new HashMap<>();
        old = new HashMap<>();
    }

    public ActiveObject(ActiveObject active) {
        id = next_id++;
        if (active == null) return;
        setMass(active.mass);
        setCenter(new Vector(active.center));
        setSpeed(new Vector(active.speed));
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
        if (active == null) return 1;

        return center.sub(active.center).intensity() - (getRadius() + active.getRadius());
    }

    public double getDistance(Vector dot) {
        if (dot == null) return 1;

        return center.sub(dot).intensity() - getRadius();
    }

    public boolean isInside(Vector dot) {
        return getDistance(dot) <= 0;
    }

    public Collidable beforeCollision(ActiveObject active) {
        if (active == null) return this;

        ActiveObject old_collided = active.old.get(id);
        if (old_collided != null)
            return old_collided;
        else
            return this;
    }

    public void duringCollision(ActiveObject active) {
        if (active == null) return;

        ActiveObject old_collided = active.old.get(id);
        if (old_collided == null) {

            ActiveObject copy = active.getIdenticalCopy();
            old.put(active.id, copy);
            synchronized (this) {
                notifyAll();
            }

        } else
            active.old.remove(id);
    }

    private void collision(Collidable collided) {
        if (collided == null) return;

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

    private void checkCollision() {
        synchronized (activeCollidables) {
            for (Collidable collidable : collidables) {
                if (this != collidable) {
                    collision(collidable);
                }
            }
        }
    }

    private void checkSpeed() throws InterruptedException {
        if (speed.isZeroVector()) {
            if (moving.contains(this)) {
                synchronized (barrier) {
                    moving.remove(this);
                }
                Log.d(STATE_TAG, this + " stopped");
            }
            synchronized (this) {
                wait();
            }
        }
        if (!moving.contains(this)) {
            synchronized (this) {
                notifyAll();
            }
            synchronized (barrier) {
                moving.add(this);
            }
            Log.d(STATE_TAG, this + " is moving");
        }
    }

    private synchronized void move() {
        center = center.add(speed);
        setSpeed(speed.mul(1 - FRICTION_COEFFICIENT));
    }

    private void waitField() throws InterruptedException {
        while (field == null) {
            synchronized (activeCollidables) {
                activeCollidables.wait();
            }
        }
    }

    private void barrier() throws InterruptedException {
        synchronized (barrier) {
            if (!barrier.contains(this)) {
                barrier.add(this);
                if (barrier.size() == moving.size()) {
                    barrier.notifyAll();
                    barrier.clear();
                } else {
                    barrier.wait();
                }
            } else {
                Log.e(COLLISION_TAG, "Barrier is not working properly");
            }
        }
    }

    protected void work() {}

    @Override
    public void run() {
        //setPriority(MAX_PRIORITY);
        try {
            waitField();
            while (true) {  ///////////////NE MOZE OVAKO!!!!!!!!!!!!!!!!!!!!
                checkCollision();
                checkSpeed();
                if (!speed.isZeroVector()) {
                    move();
                    work();
                    sleep(MOVING_DELAY);
                    //barrier();
                };
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public abstract void draw(ImageView view);

    public abstract double getRadius();

    public abstract ActiveObject getCopy();
}
