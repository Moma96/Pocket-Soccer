package com.example.myapplication.model.soccer.bot;

import com.example.myapplication.model.Active;
import com.example.myapplication.model.soccer.SoccerGameplay;

import org.jetbrains.annotations.NotNull;

public class Bot extends Active {

    private SoccerGameplay soccer;
    private int player;

    public Bot(@NotNull SoccerGameplay soccer, int player) {
        this.soccer = soccer;
        this.player = player;
    }

    @Override
    protected void iterate() {

    }
}
