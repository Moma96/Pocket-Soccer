package com.example.myapplication.model;

import android.util.Log;

import java.util.ArrayList;

public class Circle extends Thread {

    private static final String COLLISION_TAG = "Circle collision";
    private static final String STATE_TAG = "Circle state";

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

    private String collision_in_process = null;
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
    }

    public boolean isInside(Vector dot) {
        if (dot.getX() >= center.getX() - radius && dot.getX() <= center.getX() + radius
         && dot.getY() >= center.getY() - radius && dot.getY() <= center.getY() + radius)
            return true;
        else return false;
    }

    private void checkCollision() {
        boolean collision_happened = false;
        for (Circle circle : circles) {
            if (this != circle) {
                if (circleCollision(circle)) {
                    collision_happened = true;
                    break;
                }
            }
        }

        if (fieldCollision()) {
            collision_happened = true;
        }

        if (collision_in_process != null && !collision_happened) {
            collision_in_process = null;
            Log.d(COLLISION_TAG, "Collision status for circle " + id + " reset");
        }
    }

    private boolean circleCollision(Circle collided) {

        if (collided == null)
            return false;

        if (center.sub(collided.center).intensity() <= radius + collided.radius) {
            if (collision_in_process == Integer.toString(collided.id))
                Log.d(COLLISION_TAG, "Circle " + id + " recovering from collision with circle " + collided.id);
            else {
                collision_in_process = Integer.toString(collided.id);
                collided.collision_in_process = Integer.toString(id);

                Circle old = new Circle(this);
                Circle oldCollided = new Circle(collided);

                collisionUpdateSpeed(oldCollided);
                collided.collisionUpdateSpeed(old);

                if (oldCollided.speed.isZeroVector()) {
                    circles.notifyAll();
                }

                Log.d(COLLISION_TAG, "Circle " + id + " collided circle " + collided.id);
            }
            return true;
        }

        return false;
    }

    private boolean fieldCollision() {
        if (field == null) return false;

        String collided = null;

        if (center.getY() - radius <= field.top())
            collided = "top";

        if (center.getY() + radius >= field.bottom())
            collided = "bottom";

        if (center.getX() - radius <= field.left())
            collided = "left";

        if (center.getX() + radius >= field.right())
            collided = "right";

        if (collided != null) {
            if (collision_in_process == collided) {
                Log.d(COLLISION_TAG, "Circle " + id + " recovering from collision with " + collided + " wall");
            } else {
                switch(collided) {
                    case "top":
                    case "bottom":
                        speed.setY(-speed.getY());
                        break;
                    case "left":
                    case "right":
                        speed.setX(-speed.getX());
                        break;
                }
                Log.d(COLLISION_TAG, "Circle " + id + " collided " + collided + " wall");
                collision_in_process = collided;
            }
            return true;
        }

        return false;
    }

    private synchronized void collisionUpdateSpeed(Circle collided) {
        speed = speed.sub(
                center.sub(collided.center).mul(
                        2 * collided.mass/(mass + collided.mass) *
                        ((speed.sub(collided.speed).dotProduct(center.sub(collided.center))) / Math.pow(center.sub(collided.center).intensity() , 2))
                )
        );
    }

    private void move() {
        synchronized (circles) {
            center = center.add(speed.mul(0.03));
            speed = speed.mul(0.99);

            if (speed.intensity() < SPEED_ROUND_LIMIT)
                speed.clear();

            checkCollision();
        }
    }

    @Override
    public void run() {
        try {
            while (field == null) {
                synchronized (circles) {
                    circles.wait();
                }
            }

            while (true) {  ///////////////////////////NE MOZE OVAKO!!!!!!!!!!!!!!!!!!!!
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
                move();
                sleep(15);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
