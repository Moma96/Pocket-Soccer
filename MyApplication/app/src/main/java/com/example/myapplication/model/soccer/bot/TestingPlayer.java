package com.example.myapplication.model.soccer.bot;

import com.example.myapplication.model.soccer.models.Player;
import com.example.myapplication.model.soccer.models.SoccerField;

import org.jetbrains.annotations.NotNull;

public class TestingPlayer extends Player {

    public TestingPlayer(@NotNull Player player, SoccerField field) {
        super(player, field);
    }

    @Override
    protected void delay() { /* do nothing */ }


}
