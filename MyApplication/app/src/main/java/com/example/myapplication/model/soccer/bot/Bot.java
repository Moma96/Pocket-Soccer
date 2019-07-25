package com.example.myapplication.model.soccer.bot;

import com.example.myapplication.model.Active;
import com.example.myapplication.model.soccer.SoccerGameplay;
import com.example.myapplication.model.soccer.models.Player;


import org.jetbrains.annotations.NotNull;

public class Bot extends Active {

    private SoccerGameplay soccer;
    private int player;

    public Bot(@NotNull SoccerGameplay soccer, int player) {
        this.soccer = soccer;
        this.player = player;
    }

    public synchronized void waitTurn() throws InterruptedException {
        while (soccer.getActive() != player) {
            wait();
        }
    }

    public void waitResponsiveness() throws InterruptedException {
        synchronized (soccer) {
            while (!soccer.responsive()) {
                soccer.wait();
            }
        }
    }

    public synchronized void waitAllNotMoving() throws InterruptedException {
        while (!soccer.allNotMoving()) {
            wait();
        }
    }

    public synchronized void play() throws InterruptedException {

        Player[] players = soccer.getPlayers(player);
        GeneticTesting.Unit[] results = new GeneticTesting.Unit[players.length];

        int id = 0;
        GeneticTesting.Unit best = null;
        for (int i = 0; i < players.length; i++) {
            GeneticTesting gen = new GeneticTesting(soccer, player, i);
            results[i] = gen.test();
            if (gen.getFinished() != null) {
                best = gen.getFinished();
                id = i;
                break;
            }
        }

        if (best == null) {
            best = results[0];
            for (int i = 1; i < results.length; i++) {
                if (results[i].getFitness() < best.getFitness()) {
                    best = results[i];
                    id = i;
                }
            }
        }

        soccer.select(soccer.getPlayers(player)[id]);
        soccer.push(best.getGenes());
//*/
/*
        //sleep(200);
        soccer.select(soccer.getPlayers(player)[(int)(Math.random()*3)]);
        //soccer.select(soccer.getPlayers(player)[0]);
        Vector speed = new Vector(Math.random()*2 - 1, Math.random()*2 - 1);
        //Vector speed = new Vector(1, 0);
        speed.scaleIntensity(1000);
        soccer.push(speed);
//*/
    }

    @Override
    protected void iterate() {
        try {
            waitResponsiveness();
            waitTurn();

            soccer.botStarted();
            waitAllNotMoving();
            play();
            soccer.botFinished();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}