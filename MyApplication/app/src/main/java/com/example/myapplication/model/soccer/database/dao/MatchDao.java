package com.example.myapplication.model.soccer.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.myapplication.model.soccer.database.entity.Match;

import java.util.List;

@Dao
public interface MatchDao {

    @Insert
    void insert(Match match);

    @Update
    void update(Match lastGame);

    @Delete
    void delete(Match lastGame);

    @Query("SELECT * FROM match_table")
    List<Match> getAllMatches();

    @Query("SELECT * FROM match_table WHERE (player1Name = :player1Name AND player2Name = :player2Name) OR (player1Name = :player2Name AND player2Name = :player1Name)")
    List<Match> getAllBetween(String player1Name, String player2Name);

    @Query("DELETE FROM match_table")
    void deleteAll();

    @Query("DELETE FROM match_table WHERE (player1Name = :player1Name AND player2Name = :player2Name) OR (player1Name = :player2Name AND player2Name = :player1Name)")
    void deleteAllBetween(String player1Name, String player2Name);

}