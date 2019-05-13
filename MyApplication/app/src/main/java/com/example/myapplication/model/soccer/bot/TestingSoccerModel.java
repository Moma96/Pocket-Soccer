package com.example.myapplication.model.soccer.bot;

import com.example.myapplication.model.soccer.models.Player;
import com.example.myapplication.model.soccer.models.SoccerModel;

import org.jetbrains.annotations.NotNull;

public class TestingSoccerModel extends SoccerModel {

    GeneticTesting.Unit genUnit;

    public TestingSoccerModel(@NotNull GeneticTesting.Unit genUnit) {
        SoccerModel soccer = genUnit.getGen().getSoccer();

        setParameters(soccer.getX(), soccer.getY(), soccer.getWidth(), soccer.getHeight());
        field = new TestingField(soccer.getX(), soccer.getY(), soccer.getWidth(), soccer.getHeight(), genUnit, this);
        setGoals();

        ball = new TestingBall(soccer.getBall(), field, this);

        players = new Player[2][3];
        for (int p = 0; p < 2; p++) {
            for (int i = 0; i < 3; i++)
                players[p][i] = new TestingPlayer(soccer.getPlayers()[p][i], field);
        }
        this.genUnit = genUnit;
    }

    @Override
    public void score(int player) {
        synchronized (genUnit) {
            terminate();
            genUnit.finished(player, field.getTime());
        }
    }
}
