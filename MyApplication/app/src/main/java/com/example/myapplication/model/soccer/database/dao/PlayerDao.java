package com.example.myapplication.model.soccer.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.myapplication.model.soccer.database.entity.Player;

import java.util.List;

@Dao
public interface PlayerDao {

    @Insert
    void insert(Player player);

    @Update
    void update(Player player);

    @Delete
    void delete(Player player);

    @Query("SELECT * FROM player_table WHERE name = :name")
    LiveData<Player> getPlayer(String name);

    @Query("SELECT * FROM player_table")
    LiveData<List<Player>> getAllPlayers();

    @Query("DELETE FROM player_table")
    void deleteAll();
}