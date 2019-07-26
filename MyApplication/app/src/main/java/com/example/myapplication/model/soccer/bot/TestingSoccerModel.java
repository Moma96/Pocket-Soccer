package com.example.myapplication.model.soccer.bot;

import com.example.myapplication.model.soccer.models.Ball;
import com.example.myapplication.model.soccer.models.Player;
import com.example.myapplication.model.soccer.models.SoccerModel;

import org.jetbrains.annotations.NotNull;

public class TestingSoccerModel extends SoccerModel {

    GeneticTesting.Unit genUnit;

    public TestingSoccerModel(@NotNull GeneticTesting.Unit genUnit) {
        SoccerModel soccer = genUnit.getGen().getSoccer();

        setParameters(soccer.getX(), soccer.getY(), soccer.getWidth(), soccer.getHeight());
        field = new TestingField(soccer.getX(), soccer.getY(), soccer.getWidth(), soccer.getHeight(), soccer.getField().getFrictionCoefficient(), genUnit, this);
        setGoals();

        ball = new Ball(soccer.getBall(), field, this); /// da li je dobro ovo?

        players = new Player[2][3];
        for (int p = 0; p < 2; p++) {
            for (int i = 0; i < 3; i++)
                players[p][i] = new Player(soccer.getPlayers()[p][i], field); // da li je dobro ovo?
        }
        this.genUnit = genUnit;
    }

    @Override
    public void score(int player) {
        terminate();
        genUnit.finished(player, (int)field.getTime());     ////////////////////////////////////////////////// PAZI!!!!
    }

    @Override
    public void goalMissed(int player, double missed) {
        super.goalMissed(player, missed);
        genUnit.updateFitness(player, missed);
    }

    @Override
    public void allStopped() {
        genUnit.terminated((int)field.getTime());    ///////////////////////////////////////////////// PAZI!!!!
    }
}
