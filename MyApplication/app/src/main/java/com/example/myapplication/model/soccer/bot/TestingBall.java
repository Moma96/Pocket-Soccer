package com.example.myapplication.model.soccer.bot;

import com.example.myapplication.model.soccer.models.Ball;
import com.example.myapplication.model.soccer.models.Goal;
import com.example.myapplication.model.soccer.models.SoccerField;

import org.jetbrains.annotations.NotNull;

public class TestingBall extends Ball {

    public TestingBall(@NotNull Ball ball, SoccerField field, TestingSoccerModel soccer) {
        super(ball, field, soccer);
    }

    @Override
    protected void delay() { /* do nothing */ }

    @Override
    protected void checkGoal(Goal goal, int i) {
        super.checkGoal(goal, i);
    }
}
