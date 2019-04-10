package com.example.myapplication.model.collidables.inactive;

import com.example.myapplication.model.Vector;
import com.example.myapplication.model.collidables.active.ActiveObject;
import com.example.myapplication.model.collidables.inactive.InactiveObject;

public class Wall extends InactiveObject {

    @Override
    public double getDistance(ActiveObject active) {
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
        return -1;
    }

    @Override
    public void collisionUpdateSpeed(ActiveObject circle) {
        Vector new_speed = circle.getSpeed();
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
        circle.setSpeed(new_speed);
    }

    @Override
    public boolean isInside(Vector dot) {
        return false;
    }

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

    public String toString() {
        return "Wall " + direction;
    }
}
