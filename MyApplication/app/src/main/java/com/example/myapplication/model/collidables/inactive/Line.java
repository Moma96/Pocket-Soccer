package com.example.myapplication.model.collidables.inactive;

import com.example.myapplication.model.Vector;
import com.example.myapplication.model.collidables.active.Circle;

public class Line extends InactiveObject {

    public enum Orientation { HORIZONTAL, VERTICAL };

    private Orientation orientation;
    private Vector position;
    private double length;

    public Line(Orientation orientation, double x, double y, double length) {
        this.orientation = orientation;
        position = new Vector(x, y);
        this.length = length;
    }

    public String toString() {
        return "Line " + id;
    }

    @Override
    public double getDistance(Circle active) {
        if (active == null) return 1;

        switch(orientation) {
            case VERTICAL:
                if (active.getCenter().getY() < position.getY() + length && active.getCenter().getY() > position.getY()) {
                    if (active.getCenter().getX() > position.getX())
                        return active.getCenter().getX() - position.getX() - active.getRadius();
                    else
                        return position.getX() - active.getCenter().getX() - active.getRadius();
                }
                break;
            case HORIZONTAL:
                if (active.getCenter().getX() < position.getX() + length && active.getCenter().getX() > position.getX()) {
                    if (active.getCenter().getY() > position.getY())
                        return active.getCenter().getY() - position.getY() - active.getRadius();
                    else
                        return position.getY() - active.getCenter().getY() - active.getRadius();
                }
                break;
        }
        return Double.MAX_VALUE;
    }

    @Override
    public void collisionUpdateSpeed(Circle active) {
        if (active == null) return;

        Vector new_speed = active.getSpeed();
        switch(orientation) {
            case HORIZONTAL:
                new_speed.setY(-new_speed.getY());
                break;
            case VERTICAL:
                new_speed.setX(-new_speed.getX());
                break;
        }
        active.setSpeed(new_speed);
    }

    @Override
    public double nextCollisionTime(Circle circle) {
        return 0;
    }
}
