package com.example.myapplication.model.soccer.models;

import com.example.myapplication.model.collidables.Field;
import com.example.myapplication.model.collidables.active.Circle;
import com.example.myapplication.model.Vector;
import com.example.myapplication.model.soccer.SoccerFacade;

import org.jetbrains.annotations.NotNull;

public class Ball extends Circle {

    private static final double MASS = 1;
    private static final double RADIUS = 30;
    private static final double IMG_RADIUS_COEFFICIENT = 2.5;

    protected SoccerModel soccer;
    private int goal_in_process = -1;

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

    @Override
    public void updateSpeed(Vector speed) {
        super.updateSpeed(speed);
        synchronized (this) {
            notifyAll(); ///NOTIFY BALL IMAGE UPDATER
        }
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
            if (goals[i].inGoal(this)) {
                if (goal_in_process == i) return;
                else {
                    goal((i + 1) % 2);
                    goal_in_process = i;
                }
            } else if (i == goal_in_process)
                goal_in_process = -1;
        }
    }
}
