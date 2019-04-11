package com.example.myapplication.model.soccer;

import android.util.Log;

import com.example.myapplication.model.collidables.active.Circle;
import com.example.myapplication.model.Vector;

public class Ball extends Circle {

    private static final double MASS = 1;
    private static final double RADIUS = 25;
    private static final double IMG_RADIUS_COEFFICIENT = 2.5;

    private SoccerModel soccer;
    private boolean goal_in_process = false;

    public Ball(Vector center, SoccerModel soccer) {
        super(MASS, RADIUS, IMG_RADIUS_COEFFICIENT, center);
        this.soccer = soccer;
        addCollidable(this);
    }

    protected void work() {
        if (soccer != null) {
            Goal[] goals = soccer.getGoals();
            for (int i = 0; i < goals.length; i++) {
                if (goals[i].inGoal(this)) {
                    if (goal_in_process) return;
                    else {
                        Log.d("GOAL", "GOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOAAAL!" + i);
                        goal_in_process = true;
                    }
                } else goal_in_process = false;
            }
        }
    }

    public String toString() {
        return "Ball " + id;
    }
}
