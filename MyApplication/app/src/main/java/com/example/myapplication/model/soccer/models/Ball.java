package com.example.myapplication.model.soccer.models;

import com.example.myapplication.model.collidables.active.ActiveObject;
import com.example.myapplication.model.Vector;
import com.example.myapplication.model.soccer.SoccerFacade;

public class Ball extends Circle {

    private static final double MASS = 1;
    private static final double RADIUS = 30;
    private static final double IMG_RADIUS_COEFFICIENT = 2.5;

    private SoccerFacade facade;
    private SoccerModel soccer;
    private int goal_in_process = -1;

    public Ball(Vector center, SoccerModel soccer) {
        super(MASS, RADIUS, IMG_RADIUS_COEFFICIENT, center, soccer.getField());
        this.soccer = soccer;
        soccer.getField().addCollidable(this);
    }

    public Ball(Ball ball) {
        super(ball);
    }

    public synchronized void setFacade(SoccerFacade facade) {
        this.facade = facade;
        notify();
    }

    public String toString() {
        return "Ball " + getActiveId();
    }

    public ActiveObject getCopy() {
        return new Ball(this);
    }

    protected synchronized void work() {
        while (facade == null) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Goal[] goals = soccer.getGoals();
        for (int i = 0; i < goals.length; i++) {
            if (goals[i].inGoal(this)) {
                if (goal_in_process == i) return;
                else {
                    goal(i);
                    goal_in_process = i;
                }
            } else if (i == goal_in_process)
                goal_in_process = -1;
        }
    }

    protected void goal(int goal) {
        facade.score((goal + 1) % 2);
    }
}