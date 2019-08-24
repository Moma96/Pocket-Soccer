package com.example.myapplication.model.soccer.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;

import com.example.myapplication.model.soccer.database.dao.LastGameDao;
import com.example.myapplication.model.soccer.database.dao.MatchDao;
import com.example.myapplication.model.soccer.database.dao.PlayerDao;
import com.example.myapplication.model.soccer.database.entity.LastGame;
import com.example.myapplication.model.soccer.database.entity.Match;
import com.example.myapplication.model.soccer.database.entity.Player;

@Database(entities = {LastGame.class, Match.class, Player.class}, version = 1, exportSchema = false)
public abstract class SoccerDatabase extends RoomDatabase {

    private static SoccerDatabase instance;
    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {};

    public abstract PlayerDao playerDao();
    public abstract MatchDao matchDao();
    public abstract LastGameDao lastGameDao();

    public static synchronized SoccerDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    SoccerDatabase.class,"soccer_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }
}
