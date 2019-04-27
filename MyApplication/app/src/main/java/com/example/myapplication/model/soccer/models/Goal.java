package com.example.myapplication.model.soccer.models;

import com.example.myapplication.model.Vector;
import com.example.myapplication.model.collidables.Field;

public class Goal {

    private GoalPost[] posts = new GoalPost[2];
    private GoalPost.Direction direction;
    private Vector position;
    private double width;
    private double height;

    public Goal(GoalPost.Direction direction, double x, double y, double width, double height, Field field) {
        this.direction = direction;
        position = new Vector(x, y);
        this.width = width;
        this.height = height;
        posts[1] = new GoalPost(direction, x, y, width, field);
        posts[0] = new GoalPost(direction, x, y + height, width, field);
    }

    public boolean inGoal(Ball ball) {
        if (ball.getCenter().getY() > position.getY() && ball.getCenter().getY() < position.getY() + height) {
            switch(direction) {
                case EAST:
                    if (ball.getCenter().getX() > position.getX())
                        return true;
                    break;
                case WEST:
                    if (ball.getCenter().getX() < position.getX() + width)
                        return true;
                    break;
            }
        }
        return false;
    }

    public Vector getPosition() {
        return position;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public String toString() {
        return direction + " goal";
    }
}
