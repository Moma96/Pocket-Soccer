package com.example.myapplication.model.soccer.bot;

import com.example.myapplication.model.Vector;
import com.example.myapplication.model.soccer.models.Ball;
import com.example.myapplication.model.soccer.models.SoccerModel;

public class TestingBall extends Ball {

    public TestingBall(Vector center, SoccerModel soccer) {
        super(center, soccer);
    }

    public TestingBall(Ball ball) {
        super(ball);
    }

    protected void goal(int goal) {

    }
}
