package com.example.myapplication.model.collidables.inactive;

import com.example.myapplication.model.Vector;
import com.example.myapplication.model.collidables.active.Circle;

public class Dot extends InactiveObject {

    private Vector center;

    public Dot(double x, double y) {
        this.center = new Vector(x, y);
    }

    public Vector getCenter() {
        return center;
    }

    public void setCenter(Vector center) {
        this.center = center;
    }

    @Override
    public double getDistance(Circle active) {
        return center.sub(active.getCenter()).intensity() - active.getRadius();
    }

    @Override
    public void collisionUpdateSpeed(Circle collided) {
        if (collided == null) return;
        if (collided.getSpeed().isZeroVector()) return;

        collided.setSpeed(collided.getSpeed().sub(
           collided.getCenter().sub(center).mul(
                   2 * ((collided.getSpeed().dotProduct(collided.getCenter().sub(center))) / Math.pow(collided.getCenter().sub(center).intensity(), 2))
           )
        ));
    }

    public String toString() {
        return "Dot " + id;
    }
}
