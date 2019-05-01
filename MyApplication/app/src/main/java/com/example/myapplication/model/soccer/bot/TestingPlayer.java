package com.example.myapplication.model.soccer.bot;

import com.example.myapplication.model.collidables.active.ActiveObject;
import com.example.myapplication.model.soccer.models.Ball;
import com.example.myapplication.model.soccer.models.Player;
import com.example.myapplication.model.soccer.models.SoccerField;

import org.jetbrains.annotations.NotNull;

public class TestingPlayer extends Player {

    public TestingPlayer(@NotNull Player player, SoccerField field) {
        super(player, field);
    }


    protected TestingPlayer(@NotNull Player player, boolean include) {
        super(player, include);
    }

    @Override
    protected TestingPlayer getNonInclusiveCopy() {
        return new TestingPlayer(this, false);
    }

    @Override
    protected void delay() { /* do nothing */ }

    @Override
    public String toString() {
        return "Testplayer " + getActiveId();
    }
}
