package com.example.myapplication.model.soccer;

import com.example.myapplication.model.Vector;

public class Goal {

    private GoalPost[] posts = new GoalPost[2];
    private GoalPost.Direction direction;
    private Vector position;
    private double width;
    private double height;

    public Goal(GoalPost.Direction direction, double x, double y, double width, double height) {
        this.direction = direction;
        position = new Vector(x, y);
        this.width = width;
        this.height = height;
        posts[0] = new GoalPost(direction, x, y, height);
        posts[1] = new GoalPost(direction, x + width, y, height);
    }

    public boolean inGoal(Ball ball) {
        if (posts[0].getDistance(ball) <= 0 || posts[0].getDistance(ball) <= 0)
            return false;

        if (ball.getCenter().getX() > position.getX() && ball.getCenter().getX() < position.getX() + width) {
            switch(direction) {
                case NORTH:
                    if (ball.getCenter().getY() - ball.getRadius() < position.getY() + height)
                        return true;
                    break;
                case SOUTH:
                    if (ball.getCenter().getY() + ball.getRadius() > position.getY())
                        return true;
                    break;
            }
        }
        return false;
    }

    public String toString() {
        return direction + " wall";
    }
}
