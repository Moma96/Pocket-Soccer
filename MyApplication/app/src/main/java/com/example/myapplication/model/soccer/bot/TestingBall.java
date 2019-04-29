package com.example.myapplication.model.soccer.bot;

import com.example.myapplication.model.soccer.models.Ball;
import com.example.myapplication.model.soccer.models.SoccerField;

public class TestingBall extends Ball {

    public TestingBall(Ball ball, SoccerField field) {
        super(ball, field);
    }

    @Override
    protected void goal(int player) {
        soccer.score(player);
    }

    @Override
    protected void delay() { /* do nothing */ }
}
