package com.example.myapplication.model.soccer.database.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.example.myapplication.model.soccer.SoccerGameplay;
import com.example.myapplication.model.soccer.database.SoccerDatabase;
import com.example.myapplication.model.soccer.database.dao.LastGameDao;
import com.example.myapplication.model.soccer.database.entity.LastGame;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class LastGameRepository {

    private LastGameDao lastGameDao;
    private LiveData<List<LastGame>> lastGame;

    public LastGameRepository(Application application) {
        SoccerDatabase database = SoccerDatabase.getInstance(application);
        lastGameDao = database.lastGameDao();
        lastGame = lastGameDao.getLastGame();
    }

    public void insert(LastGame lastGame) {
        new AsyncTask<LastGame, Void, Void>() {
            @Override
            protected Void doInBackground(final LastGame... lastGames) {
                lastGameDao.insert(lastGames[0]);
                return null;
            }
        }.execute(lastGame);
    }

    public void update(LastGame lastGame) {
        new AsyncTask<LastGame, Void, Void>() {
            @Override
            protected Void doInBackground(final LastGame... lastGames) {
                lastGameDao.update(lastGames[0]);
                return null;
            }
        }.execute(lastGame);
    }

    public void delete() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                lastGameDao.delete();
                return null;
            }
        }.execute();
    }

    public String serialize(SoccerGameplay gameplay) {
        try {
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bao);
            oos.writeObject(gameplay);
            oos.flush();
            return bao.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public SoccerGameplay deserialize(String gameplay) {
        try {
            byte b[] = gameplay.getBytes();
            ByteArrayInputStream bi = new ByteArrayInputStream(b);
            ObjectInputStream si = new ObjectInputStream(bi);
            return (SoccerGameplay) si.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
