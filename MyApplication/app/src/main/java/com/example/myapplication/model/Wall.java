package com.example.myapplication.model;

public class Wall implements Collidable {

    @Override
    public double getDistance(Circle circle) {
        switch(direction) {
            case NORTH:
                return circle.getCenter().getY() - circle.getRadius() - xy;
            case SOUTH:
                return xy - (circle.getCenter().getY() + circle.getRadius());
            case WEST:
                return circle.getCenter().getX() - circle.getRadius() - xy;
            case EAST:
                return xy - (circle.getCenter().getX() + circle.getRadius());
        }
        return -1;
    }

    @Override
    public void collisionUpdateSpeed(Circle circle) {
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
