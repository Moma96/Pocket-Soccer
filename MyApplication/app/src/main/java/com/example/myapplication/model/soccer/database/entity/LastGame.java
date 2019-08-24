package com.example.myapplication.model.soccer.database.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "last_game_table")
public class LastGame {

    @NonNull
    @PrimaryKey
    private String lastGame;

    public LastGame(String lastGame) {
        this.lastGame = lastGame;
    }

    public String getLastGame() {
        return lastGame;
    }
}
