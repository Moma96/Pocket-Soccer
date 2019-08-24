package com.example.myapplication.model.soccer.database.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.example.myapplication.model.soccer.database.SoccerDatabase;
import com.example.myapplication.model.soccer.database.dao.PlayerDao;
import com.example.myapplication.model.soccer.database.entity.Player;

import java.util.List;

public class PlayerRepository {

    private PlayerDao playerDap;
    private LiveData<List<Player>> allPlayers;

    public PlayerRepository(Application application) {
        SoccerDatabase database = SoccerDatabase.getInstance(application);
        playerDap = database.playerDao();
        allPlayers = playerDap.getAllPlayers();
    }

    public void insert(Player player) {
        new AsyncTask<Player, Void, Void>() {
            @Override
            protected Void doInBackground(final Player... players) {
                playerDap.insert(players[0]);
                return null;
            }
        }.execute(player);
    }

    public void update(Player player) {
        new AsyncTask<Player, Void, Void>() {
            @Override
            protected Void doInBackground(final Player... players) {
                playerDap.update(players[0]);
                return null;
            }
        }.execute(player);
    }

    public void delete(Player player) {
        new AsyncTask<Player, Void, Void>() {
            @Override
            protected Void doInBackground(final Player... players) {
                playerDap.delete(players[0]);
                return null;
            }
        }.execute(player);
    }

    public void deleteAll() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                playerDap.deleteAll();
                return null;
            }
        }.execute();
    }
}
