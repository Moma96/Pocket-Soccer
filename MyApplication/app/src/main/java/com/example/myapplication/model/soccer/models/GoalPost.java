package com.example.myapplication.model.soccer.models;

import com.example.myapplication.model.collidables.Field;
import com.example.myapplication.model.collidables.active.Circle;
import com.example.myapplication.model.collidables.inactive.Dot;
import com.example.myapplication.model.collidables.inactive.InactiveObject;
import com.example.myapplication.model.collidables.inactive.Line;

import org.jetbrains.annotations.NotNull;

public class GoalPost extends InactiveObject {

    private Goal.Direction direction;
    private Line line;
    private Dot dot;
    private Field field;

    public GoalPost(Goal.Direction direction, double x, double y, double length, Field field) {
        this.field = field;
        this.direction = direction;
        this.line = new Line(Line.Orientation.HORIZONTAL, x, y, length);
        switch (direction) {
            case EAST:
                dot = new Dot(x, y);
                break;
            case WEST:
                dot = new Dot(x + length, y);
                break;
        }
        field.addCollidable(this);
    }

    @Override
    public double getDistance(Circle active) {
        double distance = line.getDistance(active);
        if (distance == Double.MAX_VALUE)
            distance = dot.getDistance(active);
        return distance;
    }

    @Override
    public void collisionUpdateSpeed(Circle active) {
        double distance = dot.getDistance(active);

        if (distance >= -field.DISTANCE_PRECISSION && distance <= field.DISTANCE_PRECISSION)
            dot.collisionUpdateSpeed(active);
        else
            line.collisionUpdateSpeed(active);
    }

    @Override
    public double nextCollisionTime(@NotNull Circle active) {
        double dotTime = dot.nextCollisionTime(active);
        double lineTime = line.nextCollisionTime(active);

        if (dotTime < lineTime)
            return dotTime;
        else
            return lineTime;
    }

    @Override
    public boolean isClose(Circle active) {
        boolean dotClose = dot.isClose(active);
        boolean lineClose = line.isClose(active);

        return dotClose || lineClose;
    }

    public String toString() {
        return direction + " GoalPost " + id;
    }
}
