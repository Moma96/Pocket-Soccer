package com.example.myapplication.model.soccer.database.dao;

import android.arch.lifecycle.LiveData;
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
    LiveData<List<Match>> getAllMatches();

    @Query("DELETE FROM match_table")
    void deleteAll();

    @Query("DELETE FROM match_table WHERE player1Name = :player1Name AND player2Name = :player2Name")
    void deleteAllBetween(String player1Name, String player2Name);

}