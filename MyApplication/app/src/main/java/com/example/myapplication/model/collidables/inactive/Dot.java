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

    @Override
    public double nextCollisionTime(Circle circle) {
        double R = circle.getRadius();
        Vector deltaS = circle.getSpeed();
        Vector deltaC = circle.getCenter().sub(center);

        double a = deltaS.getX()*deltaS.getX() + deltaS.getY()*deltaS.getY();
        double b = 2*(deltaC.getX()*deltaS.getX() + deltaC.getY()*deltaS.getY());
        double c = deltaC.getX()*deltaC.getX() + deltaC.getY()*deltaC.getY() - R*R;
        double d = b*b - 4*a*c;
        if (d < 0 || a == 0) return 1;

        double t = (-b - Math.sqrt(d))/(2*a);
        if (t > 1 || t < 0) return 1;

        return t;
    }

    @Override
    public boolean isClose(Circle circle) {
        return getDistance(circle) <= circle.getCollisionZoneRadius() - circle.getRadius();
    }

    public String toString() {
        return "Dot " + id;
    }
}
