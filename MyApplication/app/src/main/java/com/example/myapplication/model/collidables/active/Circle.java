package com.example.myapplication.model.collidables.active;

import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.myapplication.model.Active;
import com.example.myapplication.model.Vector;
import com.example.myapplication.model.collidables.Collidable;
import com.example.myapplication.model.collidables.Field;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class Circle implements Collidable {

    private static final String COLLISION_TAG = "Circle collision";

    protected static final double MOVING_INCREMENT = 0.03; //0.03;

    protected double mass;
    protected Vector center;

    protected Vector speed;
    private Vector friction;

    private double radius;
    private double img_radius_coefficient;
    private double img_radius;

    private Field field;

    private HashSet<Circle> collision_processed;

    private int id;

    public Circle(double mass, double radius, double img_radius_coefficient, Vector center, Vector speed, @NotNull Field field) {
        this(mass, radius, img_radius_coefficient, center, speed, field, true);
    }

    public Circle(@NotNull Circle circle) {
        this(circle, true);
    }

    public Circle(double mass, double radius, double img_radius_coefficient, @NotNull Vector center, @NotNull Field field) {
        this(mass, radius, img_radius_coefficient, center, new Vector(0, 0), field);
    }

    public Circle(@NotNull Circle circle, @NotNull Field field) {
        this(circle.mass, circle.radius, circle.img_radius_coefficient, circle.center, circle.speed, field);
    }

    protected Circle(@NotNull Circle circle, boolean include) {
        this(circle.mass, circle.radius, circle.img_radius_coefficient, circle.center, circle.speed, circle.field, include);
    }

    protected Circle(double mass, double radius, double img_radius_coefficient, @NotNull Vector center, Vector speed, @NotNull Field field, boolean include) {
        setField(field);
        setMass(mass);
        this.img_radius_coefficient = img_radius_coefficient;
        setRadius(radius);
        setCenter(center);
        id = field.getNextId();
        if (include) {
            field.addCollidable(this);
        }
        this.collision_processed = new HashSet<>();
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

                if (this.speed.inRange(-field.DISTANCE_PRECISSION, field.DISTANCE_PRECISSION)) {
                    field.checkStopped(this);
                    this.speed.clear();
                } else {
                    friction = speed.invert();
                    friction.scaleIntensity(field.getFrictionCoefficient());

                    field.checkStarted(this);

                    field.notifyAll();
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

    public synchronized double getCollisionZoneRadius() {
        return radius + speed.intensity();
    }

    public synchronized double getImgRadius() {
        return img_radius;
    }

    public synchronized boolean isInside(Vector dot) {
        return getDistance(dot) <= 0;
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

    public double getDistance(@NotNull Vector dot) {
        synchronized (field) {
            return center.sub(dot).intensity() - getRadius();
        }
    }

    public void reset() {
        clearSpeed();
        //collision_processed.clear(); --- DA LI IMA POTREBE ZA OVIM?
    }

    public void clearSpeed() {
        setSpeed(new Vector(0, 0));
    }

    @Override
    public double getDistance(@NotNull Circle circle) {
        synchronized (field) {
            return center.sub(circle.center).intensity() - (getRadius() + circle.getRadius());
        }
    }

    @Override
    public double nextCollisionTime(@NotNull Circle circle) {
        double R = radius + circle.radius;
        Vector deltaS = speed.sub(circle.speed);
        Vector deltaC = center.sub(circle.center);

        double a = deltaS.getX()*deltaS.getX() + deltaS.getY()*deltaS.getY();
        double b = 2*(deltaC.getX()*deltaS.getX() + deltaC.getY()*deltaS.getY());
        double c = deltaC.getX()*deltaC.getX() + deltaC.getY()*deltaC.getY() - R*R;
        double d = b*b - 4*a*c;
        if (d < 0 || a == 0)
            return Double.MAX_VALUE;

        double t = (-b - Math.sqrt(d))/(2*a);

        return t;
    }

    @Override
    public boolean checkCollisionProcessed(@NotNull Circle collided) {
        if (collision_processed.contains(collided)) {
            collision_processed.remove(collided);
            return true;
        }
        collided.collision_processed.add(this);
        return false;
    }

    @Override
    public void collisionHappened(Circle collided) {
        Circle oldCollided = getIdenticalNonInclusiveCopy();
        collided.collisionUpdateSpeed(this);
        oldCollided.collisionUpdateSpeed(collided);
    }

    @Override
    public boolean isClose(Circle circle) {
        return getDistance(circle) <= getCollisionZoneRadius() + circle.getCollisionZoneRadius() - (getRadius() + circle.getRadius());
    }

    public void collision(@NotNull Collidable collided) {
        synchronized (field) {

            if (collided.checkCollisionProcessed(this))
                return;

            double distance = collided.getDistance(this);
            if (distance >= -Field.DISTANCE_PRECISSION && distance <= Field.DISTANCE_PRECISSION) {

                Log.e(COLLISION_TAG, this + " and " + collided + " collided");

                collided.collisionHappened(this);

            } else if (distance < -Field.DISTANCE_PRECISSION) {
                Log.e(COLLISION_TAG, this + " went through " + collided + " distance: " + distance);
            }
        }
    }

    @Override
    public synchronized void collisionUpdateSpeed(@NotNull Circle collided) {

        collided.setSpeed(collided.speed.sub(
                collided.center.sub(center).mul(
                        2 * mass / (collided.mass + mass) *
                                ((collided.speed.sub(speed).dotProduct(collided.center.sub(center))) /
                                        Math.pow(collided.center.sub(center).intensity(), 2))
                )
        ));

    }

    public void move() {
        synchronized (field) {
            setCenter(center.add(speed.mul(field.getTimeSpeed())));

            //friction.mul(field.getTimeSpeed());
            friction.scaleIntensity(field.getFrictionCoefficient() * field.getTimeSpeed());
           // friction.mul(field.getTimeSpeed()); /// proveri zasto ovako ne radi
            setSpeed(speed.add(friction));

            work();
        }
    }

    public double stoppingTime() {
        if (friction.intensity() > speed.intensity()) {
            return speed.intensity() / friction.intensity();
        } else
            return 1;
    }

    protected void work() {}

    @Override
    public String toString() {
        return "Circle " + getCircleId();
    }
}
