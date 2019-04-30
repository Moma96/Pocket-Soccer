package com.example.myapplication.model.soccer.bot;

import android.util.Log;

import com.example.myapplication.model.Vector;
import com.example.myapplication.model.soccer.models.Player;
import com.example.myapplication.model.soccer.models.SoccerModel;

public class TestingSoccerModel extends SoccerModel {

    int player = -1;
    int scored = -1;
    int time = -1;

    public TestingSoccerModel(SoccerModel soccer) {
        setField(soccer.getX(), soccer.getY(), soccer.getWidth(), soccer.getHeight());
        setGoals();
        field = new TestingField(soccer.getX(), soccer.getY(), soccer.getWidth(), soccer.getHeight());

        ball = new TestingBall(soccer.getBall(), field);
        for (int p = 0; p < 2; p++) {
            for (int i = 0; i < 3; i++)
                players[p][i] = new TestingPlayer(soccer.getPlayers()[p][i], field);
        }
    }

    public int test(int p, int i, Vector force) {
        if (p >= 2 || i >= getPlayers(p).length)
            return -1; /////////////////throw exception
        this.player = p;
        notifyAll();
        Player player = getPlayers()[p][i];
        player.push(force);

        waitData();

        if (this.player == scored) {
            return getField().getTime();
        } else return Integer.MAX_VALUE;
    }

    @Override
    public boolean score(int player) {
        terminate();
        scored = player;
        time = field.getTime();
        notifyAll();
        return true;
    }
/*
    @Override
    public void allStopped() {
        terminate();
        time = field.getTime();
        notifyAll();
    }*/

    public synchronized void waitData() {
        while (player < 0 || time < 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
