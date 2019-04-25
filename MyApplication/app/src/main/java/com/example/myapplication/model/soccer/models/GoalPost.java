package com.example.myapplication.model.soccer.models;

import com.example.myapplication.model.collidables.Field;
import com.example.myapplication.model.collidables.active.ActiveObject;
import com.example.myapplication.model.collidables.inactive.Dot;
import com.example.myapplication.model.collidables.inactive.InactiveObject;
import com.example.myapplication.model.collidables.inactive.Line;

public class GoalPost extends InactiveObject {

    public enum Direction { NORTH, SOUTH };

    Direction direction;
    Line line;
    Dot dot;

    public GoalPost(Direction direction, double x, double y, double length, Field field) {
        this.direction = direction;
        this.line = new Line(Line.Orientation.VERTICAL, x, y, length);
        switch (direction) {
            case NORTH:
                dot = new Dot(x, y + length);
                break;
            case SOUTH:
                dot = new Dot(x, y);
                break;
        }
        field.addCollidable(this);
    }

    @Override
    public double getDistance(ActiveObject active) {
        double distance = line.getDistance(active);
        if (distance == Double.MAX_VALUE)
            distance = dot.getDistance(active);
        return distance;
    }

    @Override
    public void collisionUpdateSpeed(ActiveObject active) {
        if (line.getDistance(active) == Double.MAX_VALUE)
            dot.collisionUpdateSpeed(active);
        else line.collisionUpdateSpeed(active);
    }

    public String toString() {
        return direction + " GoalPost " + id;
    }
}
