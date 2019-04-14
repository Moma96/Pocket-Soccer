package com.example.myapplication.model.soccer;

import com.example.myapplication.model.collidables.active.ActiveObject;
import com.example.myapplication.model.collidables.active.Circle;
import com.example.myapplication.model.Vector;

public class Ball extends Circle {

    private static final double MASS = 1;
    private static final double RADIUS = 30;
    private static final double IMG_RADIUS_COEFFICIENT = 2.5;

    private SoccerModel soccer;
    private int goal_in_process = -1;

    public Ball(Vector center, SoccerModel soccer) {
        super(MASS, RADIUS, IMG_RADIUS_COEFFICIENT, center);
        this.soccer = soccer;
        addCollidable(this);
    }

    public Ball(Ball ball) {
        super(ball);
    }

    protected void work() {
        if (soccer != null && soccer.scoreTracking()) {
            Goal[] goals = soccer.getGoals();
            for (int i = 0; i < goals.length; i++) {
                if (goals[i].inGoal(this)) {
                    if (goal_in_process == i) return;
                    else {
                        soccer.goal((i + 1) % 2);
                        goal_in_process = i;
                    }
                } else if (i == goal_in_process)
                    goal_in_process = -1;
            }
        }
    }

    public String toString() {
        return "Ball " + id;
    }

    public ActiveObject getCopy() {
        return new Ball(this);
    }
}
