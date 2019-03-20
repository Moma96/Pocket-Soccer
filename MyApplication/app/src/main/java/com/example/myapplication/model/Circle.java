package com.example.myapplication.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Circle extends Thread implements Collidable {

    private static final String COLLISION_TAG = "Circle collision";
    private static final String STATE_TAG = "Circle state";

    private static final int MOVING_DELAY = 15; //15; //ms
    private static final double MOVING_INCREMENT = 0.03; //0.03;
    private static final double FRICTION_COEFFICIENT = 1;// 0.99;
    private static final double SPEED_ROUND_LIMIT = 0.5;

    private static ArrayList<Collidable> collidables = new ArrayList<>();
    private static ArrayList<Circle> circles = new ArrayList<>();
    private static Field field;

    private static int next_id = 0;
    private int id;

    private double mass;
    private double radius;
    private double img_radius;
    private Vector center;
    private Vector speed;

    private HashMap<String, Double> collision_in_process = new HashMap<>();
    private HashSet<Circle> collision_adjusted = new HashSet<>();

    private Boolean running = true;

    public static void setField(Field f) {
        synchronized(collidables) {
            field = f;
            collidables.notifyAll();
        }
    }

    public static ArrayList<Collidable> getCollidables() {
        return collidables;
    }

    public static ArrayList<Circle> getCircles() {
        return circles;
    }

    public static void addCollidable(Collidable collidable) {
        if (collidable == null) return;
        collidables.add(collidable);
        if (collidable instanceof Circle)
            circles.add((Circle)collidable);
    }

    public static Collidable getCollidable(Vector dot) {
        for (Collidable collidable : collidables) {
            if (collidable.isInside(dot)) {
                return collidable;
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
        synchronized (collidables) {
            collidables.notifyAll();
        }
    }

    public double getDistance(Circle circle) {
        return center.sub(circle.center).intensity() - (radius + circle.radius);
    }

    public boolean isInside(Vector dot) { //approximated
        if (dot.getX() >= center.getX() - radius
         && dot.getX() <= center.getX() + radius
         && dot.getY() >= center.getY() - radius
         && dot.getY() <= center.getY() + radius)
            return true;
        else return false;
    }

    private void circleCollision(Circle collided) {
        if (collided == null)
            return;

        if (collision_adjusted.contains(collided)) {
            collision_adjusted.remove(collided);
            Log.d(COLLISION_TAG, "Collision for " + this + " and " + collided + " already adjusted");
            return;
        }

        double distance = getDistance(collided);
        Double old_distance = collision_in_process.get(Integer.toString(collided.id));

        if (distance <= 0) {
            collided.collision_adjusted.add(this);

            if (old_distance == null || (old_distance != null && distance < old_distance)) {

                collision_in_process.put(Integer.toString(collided.id), distance);
                collided.collision_in_process.put(Integer.toString(id), distance);

                Circle old = new Circle(this);
                Circle oldCollided = new Circle(collided);
                collisionUpdateSpeed(oldCollided);
                collided.collisionUpdateSpeed(old);

                Log.d(COLLISION_TAG, this + " collided " + collided);

            } else if (old_distance != null) // (distance >= old_distance) {
                Log.d(COLLISION_TAG, this + " recovering from collision with " + collided);

        } else {
            if (old_distance != null) {
                collision_in_process.remove(Integer.toString(collided.id));
                collided.collision_in_process.remove(Integer.toString(id));
                Log.d(COLLISION_TAG, "Collision status for " + this + " and " + collided + " reset");
            }
        }
    }
    //*/

    /*public void preCollision(Collidable collided) {
        if (collision_adjusted.contains(collided)) {
            collision_adjusted.remove(collided);
            Log.d(COLLISION_TAG, "Collision for " + this + " and " + collided + " already adjusted");
            return;
        }
    }

    private void collision(Collidable collided) {
        if (collided == null)
            return;

        if (collision_adjusted.contains(collided.id)) {
            collision_adjusted.remove(collided.id);
            Log.d(COLLISION_TAG, "Collision for " + this + " and " + collided + " already adjusted");
            return;
        }

        double distance = collided.getDistance(this);
        Double old_distance = collision_in_process.get(Integer.toString(collided.id));

        if (distance <= 0) {
            collided.collision_adjusted.add(this); /////////////////////////////////////RESI OVO

            if (old_distance == null || (old_distance != null && distance < old_distance)) {

                collision_in_process.put(Integer.toString(collided.id), distance);
                collided.collision_in_process.put(Integer.toString(id), distance);

                Circle old = new Circle(this);
                Circle oldCollided = new Circle(collided);
                collisionUpdateSpeed(oldCollided);
                collided.collisionUpdateSpeed(old);

                Log.d(COLLISION_TAG, this + " collided " + collided);

            } else if (old_distance != null) // (distance >= old_distance) {
                Log.d(COLLISION_TAG, this + " recovering from collision with " + collided);

        } else {
            if (old_distance != null) {
                collision_in_process.remove(Integer.toString(collided.id));
                collided.collision_in_process.remove(Integer.toString(id));
                Log.d(COLLISION_TAG, "Collision status for " + this + " and " + collided + " reset");
            }
        }
    }
*/
    private void wallCollision(Wall wall) {
        if (wall == null) return;

        double distance = wall.getDistance(this);
        Double old_distance = collision_in_process.get(wall.toString());

        if (distance <= 0) {
            if (old_distance == null || (old_distance != null && distance < old_distance)) {

                collision_in_process.put(wall.toString(), distance);

                wall.collisionUpdateSpeed(this);

                Log.d(COLLISION_TAG, this + " collided " + wall.toString());

            } else if (old_distance != null) // (distance >= old_distance) {
                Log.d(COLLISION_TAG, this + " recovering from collision with " + wall.toString());

        } else {
            if (old_distance != null) {
                collision_in_process.remove(wall.toString());
                Log.d(COLLISION_TAG, "Collision status for " + this + " and " + wall.toString());
            }
        }
    }
    //*/

    public synchronized void collisionUpdateSpeed(Circle collided) {
        setSpeed(speed.sub(
                center.sub(collided.center).mul(
                        2 * collided.mass/(mass + collided.mass) *
                        ((speed.sub(collided.speed).dotProduct(center.sub(collided.center))) / Math.pow(center.sub(collided.center).intensity() , 2))
                )
        ));
    }

    private void checkCollision() {
        for (Collidable collidable : collidables) {
            if (this != collidable) {
                //circleCollision(collidable);
                if (collidable instanceof Circle) {
                    circleCollision((Circle) collidable);
                } else
                    wallCollision((Wall) collidable);
            }
        }
        /*
        for (Wall wall : field.getWalls())
            wallCollision(wall);*/
    }

    private void move() throws InterruptedException {

        while (speed.isZeroVector()) {
            if (running)
                Log.d(STATE_TAG, this + " stopped");
            running = false;
            synchronized (collidables) {
                collidables.wait();
            }
        }
        if (!running)
            Log.d(STATE_TAG, this + " is moving");
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

    public String toString() {
        return "Circle " + id;
    }

    @Override
    public void run() {
        setPriority(MAX_PRIORITY);
        try {
            while (field == null) {
                synchronized (collidables) {
                    collidables.wait();
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
