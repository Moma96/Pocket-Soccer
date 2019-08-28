package com.example.myapplication.model.soccer.database.repository;

import android.app.Application;
import android.os.AsyncTask;

import com.example.myapplication.model.soccer.database.SoccerDatabase;
import com.example.myapplication.model.soccer.database.dao.PlayerDao;
import com.example.myapplication.model.soccer.database.entity.Player;

public class PlayerRepository {

    private PlayerDao playerDao;

    public PlayerRepository(Application application) {
        SoccerDatabase database = SoccerDatabase.getInstance(application);
        playerDao = database.playerDao();
    }

    public void insert(Player player) {
        new AsyncTask<Player, Void, Void>() {
            @Override
            protected Void doInBackground(final Player... players) {
                playerDao.insert(players[0]);
                return null;
            }
        }.execute(player);
    }

    public void update(Player player) {
        new AsyncTask<Player, Void, Void>() {
            @Override
            protected Void doInBackground(final Player... players) {
                playerDao.update(players[0]);
                return null;
            }
        }.execute(player);
    }

    public void delete(Player player) {
        new AsyncTask<Player, Void, Void>() {
            @Override
            protected Void doInBackground(final Player... players) {
                playerDao.delete(players[0]);
                return null;
            }
        }.execute(player);
    }

    public Player getPlayer(String name) {
        return playerDao.getPlayer(name);
    }

    public void deleteAll() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                playerDao.deleteAll();
                return null;
            }
        }.execute();
    }
}
