package com.example.myapplication.model.collidables.inactive;

import com.example.myapplication.model.Vector;
import com.example.myapplication.model.collidables.active.ActiveObject;

public class Dot extends InactiveObject {

    private double radius;
    private Vector center;

    public Dot(double x, double y) {
        this.radius = 0;
        this.center = new Vector(x, y);
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

    @Override
    public double getDistance(ActiveObject active) {
        return center.sub(active.getCenter()).intensity() - (radius + active.getRadius());
    }

    @Override
    public void collisionUpdateSpeed(ActiveObject collided) {
        if (collided == null) return;
        if (collided.getSpeed().isZeroVector()) return;

        collided.setSpeed(collided.getSpeed().sub(
           collided.getCenter().sub(center).mul(
                   2 * ((collided.getSpeed().dotProduct(collided.getCenter().sub(center))) / Math.pow(collided.getCenter().sub(center).intensity(), 2))
           )
        ));
    }
}
