package com.example.myapplication.model.soccer.bot;

import com.example.myapplication.model.soccer.models.Ball;
import com.example.myapplication.model.soccer.models.Goal;
import com.example.myapplication.model.soccer.models.SoccerField;

import org.jetbrains.annotations.NotNull;

public class TestingBall extends Ball {

    //private int goal_distance_measured = -1;

    public TestingBall(@NotNull Ball ball, SoccerField field, TestingSoccerModel soccer) {
        super(ball, field, soccer);
    }

    @Override
    protected void delay() { /* do nothing */ }

    @Override
    protected void checkGoal(Goal goal, int i) {
        super.checkGoal(goal, i);
        /*double missed = goal.missedGoalBy(this);
        if (missed > 0) {
            if (goal_distance_measured == i) return;
            else {
                ((TestingSoccerModel)soccer).goalMissed((i + 1) % 2, missed);
                goal_distance_measured = i;
            }
        } else if (goal_distance_measured == i)
            goal_distance_measured = -1;
            */
    }
}
