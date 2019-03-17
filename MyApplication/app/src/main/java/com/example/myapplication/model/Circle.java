package com.example.myapplication.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class Circle extends Thread {

    private static final String COLLISION_TAG = "Circle collision";
    private static final String STATE_TAG = "Circle state";

    private static final int MOVING_DELAY = 15; //ms
    private static final double MOVING_INCREMENT = 0.03;
    private static final double FRICTION_COEFFICIENT = 0.99;
    private static final double SPEED_ROUND_LIMIT = 0.5;

    private static ArrayList<Circle> circles = new ArrayList<>();
    private static Field field;

    private static int next_id = 0;
    private int id;

    private double mass;
    private double radius;
    private double img_radius;
    private Vector center;
    private Vector speed;

    private HashMap<String, Double> collision_in_process = new HashMap<>(); //OVO TREBA BITI NIZ U KOJI CE BRZO DA SE UBACUJU I IZBACUJU ELEMENTI (HASH MAP)
    private Boolean running = true;

    public static void setField(Field f) {
        synchronized(circles) {
            field = f;
            circles.notifyAll();
        }
    }

    public static ArrayList<Circle> getCircles() {
        return circles;
    }

    public static void addCircle(Circle circle) {
        if (circle == null) return;
        circles.add(circle);
    }

    public static Circle getCircle(Vector dot) {
        for (Circle circle : circles) {
            if (circle.isInside(dot)) {
                return circle;
            }
        }
        return null;
    }

    public Circle(double mass, double radius, double img_radius, Vector center, Vector speed) {
        id = next_id++;
        this.mass = mass;
        this.radius = radius;
        this.img_radius = img_radius;
        this.center = center;
        this.speed = speed;
    }

    public Circle(Circle circle) {
        id = next_id++;
        if (circle == null) return;
        mass = circle.mass;
        radius = circle.radius;
        img_radius = circle.img_radius;
        center = new Vector(circle.center);
        speed = new Vector(circle.speed);
    }

    public Circle(double mass, double radius, double img_radius, Vector center) {
        this(mass, radius, img_radius, center, new Vector(0, 0));
    }

    public int getCircleId() {
        return id;
    }

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getImgRadius() {
        return img_radius;
    }

    public void setImgRadius(double img_radius) {
        this.img_radius = img_radius;
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
        synchronized (circles) {
            circles.notifyAll();
        }
    }

    public double getDistance(Circle circle) {
        return center.sub(circle.center).intensity() - (radius + circle.radius);
    }

    public double getDistance(Field.Wall wall) {
        switch(wall) {
            case TOP:
                return center.getY() - radius - field.top();
            case BOTTOM:
                return field.bottom() - (center.getY() + radius);
            case LEFT:
                return center.getX() - radius - field.left();
            case RIGHT:
                return field.right() - (center.getX() + radius);
        }
        return -1;
    }

    public boolean isInside(Vector dot) { //approximated
        if (dot.getX() >= center.getX() - radius && dot.getX() <= center.getX() + radius
         && dot.getY() >= center.getY() - radius && dot.getY() <= center.getY() + radius)
            return true;
        else return false;
    }

    private void circleCollision(Circle collided) {
        if (collided == null)
            return;

        double distance = getDistance(collided);
        Double old_distance = collision_in_process.get(Integer.toString(collided.id));

        if (distance <= 0) {
            if (old_distance == null || (old_distance != null && distance < old_distance)) {

                collision_in_process.put(Integer.toString(collided.id), distance);
                collided.collision_in_process.put(Integer.toString(id), distance);

                Circle old = new Circle(this);
                Circle oldCollided = new Circle(collided);
                collisionUpdateSpeed(oldCollided);
                collided.collisionUpdateSpeed(old);

                Log.d(COLLISION_TAG, "Circle " + id + " collided circle " + collided.id);

            } else if (old_distance != null) // (distance >= old_distance) {
                Log.d(COLLISION_TAG, "Circle " + id + " recovering from collision with circle " + collided.id);

        } else {
            if (old_distance != null) {
                collision_in_process.remove(Integer.toString(collided.id));
                Log.d(COLLISION_TAG, "Collision status for circle " + id + " and circle " + collided.id + " reset");
            }
        }
    }

    private void wallCollision(Field.Wall wall) {
        if (field == null) return;

        double distance = getDistance(wall);
        Double old_distance = collision_in_process.get(wall.toString());

        if (distance <= 0) {
            if (old_distance == null || (old_distance != null && distance < old_distance)) {

                collision_in_process.put(wall.toString(), distance);

                collisionUpdateSpeed(wall);

                Log.d(COLLISION_TAG, "Circle " + id + " collided " + wall.toString() + " wall");

            } else if (old_distance != null) // (distance >= old_distance) {
                Log.d(COLLISION_TAG, "Circle " + id + " recovering from collision with " + wall.toString() + " wall");

        } else {
            if (old_distance != null) {
                collision_in_process.remove(wall.toString());
                Log.d(COLLISION_TAG, "Collision status for circle " + id + " and " + wall.toString() + " wall reset");
            }
        }
    }

    private synchronized void collisionUpdateSpeed(Circle collided) {
        setSpeed(speed.sub(
                center.sub(collided.center).mul(
                        2 * collided.mass/(mass + collided.mass) *
                        ((speed.sub(collided.speed).dotProduct(center.sub(collided.center))) / Math.pow(center.sub(collided.center).intensity() , 2))
                )
        ));
    }

    private synchronized void collisionUpdateSpeed(Field.Wall wall) { /////////// UPOTREBI OVDE SETSPEED!!!!!!!!
        switch(wall) {
            case TOP:
            case BOTTOM:
                speed.setY(-speed.getY());
                break;
            case LEFT:
            case RIGHT:
                speed.setX(-speed.getX());
                break;
        }

    }

    private void checkCollision() {
        for (Circle circle : circles) {
            if (this != circle)
                circleCollision(circle);
        }

        for (Field.Wall wall : Field.Wall.values())
            wallCollision(wall);
    }

    private void move() throws InterruptedException {

        while (speed.isZeroVector()) {
            if (running)
                Log.d(STATE_TAG, "Circle " + id + " stopped");
            running = false;
            synchronized (circles) {
                circles.wait();
            }
        }
        if (!running)
            Log.d(STATE_TAG, "Circle " + id + " is moving");
        running = true;

        synchronized (circles) {
            center = center.add(speed.mul(MOVING_INCREMENT));
            //speed = speed.mul(FRICTION_COEFFICIENT);

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
            while (field == null) {
                synchronized (circles) {
                    circles.wait();
                }
            }

            while (true) {  ///////////////////////////NE MOZE OVAKO!!!!!!!!!!!!!!!!!!!!
                move();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
