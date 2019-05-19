package com.example.myapplication.model.soccer.models;

import com.example.myapplication.model.Vector;
import com.example.myapplication.model.collidables.Field;

public class Goal {

    public enum Direction { EAST, WEST };

    private GoalPost[] posts = new GoalPost[2];
    private Direction direction;
    private Vector position;
    private double width;
    private double height;

    public Goal(Direction direction, double x, double y, double width, double height, Field field) {
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

    public double missedGoalBy(Ball ball) {
        if ((direction == Direction.EAST &&
            ball.getCenter().getX() + ball.getRadius() > position.getX() &&
            ball.getSpeed().getX() > 0) // ball is going in right direction
            ||
            (direction == Direction.WEST &&
            ball.getCenter().getX() - ball.getRadius() < position.getX() + width &&
            ball.getSpeed().getY() < 0)) { // ball is going in right direction

                if (ball.getCenter().getY() - ball.getRadius() < position.getY())
                    return position.getY() - (ball.getCenter().getY() - ball.getRadius());
                else if (ball.getCenter().getY() + ball.getRadius() > position.getY() + height)
                    return ball.getCenter().getY() + ball.getRadius() - (position.getY() + height);
        }
        return 0;
    }

    public Vector getPosition() {
        return position;
    }

    public Direction getDirection() {
        return direction;
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
