package com.example.myapplication.model.soccer.bot;

import com.example.myapplication.model.soccer.models.Ball;
import com.example.myapplication.model.soccer.models.SoccerField;

import org.jetbrains.annotations.NotNull;

public class TestingBall extends Ball {

    public TestingBall(@NotNull Ball ball, SoccerField field, TestingSoccerModel soccer) {
        super(ball, field, soccer);
    }

    @Override
    protected void delay() { /* do nothing */ }

    /*protected TestingBall(@NotNull Ball ball, boolean include) {
        super(ball, include, null);
    }

    @Override
    protected TestingBall getNonInclusiveCopy() {
        return new TestingBall(this, false);

    @Override
    public String toString() {
        return "Testball " + getActiveId();
    }*/
}
