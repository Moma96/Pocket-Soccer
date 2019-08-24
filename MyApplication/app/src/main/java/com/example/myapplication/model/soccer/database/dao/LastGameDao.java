package com.example.myapplication.model.soccer.database.dao;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.myapplication.model.soccer.database.entity.LastGame;
import com.example.myapplication.view.activities.GameplayActivity;

import java.util.List;

@Dao
public interface LastGameDao {

    @Insert
    void insert(LastGame lastGame);

    @Update
    void update(LastGame lastGame);

    @Query("DELETE FROM last_game_table")
    void delete();

    @Query("SELECT * FROM last_game_table")
    LiveData<List<LastGame>> getLastGame();
}
