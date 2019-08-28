package com.example.myapplication.model.soccer.database.repository;

import android.app.Application;
import android.os.AsyncTask;

import com.example.myapplication.model.soccer.database.SoccerDatabase;
import com.example.myapplication.model.soccer.database.dao.MatchDao;
import com.example.myapplication.model.soccer.database.entity.Match;

import java.util.List;

public class MatchRepository {

    private MatchDao matchDao;

    public MatchRepository(Application application) {
        SoccerDatabase database = SoccerDatabase.getInstance(application);
        matchDao = database.matchDao();
    }

    public void insert(Match match) {
        new AsyncTask<Match, Void, Void>() {
            @Override
            protected Void doInBackground(final Match... matches) {
                matchDao.insert(matches[0]);
                return null;
            }
        }.execute(match);
    }

    public void update(Match match) {
        new AsyncTask<Match, Void, Void>() {
            @Override
            protected Void doInBackground(final Match... matches) {
                matchDao.update(matches[0]);
                return null;
            }
        }.execute(match);
    }

    public void delete(Match match) {
        new AsyncTask<Match, Void, Void>() {
            @Override
            protected Void doInBackground(final Match... matches) {
                matchDao.delete(matches[0]);
                return null;
            }
        }.execute(match);
    }

    public List<Match> getAll() {
        return matchDao.getAllMatches();
    }

    public List<Match> getAllBetween(String player1, String player2) {
        return matchDao.getAllBetween(player1, player2);
    }

    public void deleteAll() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                matchDao.deleteAll();
                return null;
            }
        }.execute();
    }

    public void deleteAllBetween(String player1Name, String player2Name) {
        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... playerNames) {
                matchDao.deleteAllBetween(playerNames[0], playerNames[1]);
                return null;
            }
        }.execute(player1Name, player2Name);
    }
}
