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

    public double getAxisDistance(@NotNull Vector vector) {
        switch (orientation) {
            case VERTICAL:
                return Math.abs(vector.getX() - position.getX());
            case HORIZONTAL:
                return Math.abs(vector.getY() - position.getY());
        }
        return Double.MAX_VALUE;
    }

    public double getAxisDistance(@NotNull Circle active) {
        return getAxisDistance(active.getCenter()) - active.getRadius();
    }

    public double getDistance(@NotNull Vector vector) {
        double distance = getAxisDistance(vector);

        if ((orientation.equals(Orientation.VERTICAL)
                && vector.getY() <= position.getY() + length && vector.getY() >= position.getY())
            ||
            (orientation.equals(Orientation.HORIZONTAL)
                && vector.getX() <= position.getX() + length && vector.getX() >= position.getX())) {
            return distance;
        } else
            return Double.MAX_VALUE;
    }

    @Override
    public double getDistance(@NotNull Circle active)  {
        double centerDistance = getDistance(active.getCenter());
        if (centerDistance == Double.MAX_VALUE)
            return centerDistance;
        return centerDistance - active.getRadius();
    }

    @Override
    public double nextCollisionTime(@NotNull Circle active) {
        double distance = getAxisDistance(active);
        double t = 1;
        switch (orientation) {
            case VERTICAL:
                    if (active.getCenter().getX() > position.getX()) {
                        t = distance / active.getSpeed().getX();
                    } else {
                        t = - distance / active.getSpeed().getX();
                    }
                break;
            case HORIZONTAL:
                    if (active.getCenter().getY() > position.getY()) {
                        t = - distance / active.getSpeed().getY();
                    } else {
                        t = distance / active.getSpeed().getY();
                    }
                break;
        }

        double newDistance = getDistance(active.getCenter().add(active.getSpeed().mul(t))) - active.getRadius();
        if (newDistance != Double.MAX_VALUE)
            return t;
        else
            return 1;
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
