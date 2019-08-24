package com.example.myapplication.model.soccer.database.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "match_table",
        foreignKeys = { @ForeignKey(entity = Player.class,
                                    parentColumns = "name",
                                    childColumns = "player1Name",
                                    onUpdate = ForeignKey.CASCADE,
                                    onDelete = ForeignKey.CASCADE),
                        @ForeignKey(entity = Player.class,
                                    parentColumns = "name",
                                    childColumns = "player2Name",
                                    onUpdate = ForeignKey.CASCADE,
                                    onDelete = ForeignKey.CASCADE)})
public class Match {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String player1Name;
    private String player2Name;
    private int player1Team;
    private int player2Team;
    private int player1Score;
    private int player2Score;

    public Match(String player1Name, int player1Team, String player2Name, int player2Team, int player1Score, int player2Score) {
        this.player1Name = player1Name;
        this.player1Team = player1Team;
        this.player2Name = player2Name;
        this.player2Team = player2Team;
        this.player1Score = player1Score;
        this.player2Score = player2Score;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getPlayer1Name() {
        return player1Name;
    }

    public void setPlayer1Name(String player1Name) {
        this.player1Name = player1Name;
    }

    public String getPlayer2Name() {
        return player2Name;
    }

    public void setPlayer2Name(String player2Name) {
        this.player2Name = player2Name;
    }

    public int getPlayer1Team() {
        return player1Team;
    }

    public void setPlayer1Team(int player1Team) {
        this.player1Team = player1Team;
    }

    public int getPlayer2Team() {
        return player2Team;
    }

    public void setPlayer2Team(int player2Team) {
        this.player2Team = player2Team;
    }

    public int getPlayer1Score() {
        return player1Score;
    }

    public void setPlayer1Score(int player1Score) {
        this.player1Score = player1Score;
    }

    public int getPlayer2Score() {
        return player2Score;
    }

    public void setPlayer2Score(int player2Score) {
        this.player2Score = player2Score;
    }
}
