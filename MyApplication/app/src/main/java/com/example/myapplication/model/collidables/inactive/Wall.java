package com.example.myapplication.model.collidables.inactive;

import com.example.myapplication.model.Vector;
import com.example.myapplication.model.collidables.active.Circle;

import org.jetbrains.annotations.NotNull;

public class Wall extends InactiveObject {

    public enum Direction { NORTH, SOUTH, WEST, EAST };

    private Direction direction;
    private double xy;

    public Wall(Direction direction, double xy) {
        this.direction = direction;
        this.xy = xy;
    }

    public Direction getDirection() {
        return direction;
    }

    public double getXY() {
        return xy;
    }

    @Override
    public double getDistance(Circle active) {
        if (active == null) return 1;

        switch(direction) {
            case NORTH:
                return active.getCenter().getY() - active.getRadius() - xy;
            case SOUTH:
                return xy - (active.getCenter().getY() + active.getRadius());
            case WEST:
                return active.getCenter().getX() - active.getRadius() - xy;
            case EAST:
                return xy - (active.getCenter().getX() + active.getRadius());
        }
        return Double.MAX_VALUE;
    }

    @Override
    public void collisionUpdateSpeed(Circle active) {
        if (active == null) return;

        Vector new_speed = active.getSpeed();
        switch(direction) {
            case NORTH:
            case SOUTH:
                new_speed.setY(-new_speed.getY());
                break;
            case EAST:
            case WEST:
                new_speed.setX(-new_speed.getX());
                break;
        }
        active.setSpeed(new_speed);
    }

    @Override
    public double nextCollisionTime(@NotNull Circle active) {
        double distance = getDistance(active);
        if (distance == Double.MAX_VALUE)
            return 1;

        double t = 1;
        switch(direction) {
            case EAST:
            case WEST:
                if (active.getSpeed().getX() > 0) {
                    t = distance / active.getSpeed().getX();
                } else {
                    t = - distance / active.getSpeed().getX();
                }
                break;
            case NORTH:
            case SOUTH:
                if (active.getSpeed().getY() > 0) {
                    t = distance / active.getSpeed().getY();
                } else {
                    t = - distance / active.getSpeed().getY();
                }
                break;
        }

        if (t > 0 && t < 1) return t;
        else return 1;
    }

    @Override
    public boolean isClose(Circle circle) {
        return getDistance(circle) <= circle.getCollisionZoneRadius() - circle.getRadius();
    }

    @Override
    public String toString() {
        return direction + " Wall " + id;
    }
}
