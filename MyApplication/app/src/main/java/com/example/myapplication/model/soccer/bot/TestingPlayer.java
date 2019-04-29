package com.example.myapplication.model.soccer.bot;

import com.example.myapplication.model.soccer.models.Player;
import com.example.myapplication.model.soccer.models.SoccerField;

public class TestingPlayer extends Player {

    public TestingPlayer(Player player, SoccerField field) {
        super(player, field);
    }

    @Override
    protected void delay() { /* do nothing */ }
}
