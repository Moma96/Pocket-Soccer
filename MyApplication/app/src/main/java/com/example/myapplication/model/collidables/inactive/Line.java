package com.example.myapplication.model.collidables.inactive;

import com.example.myapplication.model.Vector;
import com.example.myapplication.model.collidables.Field;
import com.example.myapplication.model.collidables.active.Circle;

import org.jetbrains.annotations.NotNull;

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

    private double getAxisDistance(@NotNull Circle active) {
        switch (orientation) {
            case VERTICAL:
                if (active.getCenter().getX() > position.getX())
                    return active.getCenter().getX() - position.getX() - active.getRadius();
                else
                    return position.getX() - active.getCenter().getX() - active.getRadius();
            case HORIZONTAL:
                if (active.getCenter().getY() > position.getY())
                    return active.getCenter().getY() - position.getY() - active.getRadius();
                else
                    return position.getY() - active.getCenter().getY() - active.getRadius();
        }
        return Double.MAX_VALUE;
    }

    @Override
    public double getDistance(@NotNull Circle active)  {
        double distance = getAxisDistance(active);

        if ((orientation.equals(Orientation.VERTICAL)
                && active.getCenter().getY() < position.getY() + length && active.getCenter().getY() > position.getY())
            ||
            (orientation.equals(Orientation.HORIZONTAL)
                && active.getCenter().getX() < position.getX() + length && active.getCenter().getX() > position.getX())) {
            return distance;
        } else
            return Double.MAX_VALUE;
    }

    @Override
    public double nextCollisionTime(@NotNull Circle active) {
        double distance = getAxisDistance(active);
        double t = 1;
        switch (orientation) {
            case VERTICAL:

                if (active.getCenter().getY() - active.getSpeed().getY() < position.getY() + length
                        && active.getCenter().getY() + active.getSpeed().getY() > position.getY()) {
                    if (active.getCenter().getX() > position.getX()) {
                        t = distance / active.getSpeed().getX();
                    } else {
                        t = - distance / active.getSpeed().getX();
                    }
                }
                break;
            case HORIZONTAL:
                if (active.getCenter().getX() - active.getSpeed().getX() < position.getX() + length
                        && active.getCenter().getX() + active.getSpeed().getX() > position.getX()) {
                    if (active.getCenter().getY() > position.getY()) {
                        t = - distance / active.getSpeed().getY();
                    } else {
                        t = distance / active.getSpeed().getY();
                    }
                }
                break;
        }

        return t;
        //if (t > Field.DISTANCE_PRECISSION && t < 1 - Field.DISTANCE_PRECISSION) return t;
        //else return 1;
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

    /*@Override
    public double nextCollisionTime(@NotNull Circle active) {
        double distance = 1;
        double t = 1;
        switch (orientation) {
            case VERTICAL:

                if (active.getCenter().getY() - active.getSpeed().getY() < position.getY() + length
                        && active.getCenter().getY() + active.getSpeed().getY() > position.getY()) {
                    if (active.getCenter().getX() > position.getX()) {
                        distance = active.getCenter().getX() - position.getX() - active.getRadius();
                        t = distance / active.getSpeed().getX();
                    } else {
                        distance = position.getX() - active.getCenter().getX() - active.getRadius();
                        t = - distance / active.getSpeed().getX();
                    }
                }
                break;
            case HORIZONTAL:
                if (active.getCenter().getX() - active.getSpeed().getX() < position.getX() + length
                        && active.getCenter().getX() + active.getSpeed().getX() > position.getX()) {
                    if (active.getCenter().getY() > position.getY()) {
                        distance = active.getCenter().getY() - position.getY() - active.getRadius();
                        t = - distance / active.getSpeed().getY();
                    } else {
                        distance = position.getY() - active.getCenter().getY() - active.getRadius();
                        t = distance / active.getSpeed().getY();
                    }
                }
                break;
        }

        if (t > 0 && t < 1) return t;
        else return 1;
    }*/

    @Override
    public boolean isClose(Circle active) {
        if (active == null) return false;
        double speedIntensity = active.getSpeed().intensity();

        switch(orientation) {
            case VERTICAL:
                if (active.getCenter().getY() - speedIntensity < position.getY() + length
                        && active.getCenter().getY() + speedIntensity > position.getY()) {
                    if (active.getCenter().getX() > position.getX())
                        return active.getCenter().getX() - position.getX() <= active.getCollisionZoneRadius();
                    else
                        return position.getX() - active.getCenter().getX() <= active.getCollisionZoneRadius();
                }
                break;
            case HORIZONTAL:
                if (active.getCenter().getX() - speedIntensity < position.getX() + length
                        && active.getCenter().getX() + speedIntensity > position.getX()) {
                    if (active.getCenter().getY() > position.getY())
                        return active.getCenter().getY() - position.getY() <= active.getCollisionZoneRadius();
                    else
                        return position.getY() - active.getCenter().getY() <= active.getCollisionZoneRadius();
                }
                break;
        }
        return false;
    }

    public String toString() {
        return "Line " + id;
    }
}
