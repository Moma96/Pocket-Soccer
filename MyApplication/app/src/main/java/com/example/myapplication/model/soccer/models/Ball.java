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
        super(MASS, RADIUS, IMG_RADIUS_COEFFICIENT, center);
        this.soccer = soccer;
        addCollidable(this);
    }

    public Ball(Ball ball) {
        super(ball);
    }

    public synchronized void setFacade(SoccerFacade facade) {
        this.facade = facade;
        notifyAll();
    }

    public String toString() {
        return "Ball " + id;
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
                    facade.score(i);
                    goal_in_process = i;
                }
            } else if (i == goal_in_process)
                goal_in_process = -1;
        }
    }
}
