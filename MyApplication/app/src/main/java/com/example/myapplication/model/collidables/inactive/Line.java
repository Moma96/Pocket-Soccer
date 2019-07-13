package com.example.myapplication.model.collidables.inactive;

import com.example.myapplication.model.Vector;
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
    public double nextCollisionTime(@NotNull Circle active) {
        double distance; //= getDistance(active);
        /*if (distance == Double.MAX_VALUE)               ///////OVO UOPSTE NE MORA DA ZNACI!!!! (nego isClose)
            return 1;*/

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
}
