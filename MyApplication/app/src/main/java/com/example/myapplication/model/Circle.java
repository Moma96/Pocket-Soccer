package com.example.myapplication.model;

import android.util.Log;

import java.util.ArrayList;

public class Circle extends Thread {

    private static final String COLLISION_TAG = "Circle collision";

    private static ArrayList<Circle> circles = new ArrayList<>();
    private static Field field;

    private static int next_id = 0;
    private int id;

    private double mass;
    private double radius;
    private Vector center;
    private Vector speed;

    private boolean collision_in_process = false;
    private Circle collided_in_process = null;
    //private String collided_in_progress = null;

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
            if (circle.inside(dot)) {
                return circle;
            }
        }
        return null;
    }

    public Circle(double mass, double radius, Vector center, Vector speed) {
        id = next_id++;
        this.mass = mass;
        this.radius = radius;
        this.center = center;
        this.speed = speed;
    }

    public Circle(Circle circle) {
        id = next_id++;
        if (circle == null) return;
        mass = circle.mass;
        radius = circle.radius;
        center = new Vector(circle.center);
        speed = new Vector(circle.speed);
    }

    public Circle(double mass, double radius, Vector center) {
        this(mass, radius, center, new Vector(0, 0));
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

    public boolean inside(Vector dot) {
        if (dot.getX() >= center.getX() - radius && dot.getX() <= center.getX() + radius
         && dot.getY() >= center.getY() - radius && dot.getY() <= center.getY() + radius)
            return true;
        else return false;
    }

    public boolean circleCollision(Circle collided) {

            if (collided == null)
                return false;

            if (center.sub(collided.center).intensity() <= radius + collided.radius) {

                if (collision_in_process && collided_in_process == collided) {
                    Log.d(COLLISION_TAG, "Circle " + id + " recovering from collision with circle " + collided.id);
                    return true;
                }

                collision_in_process = true;
                collided_in_process = collided;
                collided.collision_in_process = true;
                collided.collided_in_process = this;

                Circle old = new Circle(this);
                Circle oldCollided = new Circle(collided);

                collisionUpdateSpeed(oldCollided);
                collided.collisionUpdateSpeed(old);

                if (oldCollided.speed.isZeroVector()) {
                    circles.notifyAll();
                }

                Log.d(COLLISION_TAG, "Circle " + id + " collided circle " + collided.id);
                return true;
            }
            return false;
    }

    public void fieldCollision() {  ////////field collision recovery ODRADI
        if (field == null) return;
        if (center.getY() - radius <= field.top() || center.getY() + radius >= field.bottom())
            speed.setY( -speed.getY() );

        if (center.getX() - radius <= field.left() || center.getX() + radius >= field.right())
            speed.setX( -speed.getX() );
    }

    public synchronized void collisionUpdateSpeed(Circle collided) {
        speed = speed.sub(
                center.sub(collided.center).mul(
                        (2*collided.mass/(mass + collided.mass)) *
                        ((speed.sub(collided.speed).dotProduct(center.sub(collided.center))) / Math.pow(center.sub(collided.center).intensity() , 2))
                )
        );
    }

    public void move() {
        synchronized (circles) {
            center = center.add(speed.mul(0.015));
            // FRICTION
            //speed = speed.sub(speed.mul(0.00001*mass*radius)); //*radius*radius));
            speed = speed.mul(0.99);//00003*mass*radius);

            boolean collision_happened = false;
            for (Circle circle : circles) {
                if (this != circle) {
                    if (circleCollision(circle)) {
                        collision_happened = true;
                        break;
                    }
                }
            }

            if (collision_in_process && !collision_happened) {
                collision_in_process = false;
                collided_in_process = null;
                Log.d(COLLISION_TAG, "Collision status for circle " + id + " reset");
            }

            fieldCollision();
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

            while (true) {
                while (speed.intensity() == 0) {
                    synchronized (circles) {
                        circles.wait();
                    }
                }
                move();
                sleep(5);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
