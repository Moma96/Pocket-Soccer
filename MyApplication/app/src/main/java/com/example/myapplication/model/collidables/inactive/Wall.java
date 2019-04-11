package com.example.myapplication.model.collidables.inactive;

import com.example.myapplication.model.Vector;
import com.example.myapplication.model.collidables.active.ActiveObject;
import com.example.myapplication.model.collidables.inactive.InactiveObject;

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

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public double getXY() {
        return xy;
    }

    public void setXY(double xy) {
        this.xy = xy;
    }


    @Override
    public double getDistance(ActiveObject active) {
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
    public void collisionUpdateSpeed(ActiveObject active) {
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

    public String toString() {
        return direction + " Wall " + id;
    }
}
