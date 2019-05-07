package com.example.myapplication.model.soccer.bot;

import com.example.myapplication.model.soccer.models.SoccerModel;

import org.jetbrains.annotations.NotNull;

public class TestingSoccerModel extends SoccerModel {

    GeneticTesting.Unit genUnit;

    public TestingSoccerModel(@NotNull GeneticTesting.Unit genUnit) {
        this.genUnit = genUnit;
        SoccerModel soccer = genUnit.getGen().getSoccer();
        setField(soccer.getX(), soccer.getY(), soccer.getWidth(), soccer.getHeight());
        setGoals();
        field = new TestingField(soccer.getX(), soccer.getY(), soccer.getWidth(), soccer.getHeight(), genUnit);

        ball = new TestingBall(soccer.getBall(), field, this);
        for (int p = 0; p < 2; p++) {
            for (int i = 0; i < 3; i++)
                players[p][i] = new TestingPlayer(soccer.getPlayers()[p][i], field);
        }
    }

    @Override
    public boolean score(int player) {
        synchronized (genUnit) {
            terminate();
            genUnit.finished(player, field.getTime());
            return true;
        }
    }
}
