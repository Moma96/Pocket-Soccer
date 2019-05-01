package com.example.myapplication.model.soccer.bot;

import com.example.myapplication.model.soccer.models.Ball;
import com.example.myapplication.model.soccer.models.SoccerField;

import org.jetbrains.annotations.NotNull;

public class TestingBall extends Ball {

    public TestingBall(@NotNull Ball ball, SoccerField field, TestingSoccerModel soccer) {
        super(ball, field, soccer);
    }

    /*protected TestingBall(@NotNull Ball ball, boolean include) {
        super(ball, include, null);
    }

    @Override
    protected TestingBall getNonInclusiveCopy() {
        return new TestingBall(this, false);
    }*/

    @Override
    protected void goal(int player) {
        soccer.score(player);
    }

    @Override
    protected void delay() { /* do nothing */ }
/*
    @Override
    public String toString() {
        return "Testball " + getActiveId();
    }*/
}
