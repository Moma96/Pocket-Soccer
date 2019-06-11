package com.example.myapplication.model.collidables.active;

import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.myapplication.model.Active;
import com.example.myapplication.model.Vector;
import com.example.myapplication.model.collidables.Collidable;
import com.example.myapplication.model.collidables.Field;
import com.example.myapplication.model.collidables.inactive.InactiveObject;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;

public class Circle extends Active implements Collidable {

    private static final String COLLISION_TAG = "Collision";
    private static final String STATE_TAG = "Circle state";

    public static final int MOVING_DELAY = 15; //15; //ms
    protected static final double MOVING_INCREMENT = 0.03; //0.03;
    private static final double SPEED_ROUND_LIMIT = 0.05;

    protected double mass;
    protected Vector center;
    protected Vector speed;
    protected int moving_delay;

    private double radius;
    private double img_radius_coefficient;
    private double img_radius;

    private Field field;

    private HashMap<Collidable, Double> collision_in_process;
    private HashSet<Circle> collision_processed;

    private int id;

    public Circle(double mass, double radius, double img_radius_coefficient, int moving_delay, Vector center, Vector speed, @NotNull Field field) {
        this(mass, radius, img_radius_coefficient, moving_delay, center, speed, field, true, null, null);
    }

    public Circle(@NotNull Circle circle) {
        this(circle, true);
    }

    public Circle(double mass, double radius, double img_radius_coefficient, int moving_delay, @NotNull Vector center, @NotNull Field field) {
        this(mass, radius, img_radius_coefficient, moving_delay, center, new Vector(0, 0), field);
    }

    public Circle(@NotNull Circle circle, @NotNull Field field) {
        this(circle.mass, circle.radius, circle.img_radius_coefficient, circle.moving_delay, circle.center, circle.speed, field);
    }

    protected Circle(@NotNull Circle circle, boolean include) {
        this(circle.mass, circle.radius, circle.img_radius_coefficient, circle.moving_delay, circle.center, circle.speed, circle.field, include, circle.collision_in_process, circle.collision_processed);
    }

    protected Circle(double mass, double radius, double img_radius_coefficient, int moving_delay, @NotNull Vector center, Vector speed, @NotNull Field field, boolean include,
                     HashMap<Collidable, Double> collision_in_process, HashSet<Circle> collision_processed) {
        setField(field);
        setMass(mass);
        this.img_radius_coefficient = img_radius_coefficient;
        this.moving_delay = moving_delay;
        setRadius(radius);
        setCenter(center);
        id = field.getNextId();
        if (include) {
            field.addCollidable(this);
        }
        this.collision_in_process = (collision_in_process != null) ? new HashMap<>(collision_in_process) : new HashMap<Collidable, Double>();
        this.collision_processed = (collision_processed != null) ? new HashSet<>(collision_processed) : new HashSet<Circle>();

        setSpeed(speed);
    }

    private Circle getIdenticalNonInclusiveCopy() {
        synchronized (field) {
            Circle copy = getNonInclusiveCopy();
            copy.id = id;
            field.decrementId();
            return copy;
        }
    }

    public int getCircleId() {
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
        this.center = new Vector(center);
    }

    public synchronized Vector getSpeed() {
        return speed;
    }

    public void setSpeed(Vector speed) {
        synchronized (field) {
            synchronized (this) {
                this.speed = new Vector(speed);
                if (speed.intensity() < SPEED_ROUND_LIMIT) {
                    this.speed.clear();
                } else {
                    field.checkStarted(this);
                    notifyAll();
                }
            }
        }
    }

    public synchronized void setRadius(double radius) {
        this.radius = radius;
        img_radius = radius*img_radius_coefficient;
    }

    public synchronized double getRadius() {
        return radius;
    }

    public synchronized double getImgRadius() {
        return img_radius;
    }

    public synchronized boolean isInside(Vector dot) {
        return getDistance(dot) <= 0;
    }

    @Override
    public String toString() {
        return "Circle " + getCircleId();
    }

    public void draw(ImageView view) {
        if (view == null) return;
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.leftMargin = (int) (getCenter().getX() - getImgRadius());
        params.topMargin = (int) (getCenter().getY() - getImgRadius());
        view.setLayoutParams(params);
    }

    protected Circle getNonInclusiveCopy() {
        return new Circle(this, false);
    }

    @Override
    public double getDistance(@NotNull Circle circle) {
        synchronized (field) {
            return center.sub(circle.center).intensity() - (getRadius() + circle.getRadius());
        }
    }

    public double getDistance(@NotNull Vector dot) {
        synchronized (field) {
            return center.sub(dot).intensity() - getRadius();
        }
    }

    public void reset() {
        clearSpeed();
        collision_in_process.clear();
        collision_processed.clear();
    }

    public void clearSpeed() {
        setSpeed(new Vector(0, 0));
    }

    private void collision(@NotNull InactiveObject collided) {
        synchronized (field) {
            double distance = collided.getDistance(this);
            Double old_distance = collision_in_process.get(collided);
            if (distance <= 0) {

                collision_in_process.put(collided, distance);

                if (old_distance == null || (old_distance != null && distance < old_distance)) {
                    collided.collisionUpdateSpeed(this);
                    Log.d(COLLISION_TAG, this + " collided " + collided);

                } else // old_distance != null && distance => old_distance
                    Log.d(COLLISION_TAG, this + " recovering from collision with " + collided);

            } else {
                if (old_distance != null) {
                    collision_in_process.remove(collided);
                    Log.d(COLLISION_TAG, "Collision status for " + this + " and " + collided + " reset");
                }
            }
        }
    }

    private boolean checkCollisionProcessed(@NotNull Circle collided) {
        if (collision_processed.contains(collided)) {
            collision_processed.remove(collided);
            return true;
        }
        collided.collision_processed.add(this);
        return false;
    }

    private void collision(@NotNull Circle collided) {
        synchronized (field) {

            if (checkCollisionProcessed(collided))
                return;

            double distance = collided.getDistance(this);
            Double old_distance = collision_in_process.get(collided);
            if (distance <= 0) {

                collision_in_process.put(collided, distance);
                collided.collision_in_process.put(this, distance);

                if (old_distance == null || (old_distance != null && distance < old_distance)) {

                    Circle oldCollided = collided.getIdenticalNonInclusiveCopy();
                    this.collisionUpdateSpeed(collided);
                    oldCollided.collisionUpdateSpeed(this);
                    Log.d(COLLISION_TAG, this + " and " + collided + " collided");

                } else // old_distance != null && distance => old_distance
                    Log.d(COLLISION_TAG, this + " and " + collided + " recovering from collision");

            } else {
                if (old_distance != null) {

                    collision_in_process.remove(collided);
                    collided.collision_in_process.remove(this);
                    Log.d(COLLISION_TAG, "Collision status for " + this + " and " + collided + " reset");

                }
            }
        }
    }


    @Override
    public void collisionUpdateSpeed(@NotNull Circle collided) {
        if (speed.isZeroVector() && collided.speed.isZeroVector()) return;

        collided.setSpeed(collided.speed.sub(
                collided.center.sub(center).mul(
                        2 * mass / (collided.mass + mass) *
                                ((collided.speed.sub(speed).dotProduct(collided.center.sub(center))) / Math.pow(collided.center.sub(center).intensity(), 2))
                )
        ));
    }

    private void move() {
        synchronized (field) {
            setCenter(center.add(speed));
            setSpeed(speed.mul(1 - field.getFrictionCoefficient()));
        }
    }

    protected void work() {}

    protected void delay() throws InterruptedException {
        sleep(moving_delay);
    }

    private void checkCollision() {
        synchronized (field) {
            for (InactiveObject inactive : field.getInactives()) {
                collision(inactive);
            }

            for (Circle circle : field.getCircles()) {
                if (this != circle)
                    collision(circle);
            }
        }
    }

    private synchronized void checkSpeed() throws InterruptedException {
        if (speed.isZeroVector()) {
            Log.d(STATE_TAG, this + " stopped");
            wait();
        }
    }

    @Override
    protected void iterate() {
        try {
            checkSpeed();
            checkCollision();
            if (!speed.isZeroVector()) {
                move();
                work();
                delay();
            }
            field.barrier(this);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
