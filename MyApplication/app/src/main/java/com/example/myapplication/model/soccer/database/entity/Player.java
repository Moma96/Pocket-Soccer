package com.example.myapplication.model.soccer.database.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "player_table", indices = {@Index(value = {"name"}, unique = true)})
public class Player {

    @NonNull
    @PrimaryKey
    private String name;

    private int victories;

    public Player(String name, int victories) {
        this.name = name;
        this.victories = victories;
    }

    public String getName() {
        return name;
    }

    public int getVictories() {
        return victories;
    }

    public void addVictory() {
        victories++;
    }
}
