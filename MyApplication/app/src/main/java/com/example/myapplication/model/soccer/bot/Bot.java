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

    public synchronized void waitAllNotMoving() throws InterruptedException {
        while (!soccer.allNotMoving()) {
            wait();
        }
    }

    public synchronized void play() {

        Player[] players = soccer.getPlayers(player);
        GeneticTesting[] gens = new GeneticTesting[players.length];
        GeneticTesting.Unit[] results = new GeneticTesting.Unit[gens.length];

        for (int i = 0; i < gens.length; i++) {
            gens[i] = new GeneticTesting(soccer, player, i);
            results[i] = gens[i].test();
        }

        GeneticTesting.Unit best = results[0];
        int id = 0;
        for (int i = 1; i < results.length; i++) {
            if (results[i].getFitness() < best.getFitness()) {
                best = results[i];
                id = i;
            }
        }

        soccer.select(soccer.getPlayers(player)[id]);
        soccer.push(best.getGenes());

        /*
        soccer.select(soccer.getPlayers(player)[(int)(Math.random()*3)]);
        Vector speed = new Vector(Math.random()*2 - 1, Math.random()*2 - 1);
        speed.scaleIntensity(300);
        soccer.push(speed);
        */
    }

    @Override
    protected void iterate() {
        try {
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
