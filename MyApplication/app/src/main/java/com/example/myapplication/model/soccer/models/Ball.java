package com.example.myapplication.model.soccer.models;

import com.example.myapplication.model.collidables.Field;
import com.example.myapplication.model.collidables.active.Circle;
import com.example.myapplication.model.Vector;

import org.jetbrains.annotations.NotNull;

public class Ball extends Circle {

    private static final double MASS = 0.5; //0.5
    private static final double RADIUS = 30; //30
    private static final double IMG_RADIUS_COEFFICIENT = 2.5;

    protected SoccerModel soccer;
    private int goal_in_process = -1;
    private int goal_distance_measured = -1;

    public Ball(Vector center, SoccerModel soccer) {
        super(MASS, RADIUS, IMG_RADIUS_COEFFICIENT, center, soccer.getField());
        this.soccer = soccer;
    }

    public Ball(@NotNull Ball ball, SoccerModel soccer) {
        super(ball);
        this.soccer = soccer;
    }

    public Ball(@NotNull Ball ball, boolean include, SoccerModel soccer) {
        super(ball, include);
        this.soccer = soccer;
    }

    public Ball(@NotNull Ball ball, @NotNull Field field, SoccerModel soccer) {
        super(ball, field);
        this.soccer = soccer;
    }

    protected void goal(int player) {
        soccer.score(player);
    }

    @Override
    public String toString() {
        return "Ball " + getCircleId();
    }

    @Override
    protected Ball getNonInclusiveCopy() {
        return new Ball(this, false, null);
    }

    @Override
    protected synchronized void work() {
        Goal[] goals = soccer.getGoals();
        for (int i = 0; i < goals.length; i++) {
            checkGoal(goals[i], i);
        }
    }

    private void checkInGoal(Goal goal, int i) {
        if (goal.inGoal(this)) {
            if (goal_in_process == i) return;
            else {
                goal((i + 1) % 2);
                goal_in_process = i;
            }
        } else if (goal_in_process == i)
            goal_in_process = -1;
    }

    private void checkMissedGoal(Goal goal, int i) {
        double missed = goal.missedGoalBy(this);
        if (missed != 0) {
            if (goal_distance_measured == i) return;
            else {
                soccer.goalMissed((i + 1) % 2, missed);
                goal_distance_measured = i;
            }
        } else if (goal_distance_measured == i)
            goal_distance_measured = -1;
    }

    protected synchronized void checkGoal(Goal goal, int i) {
        checkInGoal(goal, i);
        checkMissedGoal(goal, i);
    }
}
