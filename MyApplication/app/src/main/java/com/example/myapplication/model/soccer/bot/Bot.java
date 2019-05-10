package com.example.myapplication.model.soccer.bot;

import com.example.myapplication.model.Active;
import com.example.myapplication.model.soccer.SoccerFacade;
import com.example.myapplication.model.soccer.SoccerGameplay;
import com.example.myapplication.model.Vector;

import org.jetbrains.annotations.NotNull;

public class Bot extends Active {

    private SoccerGameplay soccer;
    private int player;

    public Bot(@NotNull SoccerGameplay soccer, int player) {
        this.soccer = soccer;
        this.player = player;
    }

    public synchronized void waitTurn() {
        try {
            while (soccer.getActive() != player) {
                wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /*public synchronized void waitAllStopped() {
        try {
            while (soccer.allStopped() != player) {
                wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }*/

    public synchronized void play() {
        soccer.botStarted();

        /*
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
*/
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        soccer.select(soccer.getPlayers(player)[0]);
        soccer.push(new Vector(100, 0));

        soccer.botFinished();
    }

    @Override
    protected void iterate() {
        waitTurn();
        play();
    }
}
